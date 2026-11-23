package com.example.shooter.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.example.shooter.ecs.Components.TransformComponent
import com.example.shooter.ecs.Components.VelocityComponent
import com.example.shooter.ecs.Components.PlayerComponent
import com.example.shooter.ecs.Components.ShipType
import com.example.shooter.ecs.Components.WeaponComponent
import com.example.shooter.ecs.Components.UltimateComponent
import com.example.shooter.ecs.Components.UltimateType
import com.example.shooter.ecs.Components.HealthComponent
import com.example.shooter.ecs.Components.CollisionComponent
import com.example.shooter.ecs.Components.EnemyComponent
import com.example.shooter.ecs.Components.EnemyType
import com.example.shooter.ecs.Components.MovePattern
import com.example.shooter.ecs.Components.ShootPattern
import com.example.shooter.ecs.Components.ProjectileComponent
import com.example.shooter.ecs.Components.ProjectileOwner
import com.example.shooter.ecs.Components.PickupComponent
import com.example.shooter.ecs.Components.PickupType
import com.example.shooter.ecs.Components.ParticleComponent
import com.example.shooter.ecs.Components.StageComponent
import com.example.shooter.ecs.Components.ModelComponent
import com.example.shooter.screen.GameScreen

class GameWorld(
    private val game: com.example.shooter.ShooterGame,
    private val screenWidth: Float,
    private val screenHeight: Float
) {
    val engine = PooledEngine()
    val entityFactory: EntityFactory
    var playerEntity: Entity? = null
    var currentStage = 1
    var maxStage = 5
    var isGameOver = false
    var isStageClear = false
    var stageTimer = 0f
    var waveIndex = 0
    var enemiesInWave = 0
    var bossSpawned = false

    // Callbacks for UI
    var onScoreChange: ((Long) -> Unit)? = null
    var onLivesChange: ((Int) -> Unit)? = null
    var onWeaponLevelChange: ((Int) -> Unit)? = null
    var onUltimateMeterChange: ((Float, Float) -> Unit)? = null
    var onStageChange: ((Int, Int) -> Unit)? = null
    var onGameOver: (() -> Unit)? = null
    var onStageClear: (() -> Unit)? = null
    var onUltimateStart: ((UltimateType) -> Unit)? = null
    var onUltimateEnd: (() -> Unit)? = null

    init {
        entityFactory = EntityFactory(engine, game.assetManager)
        setupSystems()
        spawnPlayer()
        startStage(1)
    }

    private lateinit var weaponSystem: WeaponSystem
    private lateinit var enemyAISystem: EnemyAISystem
    private lateinit var bossAISystem: BossAISystem

    private fun setupSystems() {
        // Movement systems
        val movementSystem = MovementSystem(0)
        engine.addSystem(movementSystem)

        val playerMovementSystem = PlayerMovementSystem(1).apply {
            setScreenBounds(screenWidth, screenHeight)
        }
        engine.addSystem(playerMovementSystem)

        // Weapon system
        weaponSystem = WeaponSystem(2).apply {
            setEntityFactory(entityFactory)
        }
        engine.addSystem(weaponSystem)

        // Enemy AI
        enemyAISystem = EnemyAISystem(3).apply {
            setEntityFactory(entityFactory)
        }
        engine.addSystem(enemyAISystem)

        // Boss AI
        bossAISystem = BossAISystem(4).apply {
            setEntityFactory(entityFactory)
        }
        engine.addSystem(bossAISystem)

        // Projectile
        val projectileSystem = ProjectileSystem(5).apply {
            setEntityFactory(entityFactory)
        }
        engine.addSystem(projectileSystem)

        // Pickup
        val pickupSystem = PickupSystem(6)
        engine.addSystem(pickupSystem)

        // Ultimate
        val ultimateSystem = UltimateSystem(7).apply {
            setEntityFactory(entityFactory)
            setGameWorld(this@GameWorld)
        }
        engine.addSystem(ultimateSystem)

        // Collision
        val collisionSystem = CollisionSystem(10).apply {
            setEntityFactory(entityFactory)
            setGameWorld(this@GameWorld)
        }
        engine.addSystem(collisionSystem)

        // Cleanup
        val cleanupSystem = CleanupSystem(100).apply {
            setEntityFactory(entityFactory)
        }
        engine.addSystem(cleanupSystem)
    }

    private fun spawnPlayer() {
        // Default to balanced, will be changed by ship select
        playerEntity = entityFactory.createPlayer(ShipType.BALANCED, screenWidth / 2, 150f)
        weaponSystem.setPlayerEntity(playerEntity!!)
        enemyAISystem.setPlayerEntity(playerEntity!!)
        bossAISystem.setPlayerEntity(playerEntity!!)
    }

    fun setPlayerShipType(shipType: ShipType) {
        if (playerEntity != null) {
            val player = playerEntity!!.getComponent(PlayerComponent::class.java)
            player.shipType = shipType
            val ultimate = playerEntity!!.getComponent(UltimateComponent::class.java)
            ultimate.type = when (shipType) {
                ShipType.INTERCEPTOR -> UltimateType.INTERCEPTOR_BARRAGE
                ShipType.HEAVY -> UltimateType.HEAVY_NOVA
                else -> UltimateType.BALANCED_HOMING_SWARM
            }
            // Reconfigure weapon
            val weapon = playerEntity!!.getComponent(WeaponComponent::class.java)
            entityFactory.configureWeapon(weapon, shipType, player.weaponLevel)
        }
    }

    fun update(deltaTime: Float) {
        if (isGameOver || isStageClear) return

        stageTimer += deltaTime
        engine.update(deltaTime)
        entityFactory.cleanup()

        // Update weapon system player reference
        weaponSystem.setPlayerEntity(playerEntity!!)
        enemyAISystem.setPlayerEntity(playerEntity!!)
        bossAISystem.setPlayerEntity(playerEntity!!)

        // Stage progression
        updateStage(deltaTime)
    }

    private fun updateStage(deltaTime: Float) {
        val stageComp = engine.getEntitiesFor(
            com.badlogic.ashley.core.Family.all(StageComponent::class.java).get()
        ).firstOrNull()?.getComponent(StageComponent::class.java)

        if (stageComp == null) return

        if (stageComp.isBossStage) {
            // Check if boss is dead
            val bossEntity = entityFactory.entityIdMap.entries.firstOrNull { it.value == stageComp.bossEntityId }?.key
            if (bossEntity == null || bossEntity.getComponent(HealthComponent::class.java)?.isDead == true) {
                stageClear()
            }
        } else {
            // Check wave completion
            val enemies = engine.getEntitiesFor(
                com.badlogic.ashley.core.Family.all(EnemyComponent::class.java).get()
            )
            stageComp.enemiesRemaining = enemies.size()

            if (enemies.size() == 0 && enemiesInWave == 0) {
                spawnNextWave()
            }
        }
    }

    private fun startStage(stage: Int) {
        currentStage = stage
        isStageClear = false
        waveIndex = 0
        enemiesInWave = 0
        bossSpawned = false

        // Add stage component
        val stageEntity = engine.createEntity()
        val stageComp = engine.createComponent(StageComponent::class.java).apply {
            currentStage = stage
            maxStage = maxStage
            isBossStage = false
        }
        stageEntity.add(stageComp)
        engine.addEntity(stageEntity)

        onStageChange?.invoke(stage, maxStage)

        if (stage <= 3) {
            spawnNextWave()
        } else {
            // Boss stage
            spawnBoss()
        }
    }

    private fun spawnNextWave() {
        waveIndex++
        enemiesInWave = 0

        val wavesPerStage = 3
        if (waveIndex > wavesPerStage) {
            // Stage complete, spawn boss or next stage
            if (currentStage < maxStage) {
                spawnBoss()
            } else {
                stageClear()
            }
            return
        }

        // Spawn enemies based on wave and stage
        val enemyCount = 3 + waveIndex + currentStage
        for (i in 0 until enemyCount) {
            val x = MathUtils.random(80f, screenWidth - 80f)
            val y = screenHeight + 50f + i * 100f

            val type = if (MathUtils.randomBoolean(0.6f)) EnemyType.STRAIGHT_SHOOTER else EnemyType.SPREAD_SHOOTER
            entityFactory.createEnemy(type, x, y, currentStage)
            enemiesInWave++
        }
    }

    private fun spawnBoss() {
        bossSpawned = true
        val x = screenWidth / 2
        val y = screenHeight + 200f

        val boss = entityFactory.createEnemy(EnemyType.BOSS, x, y, currentStage)

        val stageComp = engine.getEntitiesFor(
            com.badlogic.ashley.core.Family.all(StageComponent::class.java).get()
        ).firstOrNull()?.getComponent(StageComponent::class.java)

        stageComp?.isBossStage = true
        stageComp?.bossEntityId = entityFactory.entityIdMap[boss] ?: -1
    }

    private fun stageClear() {
        isStageClear = true
        if (currentStage < maxStage) {
            currentStage++
        }
        onStageClear?.invoke()
    }

    fun nextStage() {
        startStage(currentStage)
    }

    fun addScore(points: Int) {
        val player = playerEntity?.getComponent(PlayerComponent::class.java)
        if (player != null) {
            player.score += points
            onScoreChange?.invoke(player.score)
        }
    }

    fun updateLives(lives: Int) {
        onLivesChange?.invoke(lives)
    }

    fun updateWeaponLevel(level: Int) {
        onWeaponLevelChange?.invoke(level)
        // Update weapon component
        val weapon = playerEntity?.getComponent(WeaponComponent::class.java)
        val player = playerEntity?.getComponent(PlayerComponent::class.java)
        if (weapon != null && player != null) {
            entityFactory.configureWeapon(weapon, player.shipType, level)
        }
    }

    fun updateUltimateMeter(current: Float, max: Float) {
        onUltimateMeterChange?.invoke(current, max)
    }

    fun onUltimateStart(type: UltimateType) {
        onUltimateStart?.invoke(type)
    }

    fun onUltimateEnd() {
        onUltimateEnd?.invoke()
    }

    fun gameOver() {
        isGameOver = true
        onGameOver?.invoke()
    }

    fun tryActivateUltimate(): Boolean {
        val ultimateSystem = engine.getSystem(UltimateSystem::class.java)
        return ultimateSystem?.tryActivateUltimate(playerEntity!!) ?: false
    }

    private fun handleInput(deltaTime: Float) {
        val player = playerEntity?.getComponent(PlayerComponent::class.java)
        val velocity = playerEntity?.getComponent(VelocityComponent::class.java)
        val transform = playerEntity?.getComponent(TransformComponent::class.java)

        if (player == null || velocity == null || transform == null) return

        // Touch/mouse movement
        if (Gdx.input.isTouched()) {
            val touchX = Gdx.input.getX()
            val touchY = screenHeight - Gdx.input.getY() // Flip Y

            val dx = touchX - transform.position.x
            val dy = touchY - transform.position.y
            val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            if (dist > 10f) {
                val speed = velocity.maxSpeed
                velocity.velocity.x = (dx / dist) * speed
                velocity.velocity.y = (dy / dist) * speed
            } else {
                velocity.velocity.setZero()
            }
        } else {
            velocity.velocity.setZero()
        }

        // Keyboard (for desktop testing)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) velocity.velocity.x = -velocity.maxSpeed
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) velocity.velocity.x = velocity.maxSpeed
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) velocity.velocity.y = velocity.maxSpeed
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) velocity.velocity.y = -velocity.maxSpeed

        // Ultimate activation (Space or U key)
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.U)) {
            tryActivateUltimate()
        }
    }

    fun getCamera(): Camera {
        return OrthographicCamera(screenWidth, screenHeight).apply {
            position.set(screenWidth / 2, screenHeight / 2, 0f)
            update()
        }
    }

    fun dispose() {
        engine.removeAllEntities()
        engine.clearPools()
    }
}