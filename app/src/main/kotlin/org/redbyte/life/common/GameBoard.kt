package org.redbyte.life.common

import org.redbyte.life.common.data.GameSettings
import org.redbyte.life.common.domain.Rule
import kotlin.random.Random

typealias CellMatrix = List<Long>

class GameBoard(val settings: GameSettings, val rule: Rule) {

    var matrix: CellMatrix = List(settings.height) { 0L }

    init {
        populateInitialCells()
    }

    private fun populateInitialCells() {
        matrix = List(matrix.size) { y ->
            val row = (0 until settings.width).map { Random.nextBoolean() }
                .take(settings.initialPopulation)
            row.fold(0L) { acc, isAlive ->
                acc shl 1 or (if (isAlive) 1L else 0L)
            }
        }
    }

    fun update(): GameBoard {
        matrix = matrix.mapIndexed { y, row ->
            row.mapBitsIndexed { x, isAlive ->
                val neighbors = countNeighbors(x, y)
                rule.apply(isAlive, neighbors)
            }
        }
        return this
    }

    fun countLivingCells(): Int =
        matrix.sumOf { row -> row.countBits() }

    private fun countNeighbors(x: Int, y: Int): Int {
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

    private fun Long.mapBitsIndexed(action: (Int, Boolean) -> Boolean): Long {
        var result = 0L
        for (i in 0 until Long.SIZE_BITS) {
            val isAlive = (this shr i) and 1L == 1L
            result = result.setBitAlive(i, action(i, isAlive))
        }
        return result
    }

    private fun Long.countBits(): Int =
        this.toString(2).count { it == '1' }

}
