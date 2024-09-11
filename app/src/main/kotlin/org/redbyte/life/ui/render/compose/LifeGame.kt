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

private const val DELAY_UPDATE_WORLD = 150L

@Composable
fun LifeGame(viewModel: SharedGameSettingsViewModel) {
    fun Long.countBits(): Int = this.toString(2).count { it == '1' }

    val initialBoard = remember { viewModel.getGameBoard() }
    viewModel.resetGameBoard()

    val coroutineScope = rememberCoroutineScope()

    var showTopSheet by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var turnNumber by remember { mutableIntStateOf(0) }
    var cellCount by remember { mutableIntStateOf(0) }
    var matrix by remember { mutableStateOf(initialBoard.matrix) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth
        val cellSize = screenWidth / initialBoard.settings.width

        LaunchedEffect(isPaused) {
            if (!isPaused) {
                while (true) {
                    delay(DELAY_UPDATE_WORLD)
                    val updatedBoard = initialBoard.update()
                    matrix = updatedBoard.matrix
                    cellCount = updatedBoard.matrix.sumOf { row -> row.countBits() }
                    turnNumber++
                }
            }
        }

        Column(
            modifier = Modifier.pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    coroutineScope.launch {
                        if (dragAmount > 0 && !showTopSheet) showTopSheet = true
                        else if (dragAmount < 0 && showTopSheet) showTopSheet = false
                    }
                }
            }
        ) {
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
                    Text(stringResource(R.string.turn, turnNumber))
                    Button(onClick = { isPaused = !isPaused }) {
                        Text(
                            if (isPaused) stringResource(R.string.continue_game)
                            else stringResource(R.string.pause)
                        )
                    }
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                matrix.forEachIndexed { rowIndex, row ->
                    repeat(initialBoard.settings.width) { colIndex ->
                        val isCellAlive = (row shr colIndex) and 1L == 1L
                        val color = if (isCellAlive) baseGreen else Color.White
                        drawCircle(
                            color = color,
                            radius = cellSize / 2f,
                            center = Offset(
                                colIndex * cellSize + cellSize / 2f,
                                rowIndex * cellSize + cellSize / 2f
                            )
                        )
                    }
                }
            }
        }
    }
}



