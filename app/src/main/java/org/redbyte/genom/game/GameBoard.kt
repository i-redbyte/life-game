package org.redbyte.genom.game

import android.util.Log
import org.redbyte.genom.game.data.Cell
import org.redbyte.genom.game.data.GameSettings

typealias CellMatrix = Array<Array<Cell>>

class GameBoard(
    val width: Int,
    val height: Int,
    val initialPopulation: Int,
    settings: GameSettings? // TODO: add settings
) {

    val matrix: CellMatrix = Array(width) { Array(height) { Cell(false, mutableSetOf(6)) } }
    private val newMatrix = Array(width) { Array(height) { Cell(false, mutableSetOf(6)) } }

    init {
        var populated = 0
        val maxCount = matrix.size * matrix[0].size
        val pCount = if (initialPopulation > maxCount) maxCount / 10 else initialPopulation
        while (populated < pCount) {
            val x = matrix.indices.random()
            val y = matrix[0].indices.random()
            if (!matrix[x][y].isAlive) {
                matrix[x][y].isAlive = true
                populated++
            }
        }
    }

    fun update() {
        for (i in matrix.indices) {
            for (j in matrix[0].indices) {
                val neighbors = countNeighbors(i, j)
                val cell = matrix[i][j]
                newMatrix[i][j].isAlive =
                    if (matrix[i][j].isAlive) neighbors in 2..3 else neighbors == 3
                if (newMatrix[i][j].isAlive) {
                    newMatrix[i][j].turnsLived = cell.turnsLived
                    newMatrix[i][j].genes = cell.genes
                }
            }
        }
        for (i in matrix.indices) {
            for (j in matrix[0].indices) {
                matrix[i][j] = newMatrix[i][j]
            }
        }
        Log.d("_debug", "V ==== ${matrix.flatten().count { it.isAlive }} ")
    }

    fun countLivingCells() = matrix.flatten().count { it.isAlive }

    private fun countNeighbors(x: Int, y: Int): Int {
        val directions = arrayOf(
            Pair(-1, -1),
            Pair(-1, 0),
            Pair(-1, 1),
            Pair(0, -1),
            Pair(0, 1),
            Pair(1, -1),
            Pair(1, 0),
            Pair(1, 1)
        )
        var neighborCount = 0
        for ((dx, dy) in directions) {
            val neighborX = x + dx
            val neighborY = y + dy

            if (neighborX in 0 until height && neighborY in 0 until width) {
                if (matrix[neighborY][neighborX].isAlive) {
                    neighborCount++
                }
            }
        }
        return neighborCount
    }

}

// TODO: implement logic to game board
//private fun generateWorld(matrix: CellMatrix, initialPopulation: Int) {
//    var populated = 0
//    val maxAttempts = initialPopulation * 10
//    var attempts = 0
//
//    while (populated < initialPopulation && attempts < maxAttempts) {
//        val x = matrix.indices.random()
//        val y = matrix[0].indices.random()
//
//        if (!matrix[x][y].isAlive) {
//            matrix[x][y].isAlive = true
//            when {
//                gameSettings.hasAllCells() -> matrix[x][y].genes =
//                    mutableSetOf(setOf(6, 8).random())
//
//                gameSettings.isPacificOnly() -> matrix[x][y].genes = mutableSetOf(6)
//                gameSettings.isAggressorsOnly() -> matrix[x][y].genes = mutableSetOf(8)
//            }
//            populated++
//        }
//        attempts++
//    }
//}

//private fun getNextStatus(matrix: CellMatrix): CellMatrix {
//    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { Cell(false, mutableSetOf()) } }
//    for (i in matrix.indices) {
//        for (j in matrix[0].indices) {
//            val cell = matrix[i][j]
//            val newCell = newMatrix[i][j]
//            newCell.isAlive = newStatus(cell, getNeighbors(matrix, i, j))
//            if (newCell.isAlive) {
//                newCell.genes = cell.genes
//                newCell.turnsLived = cell.turnsLived
//            }
//        }
//    }
//    return newMatrix
//}
//
//private fun getNeighbors(matrix: CellMatrix, x: Int, y: Int): List<Cell> {
//    val neighbors = mutableListOf<Cell>()
//    val ref = arrayOf(
//        intArrayOf(-1, -1),
//        intArrayOf(-1, 0),
//        intArrayOf(-1, 1),
//        intArrayOf(0, -1),
//        intArrayOf(0, 1),
//        intArrayOf(1, -1),
//        intArrayOf(1, 0),
//        intArrayOf(1, 1)
//    )
//    for (k in ref.indices) {
//        val newX = x + ref[k][0]
//        val newY = y + ref[k][1]
//        if (newX in matrix.indices && newY in matrix[0].indices) {
//            neighbors.add(matrix[newX][newY])
//        }
//    }
//    return neighbors
//}
//
//private fun newStatus(cell: Cell, neighbors: List<Cell>): Boolean {
//    val aliveNeighbors = neighbors.count { it.isAlive }
//    val peacefulNeighbors = neighbors.filter { it.genes.contains(6) }
//    val aggressiveNeighbors = neighbors.filter { it.genes.contains(8) }
//    val aggressiveCount = neighbors.count { it.genes.contains(8) }
//    val cannibalCount = neighbors.count { it.genes.contains(7) }
//    val cannibalNeighbors = neighbors.filter { it.genes.contains(7) }
//
//    return when {
//        cell.genes.contains(4) -> {
//            if (cell.turnsLived < 10) {
//                val target = aggressiveNeighbors.ifEmpty { cannibalNeighbors }.firstOrNull()
//                target?.isAlive = false
//                cell.turnsLived++
//                return true
//            } else {
//                return false // death
//            }
//        }
//
//        cell.genes.contains(6) -> {
//            when {
//                gameSettings.allowMutations && Random.nextInt(100) < 2 -> {
//                    cell.genes.remove(6)
//                    cell.genes.add(4)
//                    cell.turnsLived = 0
//                    true
//                }
//
//                cell.isAlive -> {
//                    Log.d("_debug", "${aliveNeighbors in 2..3} ");
//                    aliveNeighbors in 2..3
//                }
//
//                !cell.isAlive -> {
//                    aliveNeighbors == 3
//                }
//
//                else -> false
//            }
//        }
//
//        cell.genes.contains(8) -> {
//            val canReproduce =
//                peacefulNeighbors.isNotEmpty() || cannibalNeighbors.isNotEmpty() && aggressiveCount in 2..3
//            if (gameSettings.allowMutations && canReproduce && Random.nextInt(100) < 5) {
//                cell.genes.add(7)
//                cell.genes.remove(8)
//            }
//            canReproduce
//        }
//
//        cell.genes.contains(7) -> {
//            val hasVictims =
//                peacefulNeighbors.isNotEmpty() || aggressiveNeighbors.isNotEmpty() && cannibalCount in 2..3
//            val surroundedByPeaceful = peacefulNeighbors.size >= 4
//            val noNeighbors = neighbors.all { !it.isAlive }
//            hasVictims && !surroundedByPeaceful && !noNeighbors
//        }
//
//        else -> false
//    }
//}
