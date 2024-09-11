package org.redbyte.life.ui.render.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix
import org.redbyte.life.R
import org.redbyte.life.common.GameBoard
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(
    private val context: Context,
    private val gameBoard: GameBoard,
    private val onCellCountUpdate: (Int, Int) -> Unit
) : Renderer {

    private var shaderProgramId: Int = 0
    private val projectionMatrix = FloatArray(16)
    private var lastUpdateTime = System.nanoTime()
    private val updateInterval = 128_000_000L
    private var turnCounter = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        initializeOpenGL()
        shaderProgramId = createShaderProgram(R.raw.vertex_shader, R.raw.fragment_shader)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(shaderProgramId)

        val deltaTime = System.nanoTime() - lastUpdateTime
        renderGameBoard()

        if (deltaTime >= updateInterval) {
            updateGameState()
            lastUpdateTime = System.nanoTime()
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        setProjectionMatrix(width, height)
    }

    private fun initializeOpenGL() {
        GLES20.glClearColor(0f, 0f, 0.2f, 1.0f)
    }

    private fun createShaderProgram(vertexShaderResId: Int, fragmentShaderResId: Int): Int {
        val vertexShaderCode = readShaderCode(vertexShaderResId)
        val fragmentShaderCode = readShaderCode(fragmentShaderResId)

        val vertexShaderId = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShaderId = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        return GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShaderId)
            GLES20.glAttachShader(it, fragmentShaderId)
            GLES20.glLinkProgram(it)
        }
    }

    private fun readShaderCode(resId: Int): String =
        context.resources.openRawResource(resId).bufferedReader().use { it.readText() }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    private fun setProjectionMatrix(width: Int, height: Int) {
        val aspectRatio = if (width >= height) {
            width.toFloat() / height
        } else {
            height.toFloat() / width
        }
        if (width >= height) {
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }
    }

    private fun renderGameBoard() {
        val positionHandle = GLES20.glGetAttribLocation(shaderProgramId, "vPosition")
        val colorHandle = GLES20.glGetUniformLocation(shaderProgramId, "vColor")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramId, "uMVPMatrix")

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, projectionMatrix, 0)

        gameBoard.matrix.forEachIndexed { y, row ->
            val aliveCellColor = floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f)
            drawAliveCellsInRow(row, y, positionHandle, colorHandle, aliveCellColor)
        }
    }

    private fun drawAliveCellsInRow(row: Long, y: Int, positionHandle: Int, colorHandle: Int, color: FloatArray) {
        val vertexStride = COORDS_PER_VERTEX * 4
        for (x in 0 until gameBoard.settings.width) {
            if ((row shr x) and 1L == 1L) {
                val squareCoords = calculateSquareCoords(x, y)
                val vertexBuffer = createVertexBuffer(squareCoords)

                GLES20.glEnableVertexAttribArray(positionHandle)
                GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, squareCoords.size / COORDS_PER_VERTEX)
                GLES20.glDisableVertexAttribArray(positionHandle)
            }
        }
    }

    private fun updateGameState() {
        gameBoard.update()
        onCellCountUpdate(gameBoard.countLivingCells(), ++turnCounter)
    }

    private fun createVertexBuffer(squareCoords: FloatArray) = ByteBuffer.allocateDirect(squareCoords.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(squareCoords)
            position(0)
        }
    }

    private fun calculateSquareCoords(x: Int, y: Int): FloatArray {
        val normalizedCellWidth = 2.0f / gameBoard.settings.width
        val normalizedCellHeight = 2.0f / gameBoard.settings.height
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
