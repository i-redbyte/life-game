package org.redbyte.genom.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.DisplayMetrics
import android.view.WindowManager
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
class GameRenderer(context: Context) : GLSurfaceView.Renderer {
    private val gameBoard: GameBoard
    private val cellVertexBuffer: FloatBuffer
    private var program: Int = 0
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private val cellSize = 8
    private var matrixHandle: Int = 0

    init {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        gameBoard = GameBoard(screenWidth, screenHeight)

        val cellVertices = floatArrayOf(
            -cellSize / 2.0f, -cellSize / 2.0f,
            cellSize / 2.0f, -cellSize / 2.0f,
            -cellSize / 2.0f, cellSize / 2.0f,
            cellSize / 2.0f, cellSize / 2.0f
        )

        val buffer = ByteBuffer.allocateDirect(cellVertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(cellVertices)
        buffer.position(0)
        cellVertexBuffer = buffer
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        val vertexShaderCode = """
            attribute vec4 a_position;
            uniform mat4 u_matrix;
            void main() {
                gl_Position = u_matrix * a_position;
            }
        """.trimIndent()

        val fragmentShaderCode = """
            precision mediump float;
            uniform vec4 u_color;
            void main() {
                gl_FragColor = u_color;
            }
        """.trimIndent()

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        GLES20.glUseProgram(program)

        matrixHandle = GLES20.glGetUniformLocation(program, "u_matrix")
        positionHandle = GLES20.glGetAttribLocation(program, "a_position")
        colorHandle = GLES20.glGetUniformLocation(program, "u_color")

        val ratio: Float = gameBoard.width.toFloat() / gameBoard.height.toFloat()
        val projectionMatrix = FloatArray(16)
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(gl: GL10?) {
        for (i in 0 until gameBoard.height) {
            for (j in 0 until gameBoard.width) {
                if (gameBoard.cells[i][j]) {
                    val x = j * cellSize
                    val y = i * cellSize

                    val matrix = FloatArray(16)
                    Matrix.setIdentityM(matrix, 0)
                    Matrix.translateM(matrix, 0, x.toFloat(), y.toFloat(), 0.0f)
                    GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0)
                    GLES20.glVertexAttribPointer(
                        positionHandle, 2, GLES20.GL_FLOAT, false,
                        2 * 4, cellVertexBuffer
                    )
                    GLES20.glEnableVertexAttribArray(positionHandle)
                    GLES20.glUniform4f(colorHandle, 0.0f, 1.0f, 0.0f, 1.0f)
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                    GLES20.glDisableVertexAttribArray(positionHandle)
                }
            }
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}
