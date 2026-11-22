package com.example.shooter

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.example.shooter.ecs.GameWorld
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameLoopTest {
    @Test
    fun testGameWorldCreation() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val config = AndroidApplicationConfiguration()
        
        // Test that we can create a GameWorld without crashing
        // This is a minimal smoke test for CI
        val gameWorld = GameWorld(
            com.example.shooter.ShooterGame(),
            800f, 1280f
        )
        
        // Verify initial state
        assert(gameWorld.currentStage == 1)
        assert(gameWorld.maxStage == 5)
        assert(!gameWorld.isGameOver)
        assert(!gameWorld.isStageClear)
        assert(gameWorld.playerEntity != null)
        
        gameWorld.dispose()
    }
    
    @Test
    fun testGameWorldUpdate() {
        val gameWorld = GameWorld(
            com.example.shooter.ShooterGame(),
            800f, 1280f
        )
        
        // Run a few frames
        repeat(10) {
            gameWorld.update(0.016f) // ~60fps
        }
        
        assert(gameWorld.currentStage == 1)
        gameWorld.dispose()
    }
}