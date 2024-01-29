package org.redbyte.genom.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.redbyte.genom.Cell
import org.redbyte.genom.CellType
import kotlin.random.Random

@Composable
fun CellView(cell: Cell, size: Dp, boardWidth: Dp, boardHeight: Dp) {
    val xOffset = Random.nextInt((boardWidth - size).value.toInt()).dp
    val yOffset = Random.nextInt((boardHeight - size).value.toInt()).dp

    AnimatedVisibility(
        visible = cell.isAlive,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(xOffset, yOffset)
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
}