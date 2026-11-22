package com.example.shooter

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.example.shooter.ShooterGame

class MainActivity : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration().apply {
            useImmersiveMode = true
            useAccelerometer = false
            useCompass = false
            useGyroscope = false
            numSamples = 2 // MSAA 2x
            r = 8
            g = 8
            b = 8
            a = 8
            depth = 16
            stencil = 8
        }
        initialize(ShooterGame(), config)
    }
}