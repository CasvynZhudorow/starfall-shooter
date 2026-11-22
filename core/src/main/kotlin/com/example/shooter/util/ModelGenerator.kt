package com.example.shooter.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.example.shooter.ecs.Components.ShipType

object ModelGenerator {
    
    private val builder = ModelBuilder()
    
    // Player ships
    fun createInterceptorModel(): Model {
        builder.begin()
        
        // Main hull - long narrow body
        val hull = builder.part("hull", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal, 
            Material(ColorAttribute.createDiffuse(Color.valueOf("00ffff"))))
        builder.box(0.3f, 1.2f, 0.2f)
        
        // Cockpit
        val cockpit = builder.part("cockpit", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("88ffff"))))
        builder.box(0.25f, 0.4f, 0.3f, Vector3(0f, 0.6f, 0f))
        
        // Wings - swept back
        val wings = builder.part("wings", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("00aaaa"))))
        builder.box(1.0f, 0.1f, 0.5f, Vector3(0f, -0.2f, 0f))
        
        // Engine glow (emissive)
        val engine = builder.part("engine", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("0088ff")), 
                     ColorAttribute.createEmissive(Color.valueOf("00ffff"))))
        builder.box(0.2f, 0.3f, 0.25f, Vector3(0f, -0.8f, 0f))
        
        return builder.end()
    }
    
    fun createBalancedModel(): Model {
        builder.begin()
        
        // Main hull
        builder.part("hull", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("00ff88"))))
        builder.box(0.5f, 1.0f, 0.3f)
        
        // Cockpit
        builder.part("cockpit", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("88ffaa"))))
        builder.box(0.35f, 0.4f, 0.4f, Vector3(0f, 0.5f, 0f))
        
        // Wings - straight
        builder.part("wings", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("00aa66"))))
        builder.box(1.2f, 0.1f, 0.6f, Vector3(0f, -0.1f, 0f))
        
        // Engine
        builder.part("engine", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("008844")),
                     ColorAttribute.createEmissive(Color.valueOf("00ff88"))))
        builder.box(0.3f, 0.3f, 0.35f, Vector3(0f, -0.7f, 0f))
        
        return builder.end()
    }
    
    fun createHeavyModel(): Model {
        builder.begin()
        
        // Main hull - wide and thick
        builder.part("hull", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("ff8800"))))
        builder.box(0.8f, 1.3f, 0.5f)
        
        // Cockpit - armored
        builder.part("cockpit", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("ffaa44"))))
        builder.box(0.5f, 0.5f, 0.6f, Vector3(0f, 0.7f, 0f))
        
        // Wings - broad
        builder.part("wings", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("cc6600"))))
        builder.box(1.8f, 0.15f, 0.8f, Vector3(0f, 0f, 0f))
        
        // Twin engines
        builder.part("engine_left", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("aa4400")),
                     ColorAttribute.createEmissive(Color.valueOf("ff6600"))))
        builder.box(0.3f, 0.4f, 0.4f, Vector3(-0.5f, -0.8f, 0f))
        
        builder.part("engine_right", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("aa4400")),
                     ColorAttribute.createEmissive(Color.valueOf("ff6600"))))
        builder.box(0.3f, 0.4f, 0.4f, Vector3(0.5f, -0.8f, 0f))
        
        return builder.end()
    }
    
    // Enemies
    fun createStraightEnemyModel(): Model {
        builder.begin()
        
        // Triangular drone body
        builder.part("body", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("ff4444"))))
        builder.box(0.6f, 0.6f, 0.2f)
        
        // Sensor eye
        builder.part("eye", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("ff0000")),
                     ColorAttribute.createEmissive(Color.valueOf("ff0000"))))
        builder.box(0.15f, 0.15f, 0.25f, Vector3(0f, 0.1f, 0f))
        
        // Thruster
        builder.part("thruster", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("aa0000")),
                     ColorAttribute.createEmissive(Color.valueOf("ff4444"))))
        builder.box(0.2f, 0.2f, 0.2f, Vector3(0f, -0.4f, 0f))
        
        return builder.end()
    }
    
    fun createSpreadEnemyModel(): Model {
        builder.begin()
        
        // Three-pronged body
        builder.part("center", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("aa00ff"))))
        builder.box(0.3f, 0.8f, 0.2f)
        
        // Three prongs
        builder.part("prong_left", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("8800cc"))))
        builder.box(0.15f, 0.6f, 0.2f, Vector3(-0.4f, 0f, 0f))
        
        builder.part("prong_right", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("8800cc"))))
        builder.box(0.15f, 0.6f, 0.2f, Vector3(0.4f, 0f, 0f))
        
        builder.part("prong_bottom", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("8800cc"))))
        builder.box(0.15f, 0.6f, 0.2f, Vector3(0f, -0.6f, 0f))
        
        // Energy cores on prongs
        builder.part("core_left", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("ff00ff")),
                     ColorAttribute.createEmissive(Color.valueOf("ff00ff"))))
        builder.box(0.2f, 0.2f, 0.25f, Vector3(-0.4f, 0.4f, 0f))
        
        builder.part("core_right", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("ff00ff")),
                     ColorAttribute.createEmissive(Color.valueOf("ff00ff"))))
        builder.box(0.2f, 0.2f, 0.25f, Vector3(0.4f, 0.4f, 0f))
        
        builder.part("core_bottom", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("ff00ff")),
                     ColorAttribute.createEmissive(Color.valueOf("ff00ff"))))
        builder.box(0.2f, 0.2f, 0.25f, Vector3(0f, -1.0f, 0f))
        
        return builder.end()
    }
    
    // Bosses
    fun createBossStage1Model(): Model {
        builder.begin()
        
        // Central core
        builder.part("core", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("ffff00")),
                     ColorAttribute.createEmissive(Color.valueOf("ffff88"))))
        builder.sphere(1.2f, 1.2f, 1.2f, 16, 16)
        
        // Core eye
        builder.part("eye", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("ff0000")),
                     ColorAttribute.createEmissive(Color.valueOf("ff0000"))))
        builder.box(0.4f, 0.4f, 0.5f)
        
        // Rotating shield ring 1
        builder.part("ring1", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("8888ff")),
                     ColorAttribute.createEmissive(Color.valueOf("4444ff"))))
        builder.torus(0.15f, 2.0f, 16, 32)
        
        // Shield ring 2
        builder.part("ring2", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("6666ee")),
                     ColorAttribute.createEmissive(Color.valueOf("2222cc"))))
        builder.torus(0.15f, 2.8f, 16, 32)
        
        // Shield ring 3
        builder.part("ring3", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("4444dd")),
                     ColorAttribute.createEmissive(Color.valueOf("1111aa"))))
        builder.torus(0.15f, 3.6f, 16, 32)
        
        return builder.end()
    }
    
    fun createBossStage2Model(): Model {
        builder.begin()
        
        // Main hull - long
        builder.part("hull", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("aaaaaa"))))
        builder.box(2.0f, 5.0f, 1.0f)
        
        // Bridge
        builder.part("bridge", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("ff4444"))))
        builder.box(0.8f, 0.8f, 0.8f, Vector3(0f, 2.5f, 0f))
        
        // Turrets (4)
        val turretPositions = arrayOf(
            Vector3(-1.5f, 1.5f, 0f), Vector3(1.5f, 1.5f, 0f),
            Vector3(-1.5f, -1.5f, 0f), Vector3(1.5f, -1.5f, 0f)
        )
        turretPositions.forEachIndexed { i, pos ->
            builder.part("turret_$i", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
                Material(ColorAttribute.createDiffuse(Color.valueOf("cc0000"))))
            builder.cylinder(0.4f, 0.6f, 0.4f, 8)
        }
        
        // Engines (3)
        val enginePositions = arrayOf(
            Vector3(-0.8f, -3.0f, 0f), Vector3(0f, -3.0f, 0f), Vector3(0.8f, -3.0f, 0f)
        )
        enginePositions.forEachIndexed { i, pos ->
            builder.part("engine_$i", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
                Material(ColorAttribute.createDiffuse(Color.valueOf("ff6600")),
                         ColorAttribute.createEmissive(Color.valueOf("ff8800"))))
            builder.cylinder(0.5f, 0.8f, 0.5f, 8, Vector3(pos.x, pos.y, pos.z))
        }
        
        return builder.end()
    }
    
    fun createBossStage3Model(): Model {
        builder.begin()
        
        // Central mass - organic shape
        builder.part("core", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("110011")),
                     ColorAttribute.createEmissive(Color.valueOf("ff00ff"))))
        builder.sphere(1.5f, 1.5f, 1.5f, 20, 20)
        
        // Tentacles/limbs (6)
        (0..5).forEach { i ->
            val angle = i * 60f * 3.14159f / 180f
            val length = 3.0f
            val segments = 8
            
            for (seg in 0 until segments) {
                val t = seg / (segments - 1).toFloat()
                val segAngle = angle + Math.sin(t * 3.14159f * 2f + timer * 2f) * 0.5f
                val x = Math.cos(segAngle) * (1.5f + t * length)
                val y = Math.sin(segAngle) * (1.5f + t * length)
                val z = Math.sin(t * 3.14159f) * 0.5f
                val scale = 0.3f * (1f - t * 0.5f)
                
                builder.part("limb_${i}_$seg", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
                    Material(ColorAttribute.createDiffuse(Color.valueOf("220033")),
                             ColorAttribute.createEmissive(Color.valueOf("aa00ff"))))
                builder.box(scale, scale * 2f, scale, Vector3(x, y, z))
            }
            
            // Weapon tip
            val tipAngle = angle
            val tipX = Math.cos(tipAngle) * (1.5f + length)
            val tipY = Math.sin(tipAngle) * (1.5f + length)
            builder.part("weapon_$i", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
                Material(ColorAttribute.createDiffuse(Color.valueOf("ff00aa")),
                         ColorAttribute.createEmissive(Color.valueOf("ff00ff"))))
            builder.box(0.2f, 0.4f, 0.2f, Vector3(tipX, tipY, 0f))
        }
        
        return builder.end()
    }
    
    // Projectiles
    fun createPlayerProjectileModel(): Model {
        builder.begin()
        builder.part("bolt", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("00ffff")),
                     ColorAttribute.createEmissive(Color.valueOf("00ffff"))))
        builder.box(0.1f, 0.5f, 0.1f)
        return builder.end()
    }
    
    fun createEnemyProjectileModel(): Model {
        builder.begin()
        builder.part("bolt", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(Color.valueOf("ff4444")),
                     ColorAttribute.createEmissive(Color.valueOf("ff0000"))))
        builder.box(0.12f, 0.4f, 0.12f)
        return builder.end()
    }
    
    // Pickups
    fun createPickupModel(color: Color, emissive: Color): Model {
        builder.begin()
        builder.part("pickup", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
            Material(ColorAttribute.createDiffuse(color), ColorAttribute.createEmissive(emissive)))
        builder.box(0.4f, 0.4f, 0.4f)
        return builder.end()
    }
    
    // Get model by ship type
    fun getPlayerModel(shipType: ShipType): Model = when (shipType) {
        ShipType.INTERCEPTOR -> createInterceptorModel()
        ShipType.BALANCED -> createBalancedModel()
        ShipType.HEAVY -> createHeavyModel()
    }
    
    // Initialize all placeholder models in asset manager
    fun generateAllPlaceholders(assetManager: com.badlogic.gdx.assets.AssetManager) {
        // Models are generated on-demand, not loaded from asset manager
        // This is a placeholder for the asset loading system
    }
    
    private var timer = 0f
}