package com.example.shooter.screen

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.example.shooter.ShooterGame

class GameOverScreen(private val game: ShooterGame, finalScore: Long) : ScreenAdapter() {
    private val batch = SpriteBatch()
    private val titleFont = BitmapFont()
    private val menuFont = BitmapFont()
    private val layout = GlyphLayout()
    private var selectedIndex = 0
    private val menuItems = arrayOf("RETRY", "MAIN MENU", "QUIT")
    
    init {
        titleFont.getData().setScale(4f)
        titleFont.color = Color.RED
        
        menuFont.getData().setScale(2f)
        menuFont.color = Color.WHITE
    }
    
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.02f, 0.02f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        handleInput()
        renderUI()
    }
    
    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.W)) {
            selectedIndex = (selectedIndex - 1 + menuItems.size) % menuItems.size
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
            selectedIndex = (selectedIndex + 1) % menuItems.size
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            selectMenuItem()
        }
        
        // Touch support
        if (Gdx.input.justTouched()) {
            val y = Gdx.graphics.height - Gdx.input.getY()
            val itemHeight = 60f
            val startY = Gdx.graphics.height / 2f - 50f
            for (i in menuItems.indices) {
                val itemY = startY - i * itemHeight
                if (y >= itemY - 30f && y <= itemY + 30f) {
                    selectedIndex = i
                    selectMenuItem()
                    break
                }
            }
        }
    }
    
    private fun selectMenuItem() {
        when (selectedIndex) {
            0 -> game.setScreen(ShipSelectScreen(game))
            1 -> game.setScreen(MainMenuScreen(game))
            2 -> Gdx.app.exit()
        }
    }
    
    private fun renderUI() {
        batch.begin()
        
        // Game Over title
        val title = "GAME OVER"
        layout.setText(titleFont, title)
        titleFont.draw(batch, layout, (Gdx.graphics.width - layout.width) / 2f, Gdx.graphics.height * 0.75f)
        
        // Final score
        val scoreText = "FINAL SCORE: $finalScore"
        layout.setText(menuFont, scoreText)
        menuFont.color = Color.GOLD
        menuFont.draw(batch, layout, (Gdx.graphics.width - layout.width) / 2f, Gdx.graphics.height * 0.6f)
        
        // Menu items
        val startY = Gdx.graphics.height / 2f - 50f
        val itemHeight = 60f
        
        for (i in menuItems.indices) {
            val text = menuItems[i]
            layout.setText(menuFont, text)
            val x = (Gdx.graphics.width - layout.width) / 2f
            val y = startY - i * itemHeight
            
            if (i == selectedIndex) {
                menuFont.color = Color.GOLD
                menuFont.draw(batch, "> $text <", x - 30f, y)
            } else {
                menuFont.color = Color.WHITE
                menuFont.draw(batch, text, x, y)
            }
        }
        
        batch.end()
    }
    
    override fun dispose() {
        batch.dispose()
        titleFont.dispose()
        menuFont.dispose()
    }
}