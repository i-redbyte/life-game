package org.redbyte.genom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import org.redbyte.Cell

typealias CellMatrix = Array<Array<Cell>>
@Composable
fun GenomGame() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth
        val screenHeight = constraints.maxHeight

        val cellSize = screenWidth / 64
        val columnSize = screenWidth / cellSize
        val rowSize = screenHeight / cellSize
        val aggressiveCount = remember { mutableIntStateOf(0) }
        val peacefulCount = remember { mutableIntStateOf(0) }
        val turnNumber = remember { mutableIntStateOf(0) }

        var matrix by remember {
            mutableStateOf(CellMatrix(columnSize) {
                Array(rowSize) {
                    Cell(
                        false,
                        emptySet()
                    )
                }
            })
        }
        LaunchedEffect(key1 = matrix) {
            generateWorld(matrix, 1600)
            while (true) {
                matrix = getNextStatus(matrix)
                aggressiveCount.value =
                    matrix.sumOf { row -> row.count { it.isAlive && it.genes.contains(8) } }
                peacefulCount.value =
                    matrix.sumOf { row -> row.count { it.isAlive && it.genes.contains(6) } }
                turnNumber.value++
                delay(175)
            }
        }
        Column {
            Text("Агрессивные клетки: ${aggressiveCount.value}")
            Text("Пацифисты: ${peacefulCount.value}")
            Text("Ход номер: ${turnNumber.value}")

            Canvas(modifier = Modifier.fillMaxSize()) {
                matrix.forEachIndexed { i, row ->
                    row.forEachIndexed { j, cell ->
                        val color = when {
                            cell.isAlive && cell.genes.contains(6) -> Color.Green
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

fun generateWorld(matrix: CellMatrix, initialPopulation: Int) {
    repeat(initialPopulation) {
        val x = matrix.indices.random()
        val y = matrix[0].indices.random()
        matrix[x][y].isAlive = true
        matrix[x][y].genes = setOf(setOf(6, 8).random())
    }
}

fun getNextStatus(matrix: CellMatrix): CellMatrix {
    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { Cell(false, emptySet()) } }
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

    return when {
        cell.genes.contains(6) -> {
            when {
                cell.isAlive -> aliveNeighbors in 2..3
                else -> aliveNeighbors == 3
            }
        }

        cell.genes.contains(8) -> {
            when {
                aggressiveNeighbors.isNotEmpty() && peacefulNeighbors.isEmpty() -> false
                peacefulNeighbors.isNotEmpty() -> true
                else -> cell.isAlive && aliveNeighbors in 2..3
            }
        }

        else -> cell.isAlive && aliveNeighbors in 2..3
    }
}
