package org.redbyte.life.ui.render.opengl

import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.redbyte.life.R
import org.redbyte.life.common.GameBoard
import org.redbyte.life.monitoring.FPSMonitor
import org.redbyte.life.ui.settings.SharedGameSettingsViewModel
import org.redbyte.life.ui.theme.baseTeal

@Composable
fun LifeGame2D(viewModel: SharedGameSettingsViewModel) {
    val gameBoard = viewModel.getGameBoard()
    val livingCellsCount = remember { mutableIntStateOf(gameBoard.settings.initialPopulation) }
    val turnGame = remember { mutableIntStateOf(0) }
    val fps = remember { mutableDoubleStateOf(0.0) }

    Box(modifier = Modifier.fillMaxSize()) {
        GameBoardView(
            gameBoard = gameBoard,
            onGameUpdated = { count, turn ->
                livingCellsCount.intValue = count
                turnGame.intValue = turn
            },
            onFPSUpdate = { fps.doubleValue = it }
        )
        GameInfoView(
            livingCellsCount = livingCellsCount.intValue,
            turnGame = turnGame.intValue,
            fps = fps.doubleValue
        )
    }
}

@Composable
fun GameBoardView(
    gameBoard: GameBoard,
    onGameUpdated: (Int, Int) -> Unit,
    onFPSUpdate: (Double) -> Unit
) {
    val context = LocalContext.current
    val fpsMonitor = remember { FPSMonitor(onFPSUpdate) }

    DisposableEffect(Unit) {
        fpsMonitor.start()
        onDispose {
            fpsMonitor.stop()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                setRenderer(
                    GameRenderer(
                        context,
                        gameBoard,
                        onGameUpdated
                    )
                )
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        }
    )
}

@Composable
fun GameInfoView(livingCellsCount: Int, turnGame: Int, fps: Double) {
    val livingCellsText = stringResource(id = R.string.living_cells_count, livingCellsCount)
    val gameTurnText = stringResource(id = R.string.game_turn, turnGame)

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "$livingCellsText\n$gameTurnText\nFPS: ${fps.format(2)}",
            color = baseTeal,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 32.dp, start = 16.dp)
        )
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)
