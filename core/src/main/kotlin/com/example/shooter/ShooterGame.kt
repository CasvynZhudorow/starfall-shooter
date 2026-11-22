package com.example.shooter

import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.utils.ScreenUtils
import com.example.shooter.screen.GameScreen
import com.example.shooter.screen.LoadingScreen

class ShooterGame : Game() {
    lateinit var assetManager: AssetManager
    lateinit var modelBatch: ModelBatch
    lateinit var gameScreen: GameScreen

    override fun create() {
        assetManager = AssetManager()
        modelBatch = ModelBatch()
        setScreen(LoadingScreen(this))
    }

    fun goToGameScreen() {
        gameScreen = GameScreen(this)
        setScreen(gameScreen)
    }

    override fun dispose() {
        super.dispose()
        assetManager.dispose()
        modelBatch.dispose()
        gameScreen.dispose()
    }
}