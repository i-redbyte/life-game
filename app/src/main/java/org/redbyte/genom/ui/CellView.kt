package org.redbyte.genom.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.redbyte.genom.Cell
import org.redbyte.genom.CellType

@Composable
fun CellView(cell: Cell) {
    val color = when (cell.type) {
        CellType.SUICIDAL -> Color.Red
        CellType.AGGRESSIVE -> Color.Black
        CellType.PEACEFUL -> Color.Green
        CellType.SCAVENGER -> Color.Blue
        CellType.DEAD -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(10.dp)
            .background(color)
    )
}
