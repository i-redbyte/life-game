package org.redbyte.genom.render.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix
import org.redbyte.genom.common.GameBoard
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GameRenderer(
    private val gameBoard: GameBoard,
    private val onCellCountUpdate: (Int, Int) -> Unit
) : Renderer {
    private var squareShaderProgram: Int = 0
    private val projectionMatrix = FloatArray(16)
    private var lastUpdateTime = System.nanoTime()
    private val updateInterval = 128_000_000
    private var turn = 0
    // Vertex shader code
    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    // Fragment shader code
    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0.2f, 1.0f)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        squareShaderProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        val currentTime = System.nanoTime()
        val deltaTime = currentTime - lastUpdateTime
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUseProgram(squareShaderProgram)

        val positionHandle = GLES20.glGetAttribLocation(squareShaderProgram, "vPosition")
        val colorHandle = GLES20.glGetUniformLocation(squareShaderProgram, "vColor")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(squareShaderProgram, "uMVPMatrix")

        val vertexStride = COORDS_PER_VERTEX * 4
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, projectionMatrix, 0)

        gameBoard.matrix.forEachIndexed { y, row ->
            val aliveColor = floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f)
            row.forEachIndexed { x, cell ->
                if (cell.isAlive) {
                    val squareCoords =
                        calculateSquareCoords(x, y, gameBoard.settings.width, gameBoard.settings.height)
                    val vertexBuffer = ByteBuffer.allocateDirect(squareCoords.size * 4).run {
                        order(ByteOrder.nativeOrder())
                        asFloatBuffer().apply {
                            put(squareCoords)
                            position(0)
                        }
                    }

                    GLES20.glEnableVertexAttribArray(positionHandle)
                    GLES20.glVertexAttribPointer(
                        positionHandle,
                        COORDS_PER_VERTEX,
                        GLES20.GL_FLOAT,
                        false,
                        vertexStride,
                        vertexBuffer
                    )


                    GLES20.glUniform4fv(colorHandle, 1, aliveColor, 0)

                    GLES20.glDrawArrays(
                        GLES20.GL_TRIANGLE_STRIP,
                        0,
                        squareCoords.size / COORDS_PER_VERTEX
                    )

                    GLES20.glDisableVertexAttribArray(positionHandle)
                }
            }
        }
        if (deltaTime >= updateInterval) {
            gameBoard.update()
            onCellCountUpdate(gameBoard.countLivingCells(), ++turn)
            lastUpdateTime = currentTime
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspectRatio = if (width >= height) {
            // landscape
            width.toFloat() / height
        } else {
            // portrait
            height.toFloat() / width
        }
        if (width >= height) {
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    private fun calculateSquareCoords(x: Int, y: Int, width: Int, height: Int): FloatArray {
        val normalizedCellWidth = 2.0f / width
        val normalizedCellHeight = 2.0f / height
        val normalizedX = -1f + x * normalizedCellWidth
        val normalizedY = 1f - y * normalizedCellHeight

        return floatArrayOf(
            normalizedX, normalizedY,
            normalizedX, normalizedY - normalizedCellHeight,
            normalizedX + normalizedCellWidth, normalizedY,
            normalizedX + normalizedCellWidth, normalizedY - normalizedCellHeight
        )
    }

    companion object {
        const val COORDS_PER_VERTEX = 2
    }
}
