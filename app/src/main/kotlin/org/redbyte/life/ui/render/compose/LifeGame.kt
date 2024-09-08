package org.redbyte.life.ui.render.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.redbyte.life.R
import org.redbyte.life.ui.settings.SharedGameSettingsViewModel
import org.redbyte.life.ui.theme.baseGreen
import org.redbyte.life.ui.theme.baseYellow
import org.redbyte.life.ui.theme.baseRed
import org.redbyte.life.ui.theme.blueSapphire

private const val DELAY_UPDATE_WORLD = 125L

@Composable
fun LifeGame(viewModel: SharedGameSettingsViewModel) {
    val gameBoard = viewModel.getGameBoard()
    val coroutineScope = rememberCoroutineScope()
    var showTopSheet by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth
        val cellSize = screenWidth / gameBoard.settings.width
        val cellCount = remember { mutableIntStateOf(0) }
        val turnNumber = remember { mutableIntStateOf(0) }
        var matrix by remember { mutableStateOf(gameBoard.matrix) }

        LaunchedEffect(key1 = isPaused, key2 = matrix) {
            while (!isPaused) {

                cellCount.intValue =
                    matrix.sumOf { row -> row.count { it.isAlive } }
                gameBoard.matrix.sumOf { row -> row.count { it.isAlive } }
                turnNumber.intValue++
                delay(DELAY_UPDATE_WORLD)
                gameBoard.update()
                matrix = gameBoard.matrix.clone()
            }
        }

        Column(modifier = Modifier.pointerInput(Unit) {
            detectVerticalDragGestures { _, dragAmount ->
                coroutineScope.launch {
                    if (dragAmount > 0 && !showTopSheet) {
                        showTopSheet = true
                    } else if (dragAmount < 0 && showTopSheet) {
                        showTopSheet = false
                    }
                }
            }
        }) {
            AnimatedVisibility(
                visible = showTopSheet,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(stringResource(R.string.turn, turnNumber.intValue))
                    Button(onClick = { isPaused = !isPaused }) {
                        Text(
                            if (isPaused) stringResource(R.string.continue_game)
                            else stringResource(R.string.pause)
                        )
                    }
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                matrix.forEachIndexed { i, row ->
                    row.forEachIndexed { j, cell ->
                        val color = when {
                            cell.isAlive -> baseGreen
                            else -> Color.White
                        }
                        drawCircle(
                            color,
                            cellSize / 2f,
                            center = Offset(
                                i * cellSize + cellSize / 2f,
                                j * cellSize + cellSize / 2f
                            )
                        )
                    }
                }
            }
        }
    }
}
