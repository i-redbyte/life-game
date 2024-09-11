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
    private var aspectRatio: Float = 1f

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
        aspectRatio = if (width >= height) {
            width.toFloat() / height
        } else {
            height.toFloat() / width
        }
        setProjectionMatrix(width, height)
    }

    private fun initializeOpenGL() {
        GLES20.glClearColor(0f, 0f, 0.2f, 1.0f)
    }

    private fun createShaderProgram(vertexShaderResId: Int, fragmentShaderResId: Int): Int {
        val vertexShaderCode = readShaderCode(vertexShaderResId)
        val fragmentShaderCode = readShaderCode(fragmentShaderResId)

        return listOf(
            loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode),
            loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        ).fold(GLES20.glCreateProgram()) { program, shader ->
            GLES20.glAttachShader(program, shader)
            program
        }.also {
            GLES20.glLinkProgram(it)
        }
    }

    private fun readShaderCode(resId: Int): String =
        context.resources.openRawResource(resId).bufferedReader().use { it.readText() }

    private fun loadShader(type: Int, shaderCode: String): Int =
        GLES20.glCreateShader(type).apply {
            GLES20.glShaderSource(this, shaderCode)
            GLES20.glCompileShader(this)
        }

    private fun setProjectionMatrix(width: Int, height: Int) {
        val aspectRatio = (width.toFloat() / height).let { if (width >= height) it else 1 / it }
        Matrix.orthoM(
            projectionMatrix, 0,
            if (width >= height) -aspectRatio else -1f,
            if (width >= height) aspectRatio else 1f,
            -1f, 1f, -1f, 1f
        )
    }

    private fun renderGameBoard() {
        val handles = getHandles()

        GLES20.glUniformMatrix4fv(handles.mvpMatrixHandle, 1, false, projectionMatrix, 0)

        gameBoard.matrix.forEachIndexed { y, row ->
            drawAliveCellsInRow(row, y, handles)
        }
    }

    private fun getHandles(): Handles = Handles(
        positionHandle = GLES20.glGetAttribLocation(shaderProgramId, "vPosition"),
        colorHandle = GLES20.glGetUniformLocation(shaderProgramId, "vColor"),
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramId, "uMVPMatrix")
    )

    private fun drawAliveCellsInRow(row: Long, y: Int, handles: Handles) {
        val aliveCellColor = floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f)
        val vertexStride = COORDS_PER_VERTEX * 4

        (0 until gameBoard.settings.width)
            .filter { (row shr it) and 1L == 1L }
            .map { x -> calculateSquareCoords(x, y) }
            .forEach { squareCoords ->
                drawCell(squareCoords, handles, aliveCellColor, vertexStride)
            }
    }

    private fun drawCell(squareCoords: FloatArray, handles: Handles, color: FloatArray, vertexStride: Int) {
        val vertexBuffer = createVertexBuffer(squareCoords)
        GLES20.glEnableVertexAttribArray(handles.positionHandle)
        GLES20.glVertexAttribPointer(
            handles.positionHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        GLES20.glUniform4fv(handles.colorHandle, 1, color, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, squareCoords.size / COORDS_PER_VERTEX)
        GLES20.glDisableVertexAttribArray(handles.positionHandle)
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

    data class Handles(
        val positionHandle: Int,
        val colorHandle: Int,
        val mvpMatrixHandle: Int
    )
}
