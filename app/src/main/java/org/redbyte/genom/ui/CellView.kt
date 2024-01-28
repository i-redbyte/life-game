package org.redbyte.genom.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import org.redbyte.genom.Cell
import org.redbyte.genom.CellType

@Composable
fun CellView(cell: Cell, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = when (cell.type) {
                    CellType.SUICIDAL -> Color.Yellow
                    CellType.AGGRESSIVE -> Color.Red
                    CellType.PEACEFUL -> Color.Green
                    CellType.SCAVENGER -> Color.Blue
                    CellType.DEAD -> Color.Black
                }
            )
    )
}