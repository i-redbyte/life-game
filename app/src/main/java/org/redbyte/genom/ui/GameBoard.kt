package org.redbyte.genom.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.redbyte.genom.Cell

@Composable
fun GameBoard(cells: List<List<Cell>>) {
    BoxWithConstraints {
        val cellSize = maxWidth / cells[0].size

        LazyVerticalGrid(
            columns = GridCells.Fixed(cells[0].size),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(cells.flatten()) { cell ->
                CellView(cell, cellSize)
            }
        }
    }
}
