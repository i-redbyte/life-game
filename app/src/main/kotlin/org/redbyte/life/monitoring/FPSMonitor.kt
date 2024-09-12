package org.redbyte.life.monitoring

import android.view.Choreographer

class FPSMonitor(private val listener: (Double) -> Unit) : Choreographer.FrameCallback {

    private var lastFrameTimeNanos: Long = 0L
    private var frameCount = 0
    private val oneSecondInNanos = 1_000_000_000.0
    private val choreographer = Choreographer.getInstance()

    override fun doFrame(frameTimeNanos: Long) {
        if (lastFrameTimeNanos != 0L) {
            frameCount++
            val timeElapsedInSeconds = (frameTimeNanos - lastFrameTimeNanos) / oneSecondInNanos
            if (timeElapsedInSeconds >= 1.0) {
                val fps = frameCount / timeElapsedInSeconds
                listener(fps)
                frameCount = 0
                lastFrameTimeNanos = frameTimeNanos
            }
        } else {
            lastFrameTimeNanos = frameTimeNanos
        }

        choreographer.postFrameCallback(this)
    }

    fun start() {
        if (lastFrameTimeNanos != 0L) return
        choreographer.postFrameCallback(this)
    }

    fun stop() {
        choreographer.removeFrameCallback(this)
        lastFrameTimeNanos = 0L
        frameCount = 0
    }
}
