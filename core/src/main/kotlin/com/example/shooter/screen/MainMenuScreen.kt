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

class MainMenuScreen(private val game: ShooterGame) : ScreenAdapter() {
    private val batch = SpriteBatch()
    private val titleFont = BitmapFont()
    private val menuFont = BitmapFont()
    private val layout = GlyphLayout()
    private var selectedIndex = 0
    private val menuItems = arrayOf("START", "SHIP SELECT", "QUIT")

        // White 1x1 texture for UI drawing
        private val whiteTexture: Texture
        private val whiteRegion: TextureRegion

        init {
            titleFont.data.setScale(4f)
            titleFont.color = Color.GOLD

            menuFont.data.setScale(2f)
            menuFont.color = Color.WHITE

            // Create white 1x1 texture
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pixmap.color = Color.WHITE
            pixmap.fill()
            whiteTexture = Texture(pixmap)
            pixmap.dispose()
            whiteRegion = TextureRegion(whiteTexture)
        }
    
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        handleInput()
        renderUI()
    }
    
    private fun handleInput() {
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) {
                selectedIndex = (selectedIndex - 1 + menuItems.size) % menuItems.size
            }
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) {
                selectedIndex = (selectedIndex + 1) % menuItems.size
            }
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                selectMenuItem()
            }
        
        // Touch support
        if (Gdx.input.justTouched()) {
            val y = Gdx.graphics.height - Gdx.input.getY()
            val itemHeight = 60f
            val startY = Gdx.graphics.height / 2f + 50f
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
            0 -> game.setScreen(ShipSelectScreen(game)) // Start -> Ship Select first
            1 -> game.setScreen(ShipSelectScreen(game))
            2 -> Gdx.app.exit()
        }
    }
    
    private fun renderUI() {
        batch.begin()
        
        // Title
        val title = "STARFALL SHOOTER"
        layout.setText(titleFont, title)
        titleFont.draw(batch, layout, (Gdx.graphics.width - layout.width) / 2f, Gdx.graphics.height * 0.75f)
        
        // Subtitle
        val subtitle = "Vertical 2.5D Shooter"
        layout.setText(menuFont, subtitle)
        menuFont.draw(batch, layout, (Gdx.graphics.width - layout.width) / 2f, Gdx.graphics.height * 0.65f)
        
        // Menu items
        val startY = Gdx.graphics.height / 2f + 50f
        val itemHeight = 60f
        
        for (i in menuItems.indices) {
            val text = menuItems[i]
            layout.setText(menuFont, text)
            val x = (Gdx.graphics.width - layout.width) / 2f
            val y = startY - i * itemHeight
            
            if (i == selectedIndex) {
                // Highlight
                menuFont.color = Color.GOLD
                menuFont.draw(batch, "> $text <", x - 30f, y)
            } else {
                menuFont.color = Color.WHITE
                menuFont.draw(batch, text, x, y)
            }
        }
        
        // Version
        val version = "v1.0.0"
        layout.setText(menuFont, version)
        menuFont.color = Color.GRAY
        menuFont.draw(batch, layout, 20f, 30f)
        
        batch.end()
    }
    
    override fun dispose() {
        batch.dispose()
        titleFont.dispose()
        menuFont.dispose()
        whiteTexture.dispose()
    }
}