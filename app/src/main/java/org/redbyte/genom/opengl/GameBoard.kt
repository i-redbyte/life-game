package org.redbyte.genom.opengl

class GameBoard(val width: Int, val height: Int) {
    val cells: Array<Array<Boolean>> = Array(height) { Array(width) { false } }
    private val newCells: Array<Array<Boolean>> = Array(height) { Array(width) { false } }

    init {
        for (i in 0 until height) {
            for (j in 0 until width) {
                cells[i][j] = Math.random() > 0.5
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

    private fun countNeighbors(x: Int, y: Int): Int {
        val directions = arrayOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )

        var neighborCount = 0

        for ((dx, dy) in directions) {
            val neighborX = x + dx
            val neighborY = y + dy

            if (neighborX in 0..<width && neighborY >= 0 && neighborY < height) {
                if (cells[neighborY][neighborX]) {
                    neighborCount++
                }
            }
        }

        return neighborCount
    }

}
