package org.redbyte.genom.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import org.redbyte.genom.GameLogic

@Composable
fun GameApp() {
    val gameLogic = remember { GameLogic(10, 10) }
    val gameState = remember { mutableStateOf(gameLogic.cells) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val boardWidth = maxWidth
        val boardHeight = maxHeight

        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                gameLogic.nextTurn()
                gameState.value = gameLogic.cells
            }
        }

        GameBoard(gameState.value.map { it.toList() }, boardWidth, boardHeight)
    }
}

