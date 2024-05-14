package org.redbyte.genom

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.redbyte.genom.render.compose.GenomGame
import org.redbyte.genom.settings.SettingsScreen
import org.redbyte.genom.render.opengl.Genom2DGame
import org.redbyte.genom.settings.SharedGameSettingsViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedGameSettingsViewModel = viewModel()
    NavHost(navController = navController, startDestination = "settingsGame") {
        composable("settingsGame") {
            SettingsScreen(navController, sharedViewModel)
        }
        composable("genomGame") {
            GenomGame(sharedViewModel)
        }
        composable("openGLGame") {
            Genom2DGame(sharedViewModel)
        }
    }
}
