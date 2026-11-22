package com.example.shooter

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

object DesktopLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration().apply {
            title = "Starfall Shooter"
            setWindowedMode(1280, 720)
            setResizable(false)
            useVsync(true)
            setForegroundFPS(60)
            setIdleFPS(30)
            backBufferConfig = com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.BackBufferConfig(8, 8, 8, 8, 16, 8, 2)
        }
        Lwjgl3Application(ShooterGame(), config)
    }
}