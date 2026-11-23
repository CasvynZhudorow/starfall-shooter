package com.example.shooter.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.example.shooter.ecs.Components.ShipType

object ModelGenerator {
    private val builder = ModelBuilder()
    
    private fun makeBox(): Model {
        return builder.begin().apply {
            this.part("box", Mesh.PrimitiveType.Triangles, Usage.Position | Usage.Normal,
                Material(ColorAttribute.createDiffuse(Color.WHITE)))
            this.box(1f, 1f, 1f)
        }.end()
    }
    
    fun createInterceptorModel(): Model = makeBox()
    fun createBalancedModel(): Model = makeBox()
    fun createHeavyModel(): Model = makeBox()
    fun createStraightEnemyModel(): Model = makeBox()
    fun createSpreadEnemyModel(): Model = makeBox()
    fun createBossStage1Model(): Model = makeBox()
    fun createBossStage2Model(): Model = makeBox()
    fun createBossStage3Model(): Model = makeBox()
    fun createPlayerProjectileModel(): Model = makeBox()
    fun createEnemyProjectileModel(): Model = makeBox()
    fun createPickupModel(color: Color, emissive: Color): Model = makeBox()
    
    fun getPlayerModel(shipType: ShipType): Model = when (shipType) {
        ShipType.INTERCEPTOR -> createInterceptorModel()
        ShipType.BALANCED -> createBalancedModel()
        ShipType.HEAVY -> createHeavyModel()
        else -> createBalancedModel()
    }
    
    fun generateAllPlaceholders(assetManager: com.badlogic.gdx.assets.AssetManager) {}
}
