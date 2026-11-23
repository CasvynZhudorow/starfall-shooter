package com.example.shooter.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEmitter
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
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
import com.example.shooter.ecs.Components.ModelComponent
import com.example.shooter.ecs.Components.ScreenShakeComponent
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
import com.example.shooter.util.ModelGenerator

class EntityFactory(private val engine: PooledEngine, private val assetManager: AssetManager) {
    private val entitiesToRemove = Array<Entity>()
    private var nextEntityId = 0
    internal val entityIdMap = mutableMapOf<Entity, Int>()
    
    fun createPlayer(shipType: ShipType = ShipType.BALANCED, x: Float = 400f, y: Float = 200f): Entity {
        val entity = engine.createEntity()
        val id = assignId(entity)
        
        // Transform
        val transform = engine.createComponent(TransformComponent::class.java).apply {
            position.set(x, y)
            zIndex = 10f
        }
        
        // Velocity
        val velocity = engine.createComponent(VelocityComponent::class.java).apply {
            maxSpeed = when (shipType) {
                ShipType.INTERCEPTOR -> 600f
                ShipType.HEAVY -> 300f
                else -> 450f
            }
        }
        
        // Player
        val player = engine.createComponent(PlayerComponent::class.java).apply {
            this.shipType = shipType
            lives = 3
            weaponLevel = 1
            ultimateMeter = 0f
        }
        
        // Weapon - configure based on ship type
        val weapon = engine.createComponent(WeaponComponent::class.java)
        configureWeapon(weapon, shipType, 1)
        
        // Ultimate
        val ultimate = engine.createComponent(UltimateComponent::class.java).apply {
            type = when (shipType) {
                ShipType.INTERCEPTOR -> UltimateType.INTERCEPTOR_BARRAGE
                ShipType.HEAVY -> UltimateType.HEAVY_NOVA
                else -> UltimateType.BALANCED_HOMING_SWARM
            }
        }
        
        // Health
        val health = engine.createComponent(HealthComponent::class.java).apply {
            currentHp = 1
            maxHp = 1
        }
        
        // Collision
        val collision = engine.createComponent(CollisionComponent::class.java).apply {
            radius = 16f
            collisionLayer = 0x1 // Player layer
            collisionMask = 0x2 | 0x4 | 0x8 // Enemy, EnemyProjectile, Pickup
        }
        
        // Model
        val model = engine.createComponent(ModelComponent::class.java)
        loadPlayerModel(model, shipType)
        
        // Screen shake
        val shake = engine.createComponent(ScreenShakeComponent::class.java)
        
        entity.add(transform)
        entity.add(velocity)
        entity.add(player)
        entity.add(weapon)
        entity.add(ultimate)
        entity.add(health)
        entity.add(collision)
        entity.add(model)
        entity.add(shake)
        
        engine.addEntity(entity)
        return entity
    }
    
    internal fun configureWeapon(weapon: WeaponComponent, shipType: ShipType, level: Int) {
        when (shipType) {
            ShipType.INTERCEPTOR -> {
                weapon.fireRate = 0.08f / level
                weapon.damage = 8 * level
                weapon.projectileSpeed = 1000f
                weapon.projectileCount = 1 + (level - 1) / 2
                weapon.spreadAngle = if (level >= 3) 15f else 0f
                weapon.isPiercing = level >= 4
            }
            ShipType.HEAVY -> {
                weapon.fireRate = 0.25f / level
                weapon.damage = 20 * level
                weapon.projectileSpeed = 600f
                weapon.projectileCount = 1 + (level - 1)
                weapon.spreadAngle = 20f + (level - 1) * 5f
            }
            else -> { // BALANCED
                weapon.fireRate = 0.15f / level
                weapon.damage = 12 * level
                weapon.projectileSpeed = 800f
                weapon.projectileCount = 1 + (level - 1) / 2
                weapon.spreadAngle = if (level >= 2) 10f else 0f
                weapon.isHoming = level >= 5
            }
        }
    }
    
    private fun loadPlayerModel(modelComp: ModelComponent, shipType: ShipType) {
        val modelName = when (shipType) {
            ShipType.INTERCEPTOR -> "models/ship_interceptor.g3db"
            ShipType.HEAVY -> "models/ship_heavy.g3db"
            else -> "models/ship_balanced.g3db"
        }
        
        if (assetManager.isLoaded(modelName)) {
            val model = assetManager.get(modelName, Model::class.java)
            modelComp.modelInstance = ModelInstance(model)
            modelComp.modelInstance?.transform?.setToTranslation(0f, 0f, 0f)
        } else {
            // Use placeholder model
            modelComp.modelInstance = ModelInstance(ModelGenerator.getPlayerModel(shipType))
            modelComp.modelInstance?.transform?.setToTranslation(0f, 0f, 0f)
        }
    }
    
    fun createEnemy(type: EnemyType, x: Float, y: Float, stage: Int = 1): Entity {
        val entity = engine.createEntity()
        assignId(entity)
        
        val transform = engine.createComponent(TransformComponent::class.java).apply {
            position.set(x, y)
            zIndex = 5f
        }
        
        val velocity = engine.createComponent(VelocityComponent::class.java)
        
        val enemy = engine.createComponent(EnemyComponent::class.java).apply {
            this.type = type
        }
        
        val health = engine.createComponent(HealthComponent::class.java)
        val collision = engine.createComponent(CollisionComponent::class.java).apply {
            collisionLayer = 0x2 // Enemy layer
            collisionMask = 0x1 | 0x4 // Player, PlayerProjectile
        }
        
        val model = engine.createComponent(ModelComponent::class.java)
        
        // Configure by type
        when (type) {
            EnemyType.STRAIGHT_SHOOTER -> {
                enemy.maxHp = 15 + stage * 5
                enemy.hp = enemy.maxHp
                enemy.scoreValue = 100 + stage * 20
                enemy.movePattern = MovePattern.STRAIGHT
                enemy.shootPattern = ShootPattern.STRAIGHT
                enemy.shootInterval = 2.5f - stage * 0.1f
                velocity.maxSpeed = 200f
                collision.radius = 20f
                loadEnemyModel(model, "enemy_straight")
            }
            EnemyType.SPREAD_SHOOTER -> {
                enemy.maxHp = 25 + stage * 8
                enemy.hp = enemy.maxHp
                enemy.scoreValue = 200 + stage * 30
                enemy.movePattern = MovePattern.SINE_WAVE
                enemy.shootPattern = ShootPattern.SPREAD_3
                enemy.shootInterval = 2f - stage * 0.1f
                velocity.maxSpeed = 180f
                collision.radius = 24f
                loadEnemyModel(model, "enemy_spread")
            }
            EnemyType.BOSS -> {
                enemy.maxHp = 500 + stage * 200
                enemy.hp = enemy.maxHp
                enemy.scoreValue = 5000 + stage * 1000
                enemy.movePattern = MovePattern.BOSS_PHASE_1
                enemy.shootPattern = ShootPattern.BOSS_PATTERN_1
                enemy.shootInterval = 1.5f
                velocity.maxSpeed = 200f
                collision.radius = 80f
                loadBossModel(model, stage)
            }
        }
        
        health.currentHp = enemy.maxHp
        health.maxHp = enemy.maxHp
        
        entity.add(transform)
        entity.add(velocity)
        entity.add(enemy)
        entity.add(health)
        entity.add(collision)
        entity.add(model)
        
        engine.addEntity(entity)
        return entity
    }
    
    private fun loadEnemyModel(modelComp: ModelComponent, modelName: String) {
        val fullName = "models/$modelName.g3db"
        if (assetManager.isLoaded(fullName)) {
            val model = assetManager.get(fullName, Model::class.java)
            modelComp.modelInstance = ModelInstance(model)
        } else {
            // Use placeholder model
            modelComp.modelInstance = ModelInstance(ModelGenerator.createStraightEnemyModel())
        }
    }
    
    fun createProjectile(
        x: Float, y: Float,
        vx: Float, vy: Float,
        damage: Int,
        owner: ProjectileOwner,
        isPiercing: Boolean = false,
        isHoming: Boolean = false
    ): Entity {
        val entity = engine.createEntity()
        assignId(entity)
        
        val transform = engine.createComponent(TransformComponent::class.java).apply {
            position.set(x, y)
            zIndex = 3f
        }
        
        val velocity = engine.createComponent(VelocityComponent::class.java).apply {
            velocity.set(vx, vy)
        }
        
        val projectile = engine.createComponent(ProjectileComponent::class.java).apply {
            this.owner = owner
            this.damage = damage
            this.isPiercing = isPiercing
            this.isHoming = isHoming
            this.maxLifeTime = 5f
            this.lifeTime = 5f
        }
        
        val collision = engine.createComponent(CollisionComponent::class.java).apply {
            radius = 6f
            collisionLayer = if (owner == ProjectileOwner.PLAYER) 0x4 else 0x8
            collisionMask = if (owner == ProjectileOwner.PLAYER) 0x2 else 0x1
        }
        
        val model = engine.createComponent(ModelComponent::class.java)
        loadProjectileModel(model, owner)
        
        entity.add(transform)
        entity.add(velocity)
        entity.add(projectile)
        entity.add(collision)
        entity.add(model)
        
        engine.addEntity(entity)
        return entity
    }
    
    private fun loadProjectileModel(modelComp: ModelComponent, owner: ProjectileOwner) {
        val modelName = if (owner == ProjectileOwner.PLAYER) "models/projectile_player.g3db" else "models/projectile_enemy.g3db"
        if (assetManager.isLoaded(modelName)) {
            val model = assetManager.get(modelName, Model::class.java)
            modelComp.modelInstance = ModelInstance(model)
        } else {
            // Use placeholder model
            modelComp.modelInstance = ModelInstance(
                if (owner == ProjectileOwner.PLAYER) 
                    ModelGenerator.createPlayerProjectileModel() 
                else 
                    ModelGenerator.createEnemyProjectileModel()
            )
        }
    }
    
    fun createPickup(x: Float, y: Float): Entity {
        val entity = engine.createEntity()
        assignId(entity)
        
        val transform = engine.createComponent(TransformComponent::class.java).apply {
            position.set(x, y)
            zIndex = 2f
        }
        
        val pickup = engine.createComponent(PickupComponent::class.java).apply {
            // Random pickup type
            when (MathUtils.random(3)) {
                0 -> { type = PickupType.WEAPON_UPGRADE; value = 1 }
                1 -> { type = PickupType.ULTIMATE_CHARGE; value = 1 }
                2 -> { type = PickupType.HEALTH; value = 1 }
                else -> { type = PickupType.SCORE_BONUS; value = 1 }
            }
        }
        
        val collision = engine.createComponent(CollisionComponent::class.java).apply {
            radius = 18f
            collisionLayer = 0x10 // Pickup layer
            collisionMask = 0x1 // Player only
        }
        
        val model = engine.createComponent(ModelComponent::class.java)
        loadPickupModel(model, pickup.type)
        
        entity.add(transform)
        entity.add(pickup)
        entity.add(collision)
        entity.add(model)
        
        engine.addEntity(entity)
        return entity
    }
    
    private fun loadPickupModel(modelComp: ModelComponent, type: PickupType) {
        val modelName = when (type) {
            PickupType.WEAPON_UPGRADE -> "models/pickup_weapon.g3db"
            PickupType.ULTIMATE_CHARGE -> "models/pickup_ultimate.g3db"
            PickupType.HEALTH -> "models/pickup_health.g3db"
            PickupType.SCORE_BONUS -> "models/pickup_score.g3db"
        }
        if (assetManager.isLoaded(modelName)) {
            val model = assetManager.get(modelName, Model::class.java)
            modelComp.modelInstance = ModelInstance(model)
        } else {
            // Use placeholder model with type-specific color
            val (color, emissive) = when (type) {
                PickupType.WEAPON_UPGRADE -> Color.GREEN to Color.GREEN
                PickupType.ULTIMATE_CHARGE -> Color.GOLD to Color.GOLD
                PickupType.HEALTH -> Color.RED to Color.RED
                PickupType.SCORE_BONUS -> Color.WHITE to Color.CYAN
            }
            modelComp.modelInstance = ModelInstance(ModelGenerator.createPickupModel(color, emissive))
        }
    }
    
    private fun loadBossModel(modelComp: ModelComponent, stage: Int) {
        val model = when (stage) {
            1 -> ModelGenerator.createBossStage1Model()
            2 -> ModelGenerator.createBossStage2Model()
            3 -> ModelGenerator.createBossStage3Model()
            else -> ModelGenerator.createBossStage1Model()
        }
        
        if (assetManager.isLoaded("models/boss_stage$stage.g3db")) {
            val bossModel = assetManager.get("models/boss_stage$stage.g3db", Model::class.java)
            modelComp.modelInstance = ModelInstance(bossModel)
        } else {
            modelComp.modelInstance = ModelInstance(model)
        }
    }
    
    fun createNovaExplosion(x: Float, y: Float, radius: Float, damage: Int) {
        // Create explosion entity with particle effect
        val entity = engine.createEntity()
        assignId(entity)
        
        val transform = engine.createComponent(TransformComponent::class.java).apply {
            position.set(x, y)
            zIndex = 20f
        }
        
        val particle = engine.createComponent(ParticleComponent::class.java).apply {
            // Load explosion particle
            if (assetManager.isLoaded("particles/explosion.p")) {
                val effect = assetManager.get("particles/explosion.p", ParticleEffect::class.java)
                emitter = effect.emitters.firstOrNull()
                isPlaying = true
            } else {
                val effect = ParticleEffect()
                // Could load from file here if needed
                emitter = effect.emitters.firstOrNull()
                isPlaying = true
            }
        }
        
        entity.add(transform)
        entity.add(particle)
        engine.addEntity(entity)
        
        // Damage enemies in radius
        val entities = engine.getEntitiesFor(
            com.badlogic.ashley.core.Family.all(TransformComponent::class.java, EnemyComponent::class.java, HealthComponent::class.java).get()
        )
        
        for (i in 0 until entities.size()) {
            val e = entities.get(i)
            val t = e.getComponent(TransformComponent::class.java)
            val dx = t.position.x - x
            val dy = t.position.y - y
            if (dx * dx + dy * dy <= radius * radius) {
                val health = e.getComponent(HealthComponent::class.java)
                health.currentHp -= damage
                if (health.currentHp <= 0) {
                    health.isDead = true
                    health.deathTimer = 0.5f
                }
            }
        }
    }
    
    fun createHomingSwarm(x: Float, y: Float, count: Int, damage: Int) {
        // Find all enemies
        val enemies = engine.getEntitiesFor(
            com.badlogic.ashley.core.Family.all(TransformComponent::class.java, EnemyComponent::class.java).get()
        )
        
        if (enemies.size() == 0) return

        repeat(count) { i ->
            val target = enemies.get(MathUtils.random(enemies.size() - 1))
            val targetTransform = target.getComponent(TransformComponent::class.java)

            val projectile = createProjectile(x, y, 0f, -500f, damage, ProjectileOwner.PLAYER, false, true)
            val projComp = projectile.getComponent(ProjectileComponent::class.java)
            projComp.homingTarget = entityIdMap[target] ?: -1
        }
    }
    
    fun markForRemoval(entity: Entity) {
        entitiesToRemove.add(entity)
    }
    
    fun cleanup() {
        for (entity in entitiesToRemove) {
            if (engine.getEntities().contains(entity, true)) {
                engine.removeEntity(entity)
            }
        }
        entitiesToRemove.clear()
    }
    
    fun getEntity(id: Int): Entity? {
        return entityIdMap.entries.firstOrNull { it.value == id }?.key
    }
    
    private fun assignId(entity: Entity): Int {
        val id = nextEntityId++
        entityIdMap[entity] = id
        return id
    }
    
    fun removeEntityId(entity: Entity) {
        entityIdMap.remove(entity)
    }
}