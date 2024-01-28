package org.redbyte.genom

data class Cell(
    val genes: Set<Int>,
    var type: CellType,
    var isAlive: Boolean = true,
    var shouldDivide: Boolean = false,
    var hasSuccessfullyAttacked: Boolean = false,
    var stepCount: Int = 0,
    var eatenDeadCells: Int = 0,
    var stepsSinceLastReproduction: Int = 0,
    var turnsAsCorpse: Int = 0,
    var x: Int = 0,
    var y: Int = 0,
)
