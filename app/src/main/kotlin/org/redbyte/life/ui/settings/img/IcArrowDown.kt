package org.redbyte.life.ui.settings.img

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.unit.dp

val IcArrowDown: ImageVector
    get() = ImageVector.Builder(
        name = "ic_arrow_down",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        addPath(
            pathData = PathBuilder().apply {
                moveTo(7f, 10f)
                lineTo(12f, 15f)
                lineTo(17f, 10f)
                lineTo(16.6f, 9.6f)
                lineTo(12f, 14.2f)
                lineTo(7.4f, 9.6f)
                close()
            }.getNodes(),
            fill = SolidColor(Color.White),
            fillAlpha = 1.0f,
            stroke = null,
            strokeAlpha = 0.0f,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            pathFillType = PathFillType.NonZero
        )
    }.build()
