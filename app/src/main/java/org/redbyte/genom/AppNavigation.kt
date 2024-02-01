package org.redbyte.genom

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedGameViewModel = viewModel()
    NavHost(navController = navController, startDestination = "settingsGame") {
        composable("settingsGame") {
            SettingsScreen(navController, sharedViewModel)
        }
        composable("genomGame") {
            GenomGame(sharedViewModel)
        }
    }
}
