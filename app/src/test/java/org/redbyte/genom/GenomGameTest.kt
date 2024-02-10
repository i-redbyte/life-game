package org.redbyte.genom

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.redbyte.genom.game.data.Cell

class GenomGameTest {
    private lateinit var matrix: CellMatrix

    @Before
    fun setUp() {
        matrix = Array(10) { Array(10) { Cell(false, mutableSetOf()) } }
    }

    @Test
    fun generateWorld_populatesCorrectNumberOfCells() {
        val initialPopulation = 100
        generateWorld(matrix, initialPopulation)
        val actualPopulation = matrix.sumOf { it.count { cell -> cell.isAlive } }
        assertEquals(
            "Количество инициализированных клеток не соответствует ожидаемому",
            initialPopulation,
            actualPopulation
        )
    }

    @Test
    fun testGetNextStatus() {
        matrix[5][5] = Cell(true, mutableSetOf(6))
        matrix[4][4] = Cell(true, mutableSetOf(6))
        matrix[4][5] = Cell(true, mutableSetOf(6))
        matrix[4][6] = Cell(true, mutableSetOf(6))
        val newMatrix = getNextStatus(matrix)
        assertTrue("Клетка должна остаться живой", newMatrix[5][5].isAlive)
    }

    @Test
    fun getNeighbors_returnsCorrectNeighborsForCentralCell() {
        val neighbors = getNeighbors(matrix, 5, 5)
        assertEquals(
            "Количество соседей для центральной клетки не соответствует ожидаемому",
            8,
            neighbors.size
        )
    }

    @Test
    fun getNextStatus_changesPopulationCorrectly() {
        generateWorld(matrix, 50)
        val originalPopulation = matrix.sumOf { it.count { cell -> cell.isAlive } }
        val newMatrix = getNextStatus(matrix)
        val newPopulation = newMatrix.sumOf { it.count { cell -> cell.isAlive } }
        assertNotEquals(
            "Популяция не изменилась после обновления статуса",
            originalPopulation,
            newPopulation
        )
    }

}
