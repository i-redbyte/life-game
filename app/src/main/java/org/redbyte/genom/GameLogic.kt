package org.redbyte.genom

import kotlin.random.Random

class GameLogic(private val width: Int, private val height: Int) {
    var cells: Array<Array<Cell>> = Array(width) { x ->
        Array(height) { y ->
            Cell(
                genes = generateRandomGenes(),
                type = if (Random.nextBoolean()) CellType.randomType() else CellType.DEAD,
                isAlive = Random.nextBoolean(),
                x = x,
                y = y
            )
        }
    }

    fun nextTurn() {
        val newCells = Array(width) { x ->
            Array(height) { y ->
                processCell(cells[x][y])
            }
        }
        cells = newCells
    }

    private fun processCell(cell: Cell): Cell {
        val copy = cell.copy()
        if (!copy.isAlive) return copy

        val neighbors = getNeighbors(copy)

        when (copy.type) {
            CellType.SUICIDAL -> handleSuicidalCell(copy, neighbors)
            CellType.AGGRESSIVE -> handleAggressiveCell(copy, neighbors)
            CellType.PEACEFUL -> handlePeacefulCell(copy, neighbors)
            CellType.SCAVENGER -> handleScavengerCell(copy, neighbors)
            CellType.DEAD -> { /* no-op */
            }
        }

        additionalChecksAndUpdate(copy, neighbors)

        return copy
    }

    private fun additionalChecksAndUpdate(cell: Cell, neighbors: List<Cell>) {
        survivalCheck(cell, neighbors)
        reproductionCheck(cell, neighbors)
        mimicryCheck(cell, neighbors)
        sociumCheck(cell, neighbors)
        individualismCheck(cell, neighbors)
        randomDivisionCheck(cell)
        corpseProcessing(cell, neighbors)
        stepCountCheck(cell)
    }

    private fun stepCountCheck(cell: Cell) {
        cell.stepsSinceLastReproduction++

        if (9 in cell.genes && cell.stepsSinceLastReproduction > 30) {
            cell.isAlive = false
        }
    }

    private fun corpseProcessing(cell: Cell, neighbors: List<Cell>) {
        if (cell.type == CellType.DEAD) {
            cell.turnsAsCorpse++
            if (cell.turnsAsCorpse > 5) {
                removeCorpse(cell)
            }
            if (neighbors.any { it.isAlive && it.type == CellType.SCAVENGER }) {
                removeCorpse(cell)
            }
        }
    }

    fun removeCorpse(cell: Cell) {
        cell.isAlive = false
        cell.type = CellType.DEAD
        cell.turnsAsCorpse = 0
    }

    fun randomDivisionCheck(cell: Cell) {
        if (8 in cell.genes) {
            if (Random.nextDouble() < 0.1) {
                reproduce(cell)
            }
        }
    }

    private fun individualismCheck(cell: Cell, neighbors: List<Cell>) {
        if (6 in cell.genes) {
            val sameTypeNeighbors = neighbors.count { it.isAlive && it.type == cell.type }

            if (sameTypeNeighbors > 1) {
                moveAwayFromSameType(cell)
            }
        }
    }

    private fun moveAwayFromSameType(cell: Cell) {
        val directions = listOf(-1, 0, 1)
        var minCount = Int.MAX_VALUE
        var bestPosition: Pair<Int, Int>? = null

        for (dx in directions) {
            for (dy in directions) {
                if (dx == 0 && dy == 0) continue

                val nx = cell.x + dx
                val ny = cell.y + dy

                if (nx in 0..<width && ny >= 0 && ny < height) {
                    val neighborCount = getNeighbors(nx, ny).count {
                        it.isAlive && it.type == cell.type
                    }

                    if (neighborCount < minCount && cells[nx][ny].type == CellType.DEAD) {
                        minCount = neighborCount
                        bestPosition = Pair(nx, ny)
                    }
                }
            }
        }

        bestPosition?.let { (newX, newY) ->
            cells[newX][newY] = cell.copy()
            cells[cell.x][cell.y] = Cell(generateRandomGenes(), CellType.DEAD)
        }
    }

    private fun sociumCheck(cell: Cell, neighbors: List<Cell>) {
        if (5 in cell.genes) {
            val sameTypeNeighbors = neighbors.count { it.isAlive && it.type == cell.type }
            if (sameTypeNeighbors < 2) {
                moveToSocium(cell)
            }
        }
    }

    private fun moveToSocium(cell: Cell) {
        val directions = listOf(-1, 0, 1)
        var maxCount = 0
        var bestPosition: Pair<Int, Int>? = null

        for (dx in directions) {
            for (dy in directions) {
                if (dx == 0 && dy == 0) continue

                val nx = cell.x + dx
                val ny = cell.y + dy

                if (nx in 0..<width && ny >= 0 && ny < height) {
                    val neighborCount = getNeighbors(nx, ny).count {
                        it.isAlive && it.type == cell.type
                    }

                    if (neighborCount > maxCount && cells[nx][ny].type == CellType.DEAD) {
                        maxCount = neighborCount
                        bestPosition = Pair(nx, ny)
                    }
                }
            }
        }

        bestPosition?.let { (newX, newY) ->
            cells[newX][newY] = cell.copy()
            cells[cell.x][cell.y] = Cell(generateRandomGenes(), CellType.DEAD)
        }
    }

    private fun mimicryCheck(cell: Cell, neighbors: List<Cell>) {
        if (MIMICRY_GEN in cell.genes) {
            val mostCommonType = neighbors
                .filter { it.isAlive }
                .groupingBy { it.type }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key

            if (mostCommonType != null && mostCommonType != CellType.DEAD) {
                cell.type = mostCommonType
            }
        }
    }

    private fun reproductionCheck(cell: Cell, neighbors: List<Cell>) {
        if (4 in cell.genes && cell.shouldDivide) {
            reproduce(cell)
            cell.shouldDivide = false
        }

        when (cell.type) {
            CellType.PEACEFUL -> {
                if (neighbors.count { it.isAlive && it.type == CellType.PEACEFUL } >= 3) {
                    reproduce(cell)
                }
            }

            CellType.SCAVENGER -> {
                if (cell.eatenDeadCells >= 3) {
                    reproduce(cell)
                    cell.eatenDeadCells = 0
                }
            }

            CellType.AGGRESSIVE -> {
                if (cell.hasSuccessfullyAttacked) {
                    reproduce(cell)
                    cell.hasSuccessfullyAttacked = false
                }
            }

            CellType.SUICIDAL -> {
                if (neighbors.count { it.isAlive } in 2..3) {
                    reproduce(cell)
                }
            }

            CellType.DEAD -> {}
        }
    }

    private fun handleAggressiveCell(cell: Cell, neighbors: List<Cell>, x: Int, y: Int) {
        val nonAggressiveNeighbors =
            neighbors.filter { it.type != CellType.AGGRESSIVE && it.isAlive }

        val targetCell = if (nonAggressiveNeighbors.isNotEmpty()) {
            nonAggressiveNeighbors.random()
        } else {
            neighbors.filter { it.isAlive }.randomOrNull()
        }

        targetCell?.let {
            it.isAlive = false
            it.type = CellType.DEAD

            reproduce(cell)
        }
    }


    private fun handlePeacefulCell(cell: Cell, neighbors: List<Cell>, x: Int, y: Int) {
        val peacefulNeighbors = neighbors.count { it.isAlive && it.type == CellType.PEACEFUL }

        when {
            peacefulNeighbors >= 2 -> {
                reproducePeacefulCell(x, y)
            }

            peacefulNeighbors == 1 -> {
                moveAwayFromAggressiveCells(cell)
            }
        }
    }

    private fun reproducePeacefulCell(x: Int, y: Int) {
        val freeNeighbors = getFreeNeighborPositions(x, y)

        if (freeNeighbors.isNotEmpty()) {
            val (newX, newY) = freeNeighbors.random()

            val newCell = Cell(generateRandomGenes(), CellType.PEACEFUL)
            cells[newX][newY] = newCell
        }
    }

    private fun survivalCheck(cell: Cell, neighbors: List<Cell>) {
        // TODO: change magic numbers
        if (9 in cell.genes) {
            handleCunningGene(cell)
        }
        if (neighbors.count { it.isAlive } > 4) {
            cell.isAlive = false
        }
        if (neighbors.count { it.isAlive } < 2) {
            cell.isAlive = false
        }
        if (6 in cell.genes) {
            if (neighbors.count { it.type == cell.type } > 3) {
                cell.isAlive = false
            }
        }

        if (5 in cell.genes) {
            if (neighbors.count { it.type == cell.type } < 2) {
                cell.isAlive = false
            }
        }
        if (8 in cell.genes) {
            if (Random.nextDouble() < 0.1) {
                cell.shouldDivide = true
            }
        }
    }

    private fun handleCunningGene(cell: Cell) {
        cell.stepsSinceLastReproduction++
        if (cell.stepsSinceLastReproduction > 30) {
            cell.isAlive = false
        }
    }

    private fun handleSuicidalCell(cell: Cell, neighbors: List<Cell>) {
        val livingNeighbors = neighbors.count { it.isAlive }
        val suicideProbability = livingNeighbors / 2.0
        if (Random.nextDouble() < suicideProbability) {
            cell.isAlive = false
            cell.type = CellType.DEAD
        }
    }

    private fun handleAggressiveCell(cell: Cell, neighbors: List<Cell>) {
        val nonAggressiveNeighbors =
            neighbors.filter { it.type != CellType.AGGRESSIVE && it.isAlive }

        val targetCell =
            nonAggressiveNeighbors.randomOrNull() ?: neighbors.filter { it.isAlive }.randomOrNull()

        targetCell?.let {
            it.isAlive = false
            it.type = CellType.DEAD
            reproduce(cell)
            cell.hasSuccessfullyAttacked = true
        }
    }

    private fun handlePeacefulCell(cell: Cell, neighbors: List<Cell>) {
        val peacefulNeighbors = neighbors.count { it.isAlive && it.type == CellType.PEACEFUL }

        when {
            peacefulNeighbors >= 2 -> reproduce(cell)
            peacefulNeighbors == 1 -> moveAwayFromAggressiveCells(cell)
        }
    }

    private fun moveAwayFromAggressiveCells(cell: Cell) {
        val leastAggressiveDirection = getLeastAggressiveDirection(cell.x, cell.y)

        leastAggressiveDirection?.let { (newX, newY) ->
            if (cells[newX][newY].type == CellType.DEAD) {
                cells[newX][newY] = cell.copy()
                cell.isAlive = false
            }
        }
    }

    private fun getLeastAggressiveDirection(x: Int, y: Int): Pair<Int, Int>? {
        var minAggressiveCount = Int.MAX_VALUE
        var bestPosition: Pair<Int, Int>? = null

        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx == 0 && dy == 0) continue

                val nx = x + dx
                val ny = y + dy

                if (nx in 0 until width && ny in 0 until height) {
                    val aggressiveCount =
                        getNeighbors(nx, ny).count { it.type == CellType.AGGRESSIVE }
                    if (aggressiveCount < minAggressiveCount && cells[nx][ny].type == CellType.DEAD) {
                        minAggressiveCount = aggressiveCount
                        bestPosition = Pair(nx, ny)
                    }
                }
            }
        }

        return bestPosition
    }

    private fun getNeighbors(x: Int, y: Int): List<Cell> {
        val neighbors = mutableListOf<Cell>()

        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx == 0 && dy == 0) {
                    continue
                }

                val nx = x + dx
                val ny = y + dy

                if (nx in 0..<width && ny >= 0 && ny < height) {
                    neighbors.add(cells[nx][ny])
                }
            }
        }

        return neighbors
    }

    private fun handleScavengerCell(cell: Cell, neighbors: List<Cell>) {
        val deadNeighbors = neighbors.filter { it.type == CellType.DEAD }
        cell.eatenDeadCells += deadNeighbors.size

        deadNeighbors.forEach { it.isAlive = false }

        if (cell.eatenDeadCells >= 3) {
            reproduce(cell)
            cell.eatenDeadCells = 0
        }

        if (neighbors.count { it.type == CellType.PEACEFUL } >= 4) {
            cell.type = CellType.PEACEFUL
        }
    }

    private fun getNeighbors(cell: Cell): List<Cell> {
        val neighbors = mutableListOf<Cell>()

        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx == 0 && dy == 0) {
                    continue
                }

                val nx = cell.x + dx
                val ny = cell.y + dy

                if (nx in 0..<width && ny >= 0 && ny < height) {
                    neighbors.add(cells[nx][ny])
                }
            }
        }

        return neighbors
    }

    private fun reproduce(cell: Cell) {
        val freeNeighbors = getFreeNeighborPositions(cell.x, cell.y)
        if (freeNeighbors.isNotEmpty()) {
            val (newX, newY) = freeNeighbors.random()
            val newCell = Cell(generateRandomGenes(), CellType.AGGRESSIVE)
            cells[newX][newY] = newCell
        }
    }

    private fun getFreeNeighborPositions(x: Int, y: Int): List<Pair<Int, Int>> {
        val freePositions = mutableListOf<Pair<Int, Int>>()

        for (dx in -1..1) {
            for (dy in -1..1) {
                val nx = x + dx
                val ny = y + dy

                if ((dx != 0 || dy != 0) && nx >= 0 && nx < width && ny >= 0 && ny < height && !cells[nx][ny].isAlive) {
                    freePositions.add(Pair(nx, ny))
                }
            }
        }

        return freePositions
    }

    private fun generateRandomGenes(): Set<Int> {
        return (0..9).shuffled().take(2).toSet()
    }

    companion object {
        private const val MIMICRY_GEN = 7
    }
}
