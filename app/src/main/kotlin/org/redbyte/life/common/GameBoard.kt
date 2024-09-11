package org.redbyte.life.common

import org.redbyte.life.common.data.GameSettings

typealias CellMatrix = List<Long>

class GameBoard(val settings: GameSettings, initialMatrix: CellMatrix? = null) {

    var matrix: CellMatrix = initialMatrix ?: List(settings.height) { 0L }
    private val rule = settings.rule

    init {
        if (initialMatrix == null) {
            populateInitialCells()
        }
    }

    private fun populateInitialCells() {
        val initialCells = (0 until settings.height).flatMap { y ->
            (0 until settings.width).map { x -> x to y }
        }
            .shuffled()
            .take(settings.initialPopulation)
            .toSet()
        matrix = List(settings.height) { y ->
            (0 until settings.width).fold(0L) { row, x ->
                if (x to y in initialCells) row or (1L shl x) else row
            }
        }
    }

    fun update(): GameBoard {
        matrix = matrix.mapIndexed { y, row ->
            row.mapBitsIndexed(settings.width) { x, isAlive ->
                val neighbors = countNeighbors(x, y)
                rule.apply(isAlive, neighbors)
            }
        }
        return this
    }

    fun countLivingCells(): Int =
        matrix.sumOf { row -> row.countBits() }

    internal fun countNeighbors(x: Int, y: Int): Int {
        val directions = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )

        return directions.mapNotNull { (dx, dy) ->
            val nx = x + dx
            val ny = y + dy
            if (nx in 0 until settings.width && ny in 0 until settings.height)
                getCellAlive(nx, ny)
            else
                null
        }.count { it }
    }

    private fun getCellAlive(x: Int, y: Int): Boolean =
        (matrix[y] shr x) and 1L == 1L

    private fun Long.setBitAlive(x: Int, isAlive: Boolean): Long =
        if (isAlive) this or (1L shl x)
        else this and (1L shl x).inv()

    private fun Long.mapBitsIndexed(width: Int, action: (Int, Boolean) -> Boolean): Long {
        var result = 0L
        for (i in 0 until width) {
            val isAlive = (this shr i) and 1L == 1L
            result = result.setBitAlive(i, action(i, isAlive))
        }
        return result
    }

    private fun Long.countBits(): Int =
        this.toString(2).count { it == '1' }

}
