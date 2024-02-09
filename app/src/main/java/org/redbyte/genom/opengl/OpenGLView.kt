package org.redbyte.genom.opengl

import android.opengl.GLSurfaceView
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun OpenGLView(gameBoard: GameBoard) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                setRenderer(GameRenderer(gameBoard)) // Настраиваем наш кастомный рендерер
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        },
        update = { view ->
            Log.d("_debug", "UPD: ");
        }
    )
}