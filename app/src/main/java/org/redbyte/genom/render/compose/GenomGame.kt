package org.redbyte.genom.render.compose

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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.redbyte.genom.common.GameBoard

@Composable
fun GenomGame(gameBoard: GameBoard) {
    val coroutineScope = rememberCoroutineScope()
    var showTopSheet by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth
        val cellSize = screenWidth / gameBoard.width
        val aggressiveCount = remember { mutableIntStateOf(0) }
        val peacefulCount = remember { mutableIntStateOf(0) }
        val cannibalCount = remember { mutableIntStateOf(0) }
        val psychoCount = remember { mutableIntStateOf(0) }
        val turnNumber = remember { mutableIntStateOf(0) }
        var matrix by remember { mutableStateOf(gameBoard.matrix) }

        LaunchedEffect(key1 = isPaused, key2 = matrix) {
            while (!isPaused) {
                psychoCount.intValue =
                    matrix.sumOf { row -> row.count { it.isAlive && it.genes.contains(4) } }
                peacefulCount.intValue =
                    matrix.sumOf { row -> row.count { it.isAlive && it.genes.contains(6) } }
                cannibalCount.intValue =
                    matrix.sumOf { row -> row.count { it.isAlive && it.genes.contains(7) } }
                aggressiveCount.intValue =
                    gameBoard.matrix.sumOf { row -> row.count { it.isAlive && it.genes.contains(8) } }
                turnNumber.intValue++
                delay(250)
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
                    Text("Ход: ${turnNumber.intValue}")
                    Text("Агрессоры: ${aggressiveCount.intValue}")
                    Text("Каннибалы: ${cannibalCount.intValue}")
                    Text("Пацифисты: ${peacefulCount.intValue}")
                    Text("Психи: ${psychoCount.intValue}")
                    Button(onClick = { isPaused = !isPaused }) {
                        Text(if (isPaused) "Продолжить" else "Пауза")
                    }
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                matrix.forEachIndexed { i, row ->
                    row.forEachIndexed { j, cell ->
                        val color = when {
                            cell.isAlive && cell.genes.contains(4) -> Color(
                                android.graphics.Color.parseColor(
                                    "#FFFFC107"
                                )
                            )

                            cell.isAlive && cell.genes.contains(6) -> Color.Green
                            cell.isAlive && cell.genes.contains(7) -> Color.Blue
                            cell.isAlive && cell.genes.contains(8) -> Color.Red
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
