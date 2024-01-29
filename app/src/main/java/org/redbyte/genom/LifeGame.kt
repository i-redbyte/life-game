package org.redbyte.genom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@Composable
fun LifeGame() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth
        val screenHeight = constraints.maxHeight

        val cellSize = screenWidth / 64
        val columnSize = screenWidth / cellSize
        val rowSize = screenHeight / cellSize

        var matrix by remember { mutableStateOf(Array(columnSize) { IntArray(rowSize) }) }
        LaunchedEffect(Unit) {
            generateWorld(matrix, 1680)
            while (true) {
                matrix = getNextStatus(matrix)
                delay(175)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            matrix.forEachIndexed { i, row ->
                row.forEachIndexed { j, cell ->
                    val color = if (cell == 1) Color.Green else Color.White
                    drawCircle(color, cellSize / 2f, center = Offset(i * cellSize + cellSize / 2f, j * cellSize + cellSize / 2f))
                }
            }
        }
    }
}

fun generateWorld(matrix: Array<IntArray>, initialPopulation: Int) {
    repeat(initialPopulation) {
        val x = matrix.indices.random()
        val y = matrix[0].indices.random()
        matrix[x][y] = if (matrix[x][y] == 0) 1 else 0
    }
}

fun getNextStatus(matrix: Array<IntArray>): Array<IntArray> {
    val newMatrix = Array(matrix.size) { IntArray(matrix[0].size) }
    for (i in newMatrix.indices) {
        for (j in newMatrix[0].indices) {
            newMatrix[i][j] = if (isLive(matrix, i, j)) 1 else 0
        }
    }
    return newMatrix
}

fun isLive(matrix: Array<IntArray>, i: Int, j: Int): Boolean {
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
    var liveNeighborsCount = 0
    for (k in ref.indices) {
        val x = i + ref[k][0]
        val y = j + ref[k][1]
        if (x < 0 || y < 0 || x >= matrix.size || y >= matrix[0].size) continue
        liveNeighborsCount += matrix[x][y]
    }
    return if (matrix[i][j] == 0) {
        liveNeighborsCount == 3
    } else {
        liveNeighborsCount in 2..3
    }
}