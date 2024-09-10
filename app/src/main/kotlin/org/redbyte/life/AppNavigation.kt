package org.redbyte.life

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.redbyte.life.ui.render.compose.LifeGame
import org.redbyte.life.ui.settings.SettingsScreen
import org.redbyte.life.ui.render.opengl.LifeGame2D
import org.redbyte.life.ui.settings.SharedGameSettingsViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedGameSettingsViewModel = viewModel()
    NavHost(navController = navController, startDestination = "settingsGame") {
        composable("settingsGame") {
            SettingsScreen(navController, sharedViewModel)
        }
        composable("lifeGame") {
            LifeGame(sharedViewModel)
        }
        composable("openGLGame") {
            LifeGame2D(sharedViewModel)
        }
    }
}
