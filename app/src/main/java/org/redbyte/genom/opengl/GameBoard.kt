package org.redbyte.genom.opengl

import android.util.Log

class GameBoard(val width: Int, val height: Int, val initialPopulation: Int) {
    val cells: Array<Array<Boolean>> = Array(height) { Array(width) { false } }
    private val newCells: Array<Array<Boolean>> = Array(height) { Array(width) { false } }

    init {
        var populated = 0
        val maxCount = cells.size * cells[0].size
        val pCount = if (initialPopulation > maxCount) maxCount / 10 else initialPopulation
        while (populated < pCount) {
            val x = cells.indices.random()
            val y = cells[0].indices.random()
            if (!cells[x][y]) {
                cells[x][y] = true
                populated++
            }
        }
    }

    fun update() {
        for (i in 0 until height) {
            for (j in 0 until width) {
                val neighbors = countNeighbors(i, j)
                newCells[i][j] = if (cells[i][j]) neighbors in 2..3 else neighbors == 3
            }
        }
        for (i in 0 until height) {
            for (j in 0 until width) {
                cells[i][j] = newCells[i][j]
            }
        }
    }

    fun countLivingCells() = cells.flatten().count { it }
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

            if (neighborX in 0 until width && neighborY in 0 until height) {
                if (cells[neighborY][neighborX]) {
                    neighborCount++
                }
            }
        }
        return neighborCount
    }

}


