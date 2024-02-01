package org.redbyte.genom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import org.redbyte.genom.ui.theme.GenomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GenomTheme {
                val navController = rememberNavController()
                SettingsScreen(navController)
               // GenomGame()
            }
        }
    }
}