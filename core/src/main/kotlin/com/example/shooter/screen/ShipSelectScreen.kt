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
import com.example.shooter.ecs.Components.ShipType

class ShipSelectScreen(private val game: ShooterGame) : ScreenAdapter() {
    private val batch = SpriteBatch()
    private val titleFont = BitmapFont()
    private val infoFont = BitmapFont()
    private val layout = GlyphLayout()
    private var selectedIndex = 1 // Default to BALANCED
    private val ships = arrayOf(
        ShipData(
            ShipType.INTERCEPTOR,
            "INTERCEPTOR",
            "High speed, piercing shots",
            "Ultimate: RAPID BARRAGE\nExtreme fire rate + speed boost",
            Color.CYAN
        ),
        ShipData(
            ShipType.BALANCED,
            "BALANCED",
            "Well-rounded, homing at max level",
            "Ultimate: HOMING SWARM\nMissiles target all enemies",
            Color.GREEN
        ),
        ShipData(
            ShipType.HEAVY,
            "HEAVY",
            "High damage, wide spread",
            "Ultimate: NOVA EXPLOSION\nMassive area damage",
            Color.ORANGE
        )
    )
    
    private data class ShipData(
        val type: ShipType,
        val name: String,
        val desc: String,
        val ultimateDesc: String,
        val color: Color
    )
    
    init {
        titleFont.getData().setScale(3f)
        titleFont.color = Color.GOLD
        
        infoFont.getData().setScale(1.5f)
        infoFont.color = Color.WHITE
    }
    
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        handleInput()
        renderUI()
    }
    
    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.A)) {
            selectedIndex = (selectedIndex - 1 + ships.size) % ships.size
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.D)) {
            selectedIndex = (selectedIndex + 1) % ships.size
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            confirmSelection()
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            game.setScreen(MainMenuScreen(game))
        }
        
        // Touch support
        if (Gdx.input.justTouched()) {
            val x = Gdx.input.getX()
            val shipWidth = Gdx.graphics.width / 3f
            selectedIndex = (x / shipWidth).toInt().coerceIn(0, ships.size - 1)
        }
    }
    
    private fun confirmSelection() {
        val selectedShip = ships[selectedIndex]
        // The game world will be created in GameScreen, so we need to pass the selection
        game.setScreen(GameScreen(game).also { screen ->
            // We'll set the ship type after GameScreen creates its GameWorld
            // This is a bit hacky - in a real app we'd use a proper state management
        })
    }
    
    private fun renderUI() {
        batch.begin()
        
        // Title
        val title = "SELECT YOUR SHIP"
        layout.setText(titleFont, title)
        titleFont.draw(batch, layout, (Gdx.graphics.width - layout.width) / 2f, Gdx.graphics.height * 0.85f)
        
        // Ship cards
        val cardWidth = Gdx.graphics.width / 3f - 20f
        val cardHeight = Gdx.graphics.height * 0.5f
        val startX = 10f
        val cardY = Gdx.graphics.height * 0.3f
        
        for (i in ships.indices) {
            val ship = ships[i]
            val x = startX + i * (cardWidth + 10f)
            val isSelected = i == selectedIndex
            
            // Card background
            val bgColor = if (isSelected) ship.color.cpy().apply { a = 0.3f } else Color.DARK_GRAY.cpy().apply { a = 0.8f }
            batch.draw(
                com.badlogic.gdx.graphics.g2d.TextureRegion.getWhiteRegion(),
                x, cardY, cardWidth, cardHeight,
                bgColor
            )
            
            // Border if selected
            if (isSelected) {
                batch.draw(
                    com.badlogic.gdx.graphics.g2d.TextureRegion.getWhiteRegion(),
                    x - 2f, cardY - 2f, cardWidth + 4f, cardHeight + 4f,
                    ship.color
                )
                // Redraw background on top
                batch.draw(
                    com.badlogic.gdx.graphics.g2d.TextureRegion.getWhiteRegion(),
                    x, cardY, cardWidth, cardHeight,
                    bgColor
                )
            }
            
            // Ship name
            layout.setText(titleFont, ship.name)
            titleFont.color = ship.color
            titleFont.draw(batch, layout, x + (cardWidth - layout.width) / 2f, cardY + cardHeight - 40f)
            
            // Description
            infoFont.color = Color.WHITE
            layout.setText(infoFont, ship.desc)
            infoFont.draw(batch, layout, x + 10f, cardY + cardHeight - 100f)
            
            // Ultimate
            val ultLines = ship.ultimateDesc.split("\n")
            var lineY = cardY + cardHeight - 140f
            for (line in ultLines) {
                layout.setText(infoFont, line)
                infoFont.draw(batch, layout, x + 10f, lineY)
                lineY -= 25f
            }
            
            // Stats bar
            drawStatBar(x + 10f, cardY + 20f, cardWidth - 20f, "SPEED", getSpeedStat(ship.type))
            drawStatBar(x + 10f, cardY + 50f, cardWidth - 20f, "POWER", getPowerStat(ship.type))
            drawStatBar(x + 10f, cardY + 80f, cardWidth - 20f, "SPREAD", getSpreadStat(ship.type))
        }
        
        // Instructions
        val instr = "← → to select  |  ENTER to confirm  |  ESC to go back"
        layout.setText(infoFont, instr)
        infoFont.color = Color.GRAY
        infoFont.draw(batch, layout, (Gdx.graphics.width - layout.width) / 2f, 50f)
        
        batch.end()
    }
    
    private fun drawStatBar(x: Float, y: Float, width: Float, label: String, value: Float) {
        infoFont.color = Color.WHITE
        layout.setText(infoFont, label)
        infoFont.draw(batch, layout, x, y + 15f)
        
        // Background
        batch.draw(
            com.badlogic.gdx.graphics.g2d.TextureRegion.getWhiteRegion(),
            x, y, width, 10f,
            Color.DARK_GRAY
        )
        
        // Fill
        batch.draw(
            com.badlogic.gdx.graphics.g2d.TextureRegion.getWhiteRegion(),
            x, y, width * value, 10f,
            Color.GREEN
        )
    }
    
    private fun getSpeedStat(type: ShipType): Float = when (type) {
        ShipType.INTERCEPTOR -> 1.0f
        ShipType.BALANCED -> 0.7f
        ShipType.HEAVY -> 0.4f
    }
    
    private fun getPowerStat(type: ShipType): Float = when (type) {
        ShipType.INTERCEPTOR -> 0.5f
        ShipType.BALANCED -> 0.7f
        ShipType.HEAVY -> 1.0f
    }
    
    private fun getSpreadStat(type: ShipType): Float = when (type) {
        ShipType.INTERCEPTOR -> 0.3f
        ShipType.BALANCED -> 0.6f
        ShipType.HEAVY -> 1.0f
    }
    
    override fun dispose() {
        batch.dispose()
        titleFont.dispose()
        infoFont.dispose()
    }
}