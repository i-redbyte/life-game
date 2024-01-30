package org.redbyte.genom
import org.junit.Assert.*
import org.junit.Test
import org.redbyte.Cell

class GenomGameTest {
    @Test
    fun testGenerateWorld() {
        val matrix = Array(10) { Array(10) { Cell(false, mutableSetOf()) } }
        val initialPopulation = 20

        generateWorld(matrix, initialPopulation)
        val aliveCells = matrix.sumOf { row -> row.count { it.isAlive } }
        assertEquals(initialPopulation, aliveCells)
    }

    @Test
    fun testGenerateWorld_populationCount() {
        val matrix = Array(10) { Array(10) { Cell(false, mutableSetOf()) } }
        val initialPopulation = 20

        generateWorld(matrix, initialPopulation)

        val aliveCells = matrix.sumOf { row -> row.count { it.isAlive } }
        assertEquals(initialPopulation, aliveCells)
    }

    @Test
    fun testGenerateWorld_validGenes() {
        val matrix = Array(10) { Array(10) { Cell(false, mutableSetOf()) } }
        generateWorld(matrix, 10)

        assertTrue(matrix.any { row -> row.any { it.isAlive && (it.genes.contains(6) || it.genes.contains(8)) } })
    }

}

