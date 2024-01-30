package org.redbyte.genom

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
import org.redbyte.Cell
import kotlin.random.Random

typealias CellMatrix = Array<Array<Cell>>

@Composable
fun GenomGame() {
    val coroutineScope = rememberCoroutineScope()
    var showTopSheet by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth
        val screenHeight = constraints.maxHeight

        val cellSize = screenWidth / 64
        val columnSize = screenWidth / cellSize
        val rowSize = screenHeight / cellSize
        val aggressiveCount = remember { mutableIntStateOf(0) }
        val peacefulCount = remember { mutableIntStateOf(0) }
        val cannibalCount = remember { mutableIntStateOf(0) }
        val psychoCount = remember { mutableIntStateOf(0) }
        val turnNumber = remember { mutableIntStateOf(0) }

        var matrix by remember {
            mutableStateOf(Array(columnSize) { Array(rowSize) { Cell(false, mutableSetOf()) } })
        }

        LaunchedEffect(key1 = isPaused, key2 = matrix) {
            if (!isPaused) {
                generateWorld(matrix, 1600)
                while (!isPaused) {
                    matrix = getNextStatus(matrix)
                    psychoCount.intValue =
                        matrix.sumOf { row -> row.count { it.isAlive && it.genes.contains(4) } }
                    peacefulCount.intValue =
                        matrix.sumOf { row -> row.count { it.isAlive && it.genes.contains(6) } }
                    cannibalCount.intValue =
                        matrix.sumOf { row -> row.count { it.isAlive && it.genes.contains(7) } }
                    aggressiveCount.intValue =
                        matrix.sumOf { row -> row.count { it.isAlive && it.genes.contains(8) } }
                    turnNumber.intValue++
                    delay(150)
                }
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

//fun generateWorld(matrix: CellMatrix, initialPopulation: Int) {
//    repeat(initialPopulation) {
//        val x = matrix.indices.random()
//        val y = matrix[0].indices.random()
//        matrix[x][y].isAlive = true
//        matrix[x][y].genes = mutableSetOf(setOf(6, 8).random())
//    }
//}
fun generateWorld(matrix: CellMatrix, initialPopulation: Int) {
    var populated = 0
    val maxAttempts = initialPopulation * 10 // Установите максимальное количество попыток для избежания бесконечного цикла
    var attempts = 0

    while (populated < initialPopulation && attempts < maxAttempts) {
        val x = matrix.indices.random()
        val y = matrix[0].indices.random()

        if (!matrix[x][y].isAlive) {
            matrix[x][y].isAlive = true
            matrix[x][y].genes = mutableSetOf(setOf(6, 8).random())
            populated++
        }
        attempts++
    }
}

fun getNextStatus(matrix: CellMatrix): CellMatrix {
    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { Cell(false, mutableSetOf()) } }
    for (i in matrix.indices) {
        for (j in matrix[0].indices) {
            val cell = matrix[i][j]
            val neighbors = getNeighbors(matrix, i, j)
            val newCell = newMatrix[i][j]
            newCell.isAlive = getNewStatus(cell, neighbors)
            if (newCell.isAlive) {
                newCell.genes = cell.genes
            }
        }
    }
    return newMatrix
}

fun getNeighbors(matrix: CellMatrix, x: Int, y: Int): List<Cell> {
    val neighbors = mutableListOf<Cell>()
    val ref = arrayOf(
        intArrayOf(-1, -1),
        intArrayOf(-1, 0),
        intArrayOf(-1, 1),
        intArrayOf(0, -1),
        intArrayOf(0, 1),
        intArrayOf(1, -1),
        intArrayOf(1, 0),
        intArrayOf(1, 1)
    )
    for (k in ref.indices) {
        val newX = x + ref[k][0]
        val newY = y + ref[k][1]
        if (newX in matrix.indices && newY in matrix[0].indices) {
            neighbors.add(matrix[newX][newY])
        }
    }
    return neighbors
}

fun getNewStatus(cell: Cell, neighbors: List<Cell>): Boolean {
    val aliveNeighbors = neighbors.count { it.isAlive }
    val peacefulNeighbors = neighbors.filter { it.genes.contains(6) }
    val aggressiveNeighbors = neighbors.filter { it.genes.contains(8) }
    val aggressiveCount = neighbors.count { it.genes.contains(8) }
    val cannibalCount = neighbors.count { it.genes.contains(7) }
    val cannibalNeighbors = neighbors.filter { it.genes.contains(7) }

    return when {
        cell.genes.contains(4) -> {
            if (cell.turnsLived < 5) {
                val target = aggressiveNeighbors.ifEmpty { cannibalNeighbors }.firstOrNull()
                target?.isAlive = false
                cell.turnsLived++
                return true
            } else {
                return false // death
            }
        }

        cell.genes.contains(6) -> {
            when {
                Random.nextInt(100) < 2 -> {
                    cell.genes.remove(6)
                    cell.genes.add(4)
                    cell.turnsLived = 0
                    true
                }
                cell.isAlive -> aliveNeighbors in 2..3
                else -> false
            }
        }

        cell.genes.contains(8) -> {
            val canReproduce = peacefulNeighbors.isNotEmpty() || cannibalNeighbors.isNotEmpty() && aggressiveCount in 2..3
            if (canReproduce && Random.nextInt(100) < 5) {
                cell.genes.add(7)
                cell.genes.remove(8)
            }
            canReproduce
        }

        cell.genes.contains(7) -> {
            val hasVictims = peacefulNeighbors.isNotEmpty() || aggressiveNeighbors.isNotEmpty() && cannibalCount in 2..3
            val surroundedByPeaceful = peacefulNeighbors.size >= 4
            val noNeighbors = neighbors.all { !it.isAlive }
            hasVictims && !surroundedByPeaceful && !noNeighbors
        }

        else -> false
    }
}
