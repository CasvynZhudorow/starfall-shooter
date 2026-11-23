package com.example.shooter.screen

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.example.shooter.ShooterGame

class StageClearScreen(
    private val game: ShooterGame,
    private val stageCleared: Int,
    private val score: Long,
    private val lives: Int,
    private val weaponLevel: Int,
    private val ultimateCurrent: Float,
    private val ultimateMax: Float
) : ScreenAdapter() {
    private val batch = SpriteBatch()
    private val titleFont = BitmapFont()
    private val infoFont = BitmapFont()
    private val menuFont = BitmapFont()
    private val layout = GlyphLayout()
    private var timer = 0f
        private var canContinue = false

        // White 1x1 texture for UI drawing
        private val whiteTexture: Texture
        private val whiteRegion: TextureRegion

        init {
            titleFont.data.setScale(4f)
            titleFont.color = Color.GOLD

            infoFont.data.setScale(2f)
            infoFont.color = Color.WHITE

            menuFont.data.setScale(2f)
            menuFont.color = Color.GREEN

            // Create white 1x1 texture
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pixmap.setColor(Color.WHITE)
            pixmap.fill()
            whiteTexture = Texture(pixmap)
            pixmap.dispose()
            whiteRegion = TextureRegion(whiteTexture)
        }
    
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.02f, 0.1f, 0.02f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        timer += delta
        if (timer > 1.5f) canContinue = true
        
        handleInput()
        renderUI()
    }
    
    private fun handleInput() {
            if (canContinue && (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER) ||
                Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE) || Gdx.input.justTouched())) {
                game.setScreen(com.example.shooter.screen.GameScreen(game))
            }
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                game.setScreen(MainMenuScreen(game))
            }
        }
    
    private fun renderUI() {
        batch.begin()
        
        // Stage Clear title
        val title = "STAGE $stageCleared CLEAR!"
        layout.setText(titleFont, title)
        titleFont.draw(batch, layout, (Gdx.graphics.width - layout.width) / 2f, Gdx.graphics.height * 0.8f)
        
        // Stats
        val yStart = Gdx.graphics.height * 0.6f
        val lineHeight = 50f
        
        val stats = arrayOf(
            "SCORE: $score",
            "LIVES: $lives",
            "WEAPON LV: $weaponLevel",
            "ULTIMATE: ${(ultimateCurrent / ultimateMax * 100).toInt()}%"
        )
        
        for (i in stats.indices) {
            val text = stats[i]
            layout.setText(infoFont, text)
            infoFont.draw(batch, layout, (Gdx.graphics.width - layout.width) / 2f, yStart - i * lineHeight)
        }
        
        // Bonus
        val bonus = stageCleared * 5000
        val bonusText = "STAGE BONUS: +$bonus"
        layout.setText(infoFont, bonusText)
        infoFont.color = Color.GOLD
        infoFont.draw(batch, layout, (Gdx.graphics.width - layout.width) / 2f, yStart - stats.size * lineHeight - 20f)
        
        // Continue prompt
        if (canContinue) {
            val prompt = "Tap or Press ENTER to Continue"
            val alpha = ((Math.sin(timer * 4f) * 0.5 + 0.5) as Float)
            menuFont.color = Color.GREEN.cpy().apply { this.a = alpha }
            layout.setText(menuFont, prompt)
            menuFont.draw(batch, layout, (Gdx.graphics.width - layout.width) / 2f, Gdx.graphics.height * 0.2f)
        }
        
        batch.end()
    }
    
    override fun dispose() {
        batch.dispose()
        titleFont.dispose()
        infoFont.dispose()
        menuFont.dispose()
        whiteTexture.dispose()
    }
}