package com.example.shooter.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.example.shooter.ecs.Components.*

class MovementSystem(priority: Int = 0) : IteratingSystem(
    Family.all(TransformComponent::class.java, VelocityComponent::class.java).get(),
    priority
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent(TransformComponent::class.java)
        val velocity = entity.getComponent(VelocityComponent::class.java)
        
        transform.position.add(velocity.velocity.x * deltaTime, velocity.velocity.y * deltaTime)
    }
}

class PlayerMovementSystem(priority: Int = 1) : IteratingSystem(
    Family.all(TransformComponent::class.java, VelocityComponent::class.java, PlayerComponent::class.java).get(),
    priority
) {
    private val screenBounds = Vector2(800f, 1280f) // Will be set from game
    private val margin = 40f
    
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent(TransformComponent::class.java)
        val velocity = entity.getComponent(VelocityComponent::class.java)
        val player = entity.getComponent(PlayerComponent::class.java)
        
        // Clamp to screen bounds
        transform.position.x = MathUtils.clamp(transform.position.x, margin, screenBounds.x - margin)
        transform.position.y = MathUtils.clamp(transform.position.y, margin, screenBounds.y - margin)
        
        // Update invulnerability
        if (player.isInvulnerable) {
            player.invulnerabilityTimer -= deltaTime
            if (player.invulnerabilityTimer <= 0f) {
                player.isInvulnerable = false
            }
        }
        
        // Update ultimate cooldown
        if (player.ultimateCooldown > 0f) {
            player.ultimateCooldown -= deltaTime
        }
    }
    
    fun setScreenBounds(width: Float, height: Float) {
        screenBounds.set(width, height)
    }
}

class WeaponSystem(priority: Int = 2) : IteratingSystem(
    Family.all(TransformComponent::class.java, WeaponComponent::class.java).get(),
    priority
) {
    private var entityFactory: EntityFactory? = null
    private var playerEntity: Entity? = null
    
    fun setEntityFactory(factory: EntityFactory) {
        entityFactory = factory
    }
    
    fun setPlayerEntity(entity: Entity) {
        playerEntity = entity
    }
    
    override fun processEntity(entity: Entity, deltaTime: Float) {
            val weapon = entity.getComponent(WeaponComponent::class.java)
            val transform = entity.getComponent(TransformComponent::class.java)

            weapon.fireTimer -= deltaTime

            if (weapon.fireTimer <= 0f && entityFactory != null) {
                weapon.fireTimer = weapon.fireRate

                // Fire projectiles based on pattern
                when {
                    weapon.projectileCount == 1 && weapon.spreadAngle == 0f -> {
                        fireStraight(transform, weapon)
                    }
                    weapon.projectileCount > 1 -> {
                        fireSpread(transform, weapon)
                    }
                    weapon.isHoming -> {
                        fireHoming(transform, weapon)
                    }
                    else -> {}
                }
            }
        }
    
    private fun fireStraight(transform: TransformComponent, weapon: WeaponComponent) {
        entityFactory?.createProjectile(
            transform.position.x,
            transform.position.y,
            0f, -weapon.projectileSpeed,
            weapon.damage,
            ProjectileOwner.PLAYER,
            weapon.isPiercing,
            weapon.isHoming
        )
    }
    
    private fun fireSpread(transform: TransformComponent, weapon: WeaponComponent) {
        val angleStep = weapon.spreadAngle / (weapon.projectileCount - 1)
        val startAngle = -weapon.spreadAngle / 2
        
        repeat(weapon.projectileCount) { i ->
            val angle = MathUtils.degreesToRadians * (startAngle + i * angleStep)
            val vx = MathUtils.sin(angle) * weapon.projectileSpeed
            val vy = -MathUtils.cos(angle) * weapon.projectileSpeed
            
            entityFactory?.createProjectile(
                transform.position.x,
                transform.position.y,
                vx, vy,
                weapon.damage,
                ProjectileOwner.PLAYER,
                weapon.isPiercing,
                weapon.isHoming
            )
        }
    }
    
    private fun fireHoming(transform: TransformComponent, weapon: WeaponComponent) {
        entityFactory?.createProjectile(
            transform.position.x,
            transform.position.y,
            0f, -weapon.projectileSpeed,
            weapon.damage,
            ProjectileOwner.PLAYER,
            false,
            true
        )
    }
}

class EnemyAISystem(priority: Int = 3) : IteratingSystem(
    Family.all(TransformComponent::class.java, VelocityComponent::class.java, EnemyComponent::class.java).get(),
    priority
) {
    private var entityFactory: EntityFactory? = null
    private var playerEntity: Entity? = null
    private val screenHeight = 1280f
    
    fun setEntityFactory(factory: EntityFactory) {
        entityFactory = factory
    }
    
    fun setPlayerEntity(entity: Entity) {
        playerEntity = entity
    }
    
    override fun processEntity(entity: Entity, deltaTime: Float) {
            val transform = entity.getComponent(TransformComponent::class.java)
            val velocity = entity.getComponent(VelocityComponent::class.java)
            val enemy = entity.getComponent(EnemyComponent::class.java)

            // Movement patterns
            when (enemy.movePattern) {
                MovePattern.STRAIGHT -> {
                    velocity.velocity.y = -velocity.maxSpeed * 0.5f
                }
                MovePattern.SINE_WAVE -> {
                    velocity.velocity.y = -velocity.maxSpeed * 0.4f
                    velocity.velocity.x = MathUtils.sin(enemy.shootTimer * 3f) * velocity.maxSpeed * 0.6f
                }
                MovePattern.CIRCLE -> {
                    val angle = enemy.shootTimer * 2f
                    velocity.velocity.x = MathUtils.cos(angle) * velocity.maxSpeed * 0.5f
                    velocity.velocity.y = -velocity.maxSpeed * 0.3f + MathUtils.sin(angle) * velocity.maxSpeed * 0.2f
                }
                MovePattern.BOSS_PHASE_1, MovePattern.BOSS_PHASE_2, MovePattern.BOSS_PHASE_3 -> {}
            }
        
        // Shooting
        enemy.shootTimer += deltaTime
        if (enemy.shootTimer >= enemy.shootInterval && entityFactory != null) {
            enemy.shootTimer = 0f
            fireEnemyProjectile(transform, enemy)
        }
        
        // Despawn if off screen
        if (transform.position.y < -100f) {
            entityFactory?.markForRemoval(entity)
        }
    }
    
    private fun fireEnemyProjectile(transform: TransformComponent, enemy: EnemyComponent) {
            val playerPos = playerEntity?.getComponent(TransformComponent::class.java)?.position ?: Vector2(400f, 200f)
            val dx = playerPos.x - transform.position.x
            val dy = playerPos.y - transform.position.y
            val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            when (enemy.shootPattern) {
                ShootPattern.STRAIGHT -> {
                    entityFactory?.createProjectile(
                        transform.position.x, transform.position.y,
                        0f, -400f, 10, ProjectileOwner.ENEMY
                    )
                }
                ShootPattern.SPREAD_3 -> {
                    val angles = floatArrayOf(-0.3f, 0f, 0.3f)
                    angles.forEach { angle ->
                        val vx = MathUtils.sin(angle) * 400f
                        val vy = -MathUtils.cos(angle) * 400f
                        entityFactory?.createProjectile(transform.position.x, transform.position.y, vx, vy, 10, ProjectileOwner.ENEMY)
                    }
                }
                ShootPattern.SPREAD_5 -> {
                    val angles = floatArrayOf(-0.5f, -0.25f, 0f, 0.25f, 0.5f)
                    angles.forEach { angle ->
                        val vx = MathUtils.sin(angle) * 400f
                        val vy = -MathUtils.cos(angle) * 400f
                        entityFactory?.createProjectile(transform.position.x, transform.position.y, vx, vy, 10, ProjectileOwner.ENEMY)
                    }
                }
                ShootPattern.TARGETED -> {
                    if (dist > 0) {
                        val vx = (dx / dist) * 400f
                        val vy = (dy / dist) * 400f
                        entityFactory?.createProjectile(transform.position.x, transform.position.y, vx, vy, 15, ProjectileOwner.ENEMY)
                    }
                }
                else -> {}
            }
        }
}

class BossAISystem(priority: Int = 4) : IteratingSystem(
    Family.all(TransformComponent::class.java, VelocityComponent::class.java, EnemyComponent::class.java).get(),
    priority
) {
    private var entityFactory: EntityFactory? = null
    private var playerEntity: Entity? = null
    private var gameWorld: GameWorld? = null
    
    fun setEntityFactory(factory: EntityFactory) {
        entityFactory = factory
    }
    
    fun setPlayerEntity(entity: Entity) {
        playerEntity = entity
    }
    
    fun setGameWorld(world: GameWorld) {
        gameWorld = world
    }
    
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent(TransformComponent::class.java)
        val velocity = entity.getComponent(VelocityComponent::class.java)
        val enemy = entity.getComponent(EnemyComponent::class.java)
        
        if (enemy.type != EnemyType.BOSS) return
        
        // Boss phase management
        val hpPercent = enemy.hp.toFloat() / enemy.maxHp
        
        when {
            hpPercent > 0.66f && enemy.phase != 1 -> enterPhase1(enemy)
            hpPercent > 0.33f && hpPercent <= 0.66f && enemy.phase != 2 -> enterPhase2(enemy)
            hpPercent <= 0.33f && enemy.phase != 3 -> enterPhase3(enemy)
        }
        
        // Phase behavior
        when (enemy.phase) {
            1 -> phase1Behavior(transform, velocity, enemy, deltaTime)
            2 -> phase2Behavior(transform, velocity, enemy, deltaTime)
            3 -> phase3Behavior(transform, velocity, enemy, deltaTime)
            else -> {}
        }
        
        // Shooting
        enemy.shootTimer += deltaTime
        if (enemy.shootTimer >= enemy.shootInterval && entityFactory != null) {
            enemy.shootTimer = 0f
            fireBossProjectile(transform, enemy)
        }
    }
    
    private fun enterPhase1(enemy: EnemyComponent) {
        enemy.phase = 1
        enemy.movePattern = MovePattern.BOSS_PHASE_1
        enemy.shootPattern = ShootPattern.BOSS_PATTERN_1
        enemy.shootInterval = 1.5f
    }
    
    private fun enterPhase2(enemy: EnemyComponent) {
        enemy.phase = 2
        enemy.movePattern = MovePattern.BOSS_PHASE_2
        enemy.shootPattern = ShootPattern.BOSS_PATTERN_2
        enemy.shootInterval = 1f
    }
    
    private fun enterPhase3(enemy: EnemyComponent) {
        enemy.phase = 3
        enemy.movePattern = MovePattern.BOSS_PHASE_3
        enemy.shootPattern = ShootPattern.BOSS_PATTERN_3
        enemy.shootInterval = 0.5f
    }
    
    private fun phase1Behavior(transform: TransformComponent, velocity: VelocityComponent, enemy: EnemyComponent, deltaTime: Float) {
        // Slow horizontal sweep
        velocity.velocity.x = MathUtils.sin(enemy.phaseTimer) * 150f
        velocity.velocity.y = 0f
        enemy.phaseTimer += deltaTime
    }
    
    private fun phase2Behavior(transform: TransformComponent, velocity: VelocityComponent, enemy: EnemyComponent, deltaTime: Float) {
        // Figure-8 pattern
        val t = enemy.phaseTimer * 2f
        velocity.velocity.x = MathUtils.sin(t) * 200f
        velocity.velocity.y = MathUtils.sin(t * 2f) * 100f
        enemy.phaseTimer += deltaTime
    }
    
    private fun phase3Behavior(transform: TransformComponent, velocity: VelocityComponent, enemy: EnemyComponent, deltaTime: Float) {
            // Aggressive targeting
            val playerPos = playerEntity?.getComponent(TransformComponent::class.java)?.position ?: Vector2(400f, 200f)
            val dx = playerPos.x - transform.position.x
            val dy = playerPos.y - transform.position.y
            val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            if (dist > 0) {
                velocity.velocity.x = (dx / dist) * 180f
                velocity.velocity.y = (dy / dist) * 180f
            }
            enemy.phaseTimer += deltaTime
        }
    
    private fun fireBossProjectile(transform: TransformComponent, enemy: EnemyComponent) {
            when (enemy.shootPattern) {
                ShootPattern.BOSS_PATTERN_1 -> {
                    // 3-way spread aimed at player
                    val playerPos = playerEntity?.getComponent(TransformComponent::class.java)?.position ?: Vector2(400f, 200f)
                    val dx = playerPos.x - transform.position.x
                    val dy = playerPos.y - transform.position.y
                    val baseAngle = MathUtils.atan2(dx, -dy)

                    floatArrayOf(-0.4f, 0f, 0.4f).forEach { offset ->
                        val angle = baseAngle + offset
                        val vx = MathUtils.sin(angle) * 350f
                        val vy = -MathUtils.cos(angle) * 350f
                        entityFactory?.createProjectile(transform.position.x, transform.position.y, vx, vy, 20, ProjectileOwner.ENEMY)
                    }
                }
                ShootPattern.BOSS_PATTERN_2 -> {
                    // Circle burst
                    repeat(12) { i ->
                        val angle = (i * 30f) * MathUtils.degreesToRadians
                        val vx = MathUtils.sin(angle) * 300f
                        val vy = -MathUtils.cos(angle) * 300f
                        entityFactory?.createProjectile(transform.position.x, transform.position.y, vx, vy, 15, ProjectileOwner.ENEMY)
                    }
                }
                ShootPattern.BOSS_PATTERN_3 -> {
                    // Targeted + circle
                    val playerPos = playerEntity?.getComponent(TransformComponent::class.java)?.position ?: Vector2(400f, 200f)
                    val dx = playerPos.x - transform.position.x
                    val dy = playerPos.y - transform.position.y
                    val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                    if (dist > 0) {
                        val vx = (dx / dist) * 400f
                        val vy = (dy / dist) * 400f
                        entityFactory?.createProjectile(transform.position.x, transform.position.y, vx, vy, 25, ProjectileOwner.ENEMY)
                    }

                    repeat(8) { i ->
                        val angle = (i * 45f) * MathUtils.degreesToRadians
                        val vx = MathUtils.sin(angle) * 250f
                        val vy = -MathUtils.cos(angle) * 250f
                        entityFactory?.createProjectile(transform.position.x, transform.position.y, vx, vy, 15, ProjectileOwner.ENEMY)
                    }
                }
                else -> {}
            }
        }
}

class ProjectileSystem(priority: Int = 5) : IteratingSystem(
    Family.all(TransformComponent::class.java, VelocityComponent::class.java, ProjectileComponent::class.java).get(),
    priority
) {
    private var entityFactory: EntityFactory? = null
    private val screenBounds = Vector2(800f, 1400f)
    
    fun setEntityFactory(factory: EntityFactory) {
        entityFactory = factory
    }
    
    override fun processEntity(entity: Entity, deltaTime: Float) {
            val transform = entity.getComponent(TransformComponent::class.java)
            val velocity = entity.getComponent(VelocityComponent::class.java)
            val projectile = entity.getComponent(ProjectileComponent::class.java)

            projectile.lifeTime -= deltaTime

            // Homing logic
            if (projectile.isHoming && projectile.homingTarget >= 0 && entityFactory != null) {
                val target = entityFactory.getEntity(projectile.homingTarget)
                if (target != null) {
                    val targetTransform = target.getComponent(TransformComponent::class.java)
                    val dx = targetTransform.position.x - transform.position.x
                    val dy = targetTransform.position.y - transform.position.y
                    val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                    if (dist > 0) {
                        val speed = velocity.velocity.len()
                        velocity.velocity.x = (dx / dist) * speed
                        velocity.velocity.y = (dy / dist) * speed
                    }
                } else {
                    projectile.isHoming = false
                    projectile.homingTarget = -1
                }
            }

            // Despawn if off screen or lifetime expired
            if (projectile.lifeTime <= 0f ||
                transform.position.x < -50f || transform.position.x > screenBounds.x + 50f ||
                transform.position.y < -50f || transform.position.y > screenBounds.y + 50f) {
                entityFactory?.markForRemoval(entity)
            }
        }
}

class CollisionSystem(priority: Int = 10) : IteratingSystem(
    Family.all(TransformComponent::class.java, CollisionComponent::class.java).get(),
    priority
) {
    private var entityFactory: EntityFactory? = null
    private var gameWorld: GameWorld? = null
    
    fun setEntityFactory(factory: EntityFactory) {
        entityFactory = factory
    }
    
    fun setGameWorld(world: GameWorld) {
        gameWorld = world
    }
    
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformA = entity.getComponent(TransformComponent::class.java)
        val collisionA = entity.getComponent(CollisionComponent::class.java)
        
        // Check against all other entities with collision
        val entities = entityFactory?.engine?.getEntitiesFor(
            Family.all(TransformComponent::class.java, CollisionComponent::class.java).get()
        )
        
        if (entities == null) return
        
        for (i in 0 until entities.size) {
            val entityB = entities.get(i)
            if (entityB == entity) continue
            
            val collisionB = entityB.getComponent(CollisionComponent::class.java)
            
            // Check layer/mask
            if ((collisionA.collisionLayer and collisionB.collisionMask) == 0) continue
            if ((collisionB.collisionLayer and collisionA.collisionMask) == 0) continue
            
            val transformB = entityB.getComponent(TransformComponent::class.java)
            val dx = transformA.position.x - transformB.position.x
            val dy = transformA.position.y - transformB.position.y
            val distSq = dx * dx + dy * dy
            val radiusSum = collisionA.radius + collisionB.radius
            
            if (distSq <= radiusSum * radiusSum) {
                handleCollision(entity, entityB)
            }
        }
    }
    
    private fun handleCollision(entityA: Entity, entityB: Entity) {
        val projectileA = entityA.getComponent(ProjectileComponent::class.java)
        val projectileB = entityB.getComponent(ProjectileComponent::class.java)
        val enemyA = entityA.getComponent(EnemyComponent::class.java)
        val enemyB = entityB.getComponent(EnemyComponent::class.java)
        val playerA = entityA.getComponent(PlayerComponent::class.java)
        val playerB = entityB.getComponent(PlayerComponent::class.java)
        val pickupA = entityA.getComponent(PickupComponent::class.java)
        val pickupB = entityB.getComponent(PickupComponent::class.java)
        val healthA = entityA.getComponent(HealthComponent::class.java)
        val healthB = entityB.getComponent(HealthComponent::class.java)
        
        // Player projectile hits enemy
        if (projectileA != null && projectileA.owner == ProjectileOwner.PLAYER && (enemyB != null || healthB != null)) {
            damageEntity(entityB, projectileA.damage)
            if (!projectileA.isPiercing) {
                entityFactory?.markForRemoval(entityA)
            }
        } else if (projectileB != null && projectileB.owner == ProjectileOwner.PLAYER && (enemyA != null || healthA != null)) {
            damageEntity(entityA, projectileB.damage)
            if (!projectileB.isPiercing) {
                entityFactory?.markForRemoval(entityB)
            }
        }
        
        // Enemy projectile hits player
        else if (projectileA != null && projectileA.owner == ProjectileOwner.ENEMY && playerB != null) {
            damagePlayer(entityB, projectileA.damage)
            entityFactory?.markForRemoval(entityA)
        } else if (projectileB != null && projectileB.owner == ProjectileOwner.ENEMY && playerA != null) {
            damagePlayer(entityA, projectileB.damage)
            entityFactory?.markForRemoval(entityB)
        }
        
        // Player picks up item
        else if (playerA != null && pickupB != null) {
            applyPickup(entityA, pickupB)
            entityFactory?.markForRemoval(entityB)
        } else if (playerB != null && pickupA != null) {
            applyPickup(entityB, pickupA)
            entityFactory?.markForRemoval(entityA)
        }
        
        // Player collides with enemy (body collision)
        else if (playerA != null && enemyB != null) {
            damagePlayer(entityA, 10)
        } else if (playerB != null && enemyA != null) {
            damagePlayer(entityB, 10)
        }
    }
    
    private fun damageEntity(entity: Entity, damage: Int) {
        val health = entity.getComponent(HealthComponent::class.java)
        if (health != null) {
            health.currentHp -= damage
            if (health.currentHp <= 0 && !health.isDead) {
                health.isDead = true
                health.deathTimer = health.deathDuration
                
                // Score
                val enemy = entity.getComponent(EnemyComponent::class.java)
                if (enemy != null) {
                    gameWorld?.addScore(enemy.scoreValue)
                }
                
                // Drop pickup chance
                if (MathUtils.randomBoolean(0.15f)) {
                    entityFactory?.createPickup(
                        entity.getComponent(TransformComponent::class.java).position.x,
                        entity.getComponent(TransformComponent::class.java).position.y
                    )
                }
            }
        }
    }
    
    private fun damagePlayer(entity: Entity, damage: Int) {
        val player = entity.getComponent(PlayerComponent::class.java)
        if (player.isInvulnerable) return
        
        player.lives--
        player.isInvulnerable = true
        player.invulnerabilityTimer = 2f
        
        // Screen shake
        val shake = entity.getComponent(ScreenShakeComponent::class.java)
        if (shake != null) {
            shake.intensity = 15f
            shake.duration = 0.3f
            shake.timer = 0.3f
        }
        
        // Weapon level down
        if (player.weaponLevel > 1) {
            player.weaponLevel--
        }
        
        gameWorld?.updateLives(player.lives)
        
        if (player.lives <= 0) {
            gameWorld?.gameOver()
        }
    }
    
    private fun applyPickup(playerEntity: Entity, pickup: PickupComponent) {
        val player = playerEntity.getComponent(PlayerComponent::class.java)
        
        when (pickup.type) {
            PickupType.WEAPON_UPGRADE -> {
                player.weaponLevel = MathUtils.clamp(player.weaponLevel + pickup.value, 1, player.maxWeaponLevel)
                gameWorld?.updateWeaponLevel(player.weaponLevel)
            }
            PickupType.ULTIMATE_CHARGE -> {
                player.ultimateMeter = MathUtils.clamp(player.ultimateMeter + pickup.value * 10f, 0f, player.maxUltimateMeter)
                gameWorld?.updateUltimateMeter(player.ultimateMeter, player.maxUltimateMeter)
            }
            PickupType.HEALTH -> {
                player.lives = MathUtils.clamp(player.lives + pickup.value, 0, 5)
                gameWorld?.updateLives(player.lives)
            }
            PickupType.SCORE_BONUS -> {
                player.score += pickup.value * 1000L
                gameWorld?.addScore(pickup.value * 1000)
            }
        }
    }
}

class PickupSystem(priority: Int = 6) : IteratingSystem(
    Family.all(TransformComponent::class.java, PickupComponent::class.java).get(),
    priority
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent(TransformComponent::class.java)
        val pickup = entity.getComponent(PickupComponent::class.java)
        
        // Bob animation
        pickup.bobTimer += deltaTime
        transform.position.y += MathUtils.sin(pickup.bobTimer * pickup.bobFrequency) * pickup.bobAmplitude * deltaTime
        
        // Auto-move down slowly
        transform.position.y -= 50f * deltaTime
        
        // Despawn if off screen
        if (transform.position.y < -50f) {
            // Will be removed by entity factory cleanup
        }
    }
}

class UltimateSystem(priority: Int = 7) : IteratingSystem(
    Family.all(PlayerComponent::class.java, UltimateComponent::class.java).get(),
    priority
) {
    private var entityFactory: EntityFactory? = null
    private var gameWorld: GameWorld? = null
    
    fun setEntityFactory(factory: EntityFactory) {
        entityFactory = factory
    }
    
    fun setGameWorld(world: GameWorld) {
        gameWorld = world
    }
    
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val player = entity.getComponent(PlayerComponent::class.java)
        val ultimate = entity.getComponent(UltimateComponent::class.java)
        val transform = entity.getComponent(TransformComponent::class.java)
        
        // Update ultimate cooldown
        if (player.ultimateCooldown > 0f) {
            player.ultimateCooldown -= deltaTime
        }
        
        // Active ultimate
        if (ultimate.isActive) {
            ultimate.activeTimer -= deltaTime
            applyUltimateEffect(entity, ultimate, deltaTime)
            
            if (ultimate.activeTimer <= 0f) {
                ultimate.isActive = false
                ultimate.activeTimer = 0f
                player.ultimateCooldown = ultimate.cooldown
                gameWorld?.onUltimateEnd()
            }
        }
    }
    
    fun tryActivateUltimate(entity: Entity): Boolean {
        val player = entity.getComponent(PlayerComponent::class.java)
        val ultimate = entity.getComponent(UltimateComponent::class.java)
        
        if (player.ultimateMeter >= player.maxUltimateMeter && player.ultimateCooldown <= 0f) {
            player.ultimateMeter = 0f
            ultimate.isActive = true
            ultimate.activeTimer = ultimate.duration
            gameWorld?.onUltimateStart(ultimate.type)
            return true
        }
        return false
    }
    
    private fun applyUltimateEffect(entity: Entity, ultimate: UltimateComponent, deltaTime: Float) {
        when (ultimate.type) {
            UltimateType.INTERCEPTOR_BARRAGE -> {
                // Rapid fire handled by weapon system boost
                val weapon = entity.getComponent(WeaponComponent::class.java)
                weapon.fireRate = 0.03f
                weapon.damage = 25
            }
            UltimateType.HEAVY_NOVA -> {
                // Explosion around player - damage all nearby enemies
                if (ultimate.activeTimer > ultimate.duration - 0.1f) {
                    // One-time explosion
                    entityFactory?.createNovaExplosion(
                        entity.getComponent(TransformComponent::class.java).position.x,
                        entity.getComponent(TransformComponent::class.java).position.y,
                        300f, 50
                    )
                }
            }
            UltimateType.BALANCED_HOMING_SWARM -> {
                // Fire homing missiles at all enemies
                if (ultimate.activeTimer > ultimate.duration - 0.5f) {
                    entityFactory?.createHomingSwarm(
                        entity.getComponent(TransformComponent::class.java).position.x,
                        entity.getComponent(TransformComponent::class.java).position.y,
                        8, 30
                    )
                }
            }
        }
    }
}

class CleanupSystem(priority: Int = 100) : IteratingSystem(
    Family.all(TransformComponent::class.java).get(),
    priority
) {
    private var entityFactory: EntityFactory? = null
    
    fun setEntityFactory(factory: EntityFactory) {
        entityFactory = factory
    }
    
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val health = entity.getComponent(HealthComponent::class.java)
        if (health != null && health.isDead) {
            health.deathTimer -= deltaTime
            if (health.deathTimer <= 0f) {
                entityFactory?.markForRemoval(entity)
            }
        }
        
        // Remove off-screen pickups
        val pickup = entity.getComponent(PickupComponent::class.java)
        if (pickup != null) {
            val transform = entity.getComponent(TransformComponent::class.java)
            if (transform.position.y < -100f) {
                entityFactory?.markForRemoval(entity)
            }
        }
    }
}