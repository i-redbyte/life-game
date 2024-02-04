package org.redbyte.genom.opengl

import android.opengl.GLSurfaceView
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun OpenGLView() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                //preserveEGLContextOnPause = true
                setRenderer(GameRenderer(context))
            }
        },
        update = { view ->
            Log.d("_debug", "UPD: ");
        }
    )
}