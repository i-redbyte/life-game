package org.redbyte.genom

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.redbyte.genom.common.GameBoard
import org.redbyte.genom.render.compose.GenomGame
import org.redbyte.genom.settings.SettingsScreen
import org.redbyte.genom.render.opengl.Genom2DGame

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "settingsGame") {
        composable("settingsGame") {
            SettingsScreen(navController)
        }
        composable("genomGame") {
            GenomGame(GameBoard(10, 10, 30))
        }
        composable("openGLGame") {
            Genom2DGame(GameBoard(32, 32, 256))
        }
    }
}
