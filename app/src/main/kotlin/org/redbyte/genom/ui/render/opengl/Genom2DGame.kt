package org.redbyte.genom.ui.render.opengl

import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.redbyte.genom.R
import org.redbyte.genom.settings.SharedGameSettingsViewModel
import org.redbyte.genom.ui.theme.baseTeal

@Composable
fun Genom2DGame(viewModel: SharedGameSettingsViewModel) {
    val gameBoard = viewModel.getGameBoard()
    val livingCellsCount = remember { mutableIntStateOf(gameBoard.settings.initialPopulation) }
    val turnGame = remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory = { context ->
                GLSurfaceView(context).apply {
                    setEGLContextClientVersion(2)
                    setRenderer(GameRenderer(gameBoard) { count, turn ->
                        livingCellsCount.intValue = count
                        turnGame.intValue = turn
                    })
                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                }
            }
        )
        val livingCellsText = stringResource(id = R.string.living_cells_count, livingCellsCount.intValue)
        val gameTurnText = stringResource(id = R.string.game_turn, turnGame.intValue)

        Text(
            text = "$livingCellsText\n$gameTurnText",
            color = baseTeal,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 32.dp, start = 16.dp)
        )

    }
}
