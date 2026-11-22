package com.example.shooter.screen

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.example.shooter.ShooterGame

class LoadingScreen(private val game: ShooterGame) : ScreenAdapter() {
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private var progress = 0f
    private var assetsLoaded = false
    
    init {
        font.getData().setScale(2f)
        font.color = Color.WHITE
        
        // Queue all assets
        queueAssets()
    }
    
    private fun queueAssets() {
        val manager = game.assetManager
        
        // Models (will be generated/placeholder for now)
        // manager.load("models/ship_balanced.g3db", Model::class.java)
        // manager.load("models/ship_interceptor.g3db", Model::class.java)
        // manager.load("models/ship_heavy.g3db", Model::class.java)
        // manager.load("models/enemy_straight.g3db", Model::class.java)
        // manager.load("models/enemy_spread.g3db", Model::class.java)
        // manager.load("models/boss_stage1.g3db", Model::class.java)
        // manager.load("models/boss_stage2.g3db", Model::class.java)
        // manager.load("models/boss_stage3.g3db", Model::class.java)
        // manager.load("models/projectile_player.g3db", Model::class.java)
        // manager.load("models/projectile_enemy.g3db", Model::class.java)
        // manager.load("models/pickup_weapon.g3db", Model::class.java)
        // manager.load("models/pickup_ultimate.g3db", Model::class.java)
        // manager.load("models/pickup_health.g3db", Model::class.java)
        // manager.load("models/pickup_score.g3db", Model::class.java)
        
        // Particles
        // manager.load("particles/explosion.p", ParticleEffect::class.java)
        // manager.load("particles/hit.p", ParticleEffect::class.java)
        // manager.load("particles/engine.p", ParticleEffect::class.java)
        
        // Audio
        // manager.load("audio/shoot.ogg", Sound::class.java)
        // manager.load("audio/explosion.ogg", Sound::class.java)
        // manager.load("audio/powerup.ogg", Sound::class.java)
        // manager.load("audio/bgm_stage1.ogg", Music::class.java)
        // manager.load("audio/bgm_boss.ogg", Music::class.java)
    }
    
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        if (game.assetManager.update()) {
            assetsLoaded = true
        }
        
        progress = game.assetManager.progress
        
        batch.begin()
        val text = "Loading... ${(progress * 100).toInt()}%"
        layout.setText(font, text)
        font.draw(batch, layout, (Gdx.graphics.width - layout.width) / 2f, Gdx.graphics.height / 2f)
        batch.end()
        
        if (assetsLoaded) {
            game.goToGameScreen()
        }
    }
    
    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}