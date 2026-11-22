package com.example.shooter.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

// Transform component - position, rotation, scale
data class TransformComponent(
    var position: Vector2 = Vector2(),
    var rotation: Float = 0f,
    var scale: Vector2 = Vector2(1f, 1f),
    var zIndex: Float = 0f
) : Component

// Velocity component for movement
data class VelocityComponent(
    var velocity: Vector2 = Vector2(),
    var maxSpeed: Float = 500f
) : Component

// Player component
enum class ShipType {
    INTERCEPTOR,    // Fast, narrow spread, piercing shots
    HEAVY,          // Slow, wide spread, high damage
    BALANCED        // Balanced stats, homing shots
}

data class PlayerComponent(
    var shipType: ShipType = ShipType.BALANCED,
    var lives: Int = 3,
    var score: Long = 0,
    var weaponLevel: Int = 1,
    var maxWeaponLevel: Int = 5,
    var ultimateMeter: Float = 0f,
    var maxUltimateMeter: Float = 100f,
    var ultimateCooldown: Float = 0f,
    var isInvulnerable: Boolean = false,
    var invulnerabilityTimer: Float = 0f
) : Component

// Weapon component
data class WeaponComponent(
    var fireRate: Float = 0.15f,
    var fireTimer: Float = 0f,
    var damage: Int = 10,
    var projectileSpeed: Float = 800f,
    var spreadAngle: Float = 0f,
    var projectileCount: Int = 1,
    var isPiercing: Boolean = false,
    var isHoming: Boolean = false
) : Component

// Ultimate component
enum class UltimateType {
    INTERCEPTOR_BARRAGE,    // Rapid fire + speed boost
    HEAVY_NOVA,             // Massive explosion around player
    BALANCED_HOMING_SWARM   // Homing missiles targeting all enemies
}

data class UltimateComponent(
    var type: UltimateType = UltimateType.BALANCED_HOMING_SWARM,
    var isActive: Boolean = false,
    var activeTimer: Float = 0f,
    var duration: Float = 5f,
    var cooldown: Float = 30f
) : Component

// Enemy component
enum class EnemyType {
    STRAIGHT_SHOOTER,   // Moves down, shoots straight
    SPREAD_SHOOTER,     // Moves in pattern, shoots spread
    BOSS                // Stage boss, multiple phases
}

data class EnemyComponent(
    var type: EnemyType = EnemyType.STRAIGHT_SHOOTER,
    var hp: Int = 10,
    var maxHp: Int = 10,
    var scoreValue: Int = 100,
    var movePattern: MovePattern = MovePattern.STRAIGHT,
    var shootPattern: ShootPattern = ShootPattern.STRAIGHT,
    var shootTimer: Float = 0f,
    var shootInterval: Float = 2f,
    var phase: Int = 0,
    var phaseTimer: Float = 0f
) : Component

enum class MovePattern {
    STRAIGHT,       // Straight down
    SINE_WAVE,      // Sine wave horizontal movement
    CIRCLE,         // Circular movement
    BOSS_PHASE_1,   // Boss phase 1 pattern
    BOSS_PHASE_2,   // Boss phase 2 pattern
    BOSS_PHASE_3    // Boss phase 3 pattern
}

enum class ShootPattern {
    NONE,
    STRAIGHT,
    SPREAD_3,
    SPREAD_5,
    SPREAD_8,
    CIRCLE,
    TARGETED,
    BOSS_PATTERN_1,
    BOSS_PATTERN_2,
    BOSS_PATTERN_3
}

// Projectile component
enum class ProjectileOwner {
    PLAYER, ENEMY
}

data class ProjectileComponent(
    var owner: ProjectileOwner = ProjectileOwner.PLAYER,
    var damage: Int = 10,
    var isPiercing: Boolean = false,
    var isHoming: Boolean = false,
    var homingTarget: Int = -1, // Entity ID
    var lifeTime: Float = 5f,
    var maxLifeTime: Float = 5f
) : Component

// Pickup component
enum class PickupType {
    WEAPON_UPGRADE,   // Increases weapon level
    ULTIMATE_CHARGE,  // Fills ultimate meter
    HEALTH,           // Restores life
    SCORE_BONUS       // Bonus points
}

data class PickupComponent(
    var type: PickupType = PickupType.WEAPON_UPGRADE,
    var value: Int = 1,
    var bobTimer: Float = 0f,
    var bobAmplitude: Float = 10f,
    var bobFrequency: Float = 2f
) : Component

// Health component
data class HealthComponent(
    var currentHp: Int = 1,
    var maxHp: Int = 1,
    var isDead: Boolean = false,
    var deathTimer: Float = 0f,
    var deathDuration: Float = 0.5f
) : Component

// Collision component
data class CollisionComponent(
    var radius: Float = 16f,
    var collisionLayer: Int = 0,
    var collisionMask: Int = -1
) : Component

// Model/render component
data class ModelComponent(
    var modelInstance: com.badlogic.gdx.graphics.g3d.ModelInstance? = null,
    var animationController: com.badlogic.gdx.graphics.g3d.utils.AnimationController? = null,
    var currentAnimation: String = "",
    var tintColor: com.badlogic.gdx.graphics.Color = com.badlogic.gdx.graphics.Color.WHITE
) : Component

// Particle effect component
data class ParticleComponent(
    var emitter: com.badlogic.gdx.graphics.g2d.ParticleEmitter? = null,
    var isPlaying: Boolean = false
) : Component

// Screen shake component
data class ScreenShakeComponent(
    var intensity: Float = 0f,
    var duration: Float = 0f,
    var timer: Float = 0f
) : Component

// Stage component
data class StageComponent(
    var currentStage: Int = 1,
    var maxStage: Int = 5,
    var stageTimer: Float = 0f,
    var enemiesRemaining: Int = 0,
    var isBossStage: Boolean = false,
    var bossEntityId: Int = -1
) : Component