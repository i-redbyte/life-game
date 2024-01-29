package org.redbyte.genom.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import org.redbyte.genom.Cell

@Composable
fun GameBoard(cells: List<List<Cell>>, boardWidth: Dp, boardHeight: Dp) {
    val cellSize = boardWidth.coerceAtMost(boardHeight) / 20

    cells.flatten().forEach { cell ->
        CellView(cell = cell, size = cellSize, boardWidth = boardWidth, boardHeight = boardHeight)
    }
}
