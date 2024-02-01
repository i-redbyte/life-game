package org.redbyte.genom

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "settingsGame") {
        composable("settingsGame") {
            SettingsScreen(navController)
        }
        composable("genomGame") {
            GenomGame()
        }
    }
}
