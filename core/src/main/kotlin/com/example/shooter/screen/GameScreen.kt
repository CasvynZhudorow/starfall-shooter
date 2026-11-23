package com.example.shooter.screen

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import com.badlogic.gdx.math.MathUtils
import com.example.shooter.ShooterGame
import com.example.shooter.ecs.GameWorld
import com.example.shooter.ecs.Components.ScreenShakeComponent

class GameScreen(private val game: ShooterGame) : ScreenAdapter() {
    private val modelBatch = ModelBatch(DefaultShaderProvider())
    private val spriteBatch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val camera: OrthographicCamera
    private var gameWorld: GameWorld
    private var screenShakeX = 0f
    private var screenShakeY = 0f
    private var isPaused = false
    
    // UI state
        private var score = 0L
        private var lives = 3
        private var weaponLevel = 1
        private var ultimateCurrent = 0f
        private var ultimateMax = 100f
        private var ultimateActive = false
        private var ultimateTypeName = ""

        // White 1x1 texture for UI drawing
        private val whiteTexture: Texture
        private val whiteRegion: TextureRegion

        init {
            font.data.setScale(1.5f)
            font.color = Color.WHITE

            // Create white 1x1 texture
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pixmap.color = Color.WHITE
            pixmap.fill()
            whiteTexture = Texture(pixmap)
            pixmap.dispose()
            whiteRegion = TextureRegion(whiteTexture)

            camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            camera.position.set(Gdx.graphics.width / 2f, Gdx.graphics.height / 2f, 0f)
            camera.update()

            gameWorld = GameWorld(game, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        
        // Setup callbacks
        gameWorld.onScoreChange = { score = it }
        gameWorld.onLivesChange = { lives = it }
        gameWorld.onWeaponLevelChange = { weaponLevel = it }
        gameWorld.onUltimateMeterChange = { current, max ->
            ultimateCurrent = current
            ultimateMax = max
        }
        gameWorld.onUltimateStart = { type ->
            ultimateActive = true
            ultimateTypeName = type.name
        }
        gameWorld.onUltimateEnd = {
            ultimateActive = false
            ultimateTypeName = ""
        }
        gameWorld.onGameOver = {
            showGameOver()
        }
        gameWorld.onStageClear = {
            showStageClear()
        }
    }
    
    override fun render(delta: Float) {
        if (!isPaused) {
            gameWorld.handleInput(delta)
            gameWorld.update(delta)
        }
        
        // Handle screen shake
        val shake = gameWorld.playerEntity?.getComponent(ScreenShakeComponent::class.java)
        if (shake != null && shake.timer > 0f) {
            shake.timer -= delta
            val intensity = shake.intensity * (shake.timer / shake.duration)
            screenShakeX = MathUtils.random(-intensity, intensity)
            screenShakeY = MathUtils.random(-intensity, intensity)
        } else {
            screenShakeX = 0f
            screenShakeY = 0f
        }
        
        // Render 3D models
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.08f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        
        camera.position.x = Gdx.graphics.width / 2f + screenShakeX
        camera.position.y = Gdx.graphics.height / 2f + screenShakeY
        camera.update()
        
        modelBatch.begin(camera)
        gameWorld.engine.getEntitiesFor(
            com.badlogic.ashley.core.Family.all(com.example.shooter.ecs.Components.ModelComponent::class.java).get()
        ).forEach { entity ->
            val modelComp = entity.getComponent(com.example.shooter.ecs.Components.ModelComponent::class.java)
            val transform = entity.getComponent(com.example.shooter.ecs.Components.TransformComponent::class.java)
            modelComp.modelInstance?.let { instance ->
                instance.transform.setToTranslation(transform.position.x, transform.position.y, transform.zIndex)
                instance.transform.rotate(0f, 0f, 1f, transform.rotation)
                instance.transform.scale(transform.scale.x, transform.scale.y, 1f)
                modelBatch.render(instance)
            }
        }
        modelBatch.end()
        
        // Render particles
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
        spriteBatch.setProjectionMatrix(camera.combined)
        spriteBatch.begin()
        
        gameWorld.engine.getEntitiesFor(
            com.badlogic.ashley.core.Family.all(com.example.shooter.ecs.Components.ParticleComponent::class.java).get()
        ).forEach { entity ->
            val particle = entity.getComponent(com.example.shooter.ecs.Components.ParticleComponent::class.java)
            val transform = entity.getComponent(com.example.shooter.ecs.Components.TransformComponent::class.java)
            if (particle.isPlaying && particle.emitter != null) {
                particle.emitter?.setPosition(transform.position.x, transform.position.y)
                particle.emitter?.draw(spriteBatch, delta)
            }
        }
        
        spriteBatch.end()
        
        // Render UI
        renderUI()
        
        // Handle pause
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                    isPaused = !isPaused
                }

                if (isPaused) {
            renderPauseMenu()
        }
    }
    
    private fun renderUI() {
        spriteBatch.begin()
        
        // Score
        val scoreText = "SCORE: $score"
        layout.setText(font, scoreText)
        font.draw(spriteBatch, layout, 20f, Gdx.graphics.height.toFloat() - 20f)
        
        // Lives
        val livesText = "LIVES: $lives"
        layout.setText(font, livesText)
        font.draw(spriteBatch, layout, 20f, Gdx.graphics.height.toFloat() - 50f)
        
        // Weapon Level
        val weaponText = "WEAPON: Lv.$weaponLevel"
        layout.setText(font, weaponText)
        font.draw(spriteBatch, layout, 20f, Gdx.graphics.height.toFloat() - 80f)
        
        // Ultimate Meter
                val ultimateBgWidth = 200f
                val ultimateBgHeight = 20f
                val ultimateX = Gdx.graphics.width - ultimateBgWidth - 20f
                val ultimateY = Gdx.graphics.height.toFloat() - 40f

                // Background
                spriteBatch.draw(
                    whiteRegion,
                    ultimateX, ultimateY, ultimateBgWidth, ultimateBgHeight,
                    Color.DARK_GRAY
                )

                // Fill
                val fillWidth = (ultimateCurrent / ultimateMax) * ultimateBgWidth
                val fillColor = if (ultimateCurrent >= ultimateMax) Color.GOLD else Color.CYAN
                spriteBatch.draw(
                    whiteRegion,
                    ultimateX, ultimateY, fillWidth, ultimateBgHeight,
                    fillColor
                )
        
        // Ultimate text
        val ultimateText = if (ultimateActive) "ULTIMATE: $ultimateTypeName ACTIVE!" else "ULTIMATE: ${(ultimateCurrent / ultimateMax * 100).toInt()}%"
        layout.setText(font, ultimateText)
        font.draw(spriteBatch, layout, ultimateX, ultimateY + ultimateBgHeight + 20f)
        
        // Stage
        val stageText = "STAGE: ${gameWorld.currentStage}/${gameWorld.maxStage}"
        layout.setText(font, stageText)
        font.draw(spriteBatch, layout, Gdx.graphics.width / 2f - layout.width / 2f, Gdx.graphics.height.toFloat() - 20f)
        
        spriteBatch.end()
    }
    
    private fun renderPauseMenu() {
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

            spriteBatch.begin()

            // Dark overlay
            spriteBatch.draw(
                whiteRegion,
                0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                Color.BLACK.cpy().apply { a = 0.7f }
            )
        
        val pauseText = "PAUSED"
        layout.setText(font, pauseText)
        font.draw(spriteBatch, layout, (Gdx.graphics.width - layout.width) / 2f, Gdx.graphics.height / 2f + 50f)
        
        val resumeText = "Tap or Press ESC to Resume"
        layout.setText(font, resumeText)
        font.draw(spriteBatch, layout, (Gdx.graphics.width - layout.width) / 2f, Gdx.graphics.height / 2f - 20f)
        
        spriteBatch.end()
        
        // Handle resume on touch
        if (Gdx.input.justTouched()) {
            isPaused = false
        }
    }
    
    private fun showGameOver() {
        // Transition to game over screen
        game.setScreen(GameOverScreen(game, score))
    }
    
    private fun showStageClear() {
        game.setScreen(StageClearScreen(game, gameWorld.currentStage, score, lives, weaponLevel, ultimateCurrent, ultimateMax))
    }
    
    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }
    
    override fun dispose() {
        modelBatch.dispose()
        spriteBatch.dispose()
        font.dispose()
        whiteTexture.dispose()
        gameWorld.dispose()
    }
}