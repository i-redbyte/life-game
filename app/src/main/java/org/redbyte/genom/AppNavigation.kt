package org.redbyte.genom

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.redbyte.genom.game.GameBoard
import org.redbyte.genom.opengl.OpenGLView

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedGameViewModel = viewModel()
    NavHost(navController = navController, startDestination = "settingsGame") {
        composable("settingsGame") {
            SettingsScreen(navController, sharedViewModel)
        }
        composable("genomGame") {
            GenomGame(GameBoard(10, 10, 30, sharedViewModel.gameSettings.value))
        }
        composable("openGLGame") {
            OpenGLView(GameBoard(32, 32, 256, sharedViewModel.gameSettings.value))
        }
    }
}
