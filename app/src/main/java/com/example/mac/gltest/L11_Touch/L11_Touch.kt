package com.example.mac.gltest.L11_Touch

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.opengl.Matrix.multiplyMM
import com.example.mac.gltest.R
import com.example.mac.gltest.util.MatrixHelper
import com.example.mac.gltest.util.TextureHelper
import com.example.mac.gltest.util.VertexArray.BYTES_PER_FLOAT
import javax.microedition.khronos.opengles.GL10


/**
 *
 * 增加触控交互，为完善粒子操控做准备
 * Created by yijie.ma on 2018/12/11.
 */


private const val U_MATRIX: String = "u_Matrix"
private const val A_POSITION = "a_Position"
private const val A_COLOR = "a_Color"
private const val POSITION_COMPONENT_COUNT: Int = 2
private const val COLOR_COMPONENT_COUNT: Int = 3
private const val BYTES_PER_FLOAT: Int = 4
private const val STRIDE: Int = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT


class L11_Touch(val mContext: Context) : GLSurfaceView.Renderer {


    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)



    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var puck: Puck

    private lateinit var textureShaderProgram: ShaderProgram.TextureShaderProgram
    private lateinit var colorShaderProgram: ShaderProgram.ColorShaderProgram


    private var texture: Int = 0


    private val simple_fragment_shader =
            "precision mediump float; \n" +
                    "uniform sampler2D u_TextureUnit; \n" +
                    "varying vec2 v_TextureCoordinates;    \n" +
                    "  \n" +
                    "void main()\n" +
                    "{ \n" +
                    "    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates); \n" +
                    "}"

    private val simple_vertex_shader =
            "uniform mat4 u_Matrix; \n" +

                    "attribute vec4 a_Position; \n" +
                    "attribute vec2 a_TextureCoordinates; \n" +

                    "varying vec2 v_TextureCoordinates; \n" +

                    "void main()\n" +
                    "{ \n" +
                    "    v_TextureCoordinates = a_TextureCoordinates; \n" +
                    "    gl_Position = u_Matrix * a_Position;\n" +
                    "}"


    init {
        val tableVerticesWithTriangles = floatArrayOf(
                // Order of coordinates: X, Y, R, G, B

                // Triangle Fan
                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                // Line 1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,

                // Mallets
                0f, -0.4f, 0f, 0f, 1f,
                0f, 0.4f, 1f, 0f, 0f
        )


    }

    override fun onSurfaceCreated(gl: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)



        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)
        puck = Puck(0.06f, 0.02f, 32)

        textureShaderProgram = ShaderProgram.TextureShaderProgram(mContext)
        colorShaderProgram = ShaderProgram.ColorShaderProgram(mContext)

        texture = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface)

    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height)

//
        MatrixHelper.perspectiveM(projectionMatrix, 45f, width / height.toFloat(), 1f, 10f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f)

    }


    override fun onDrawFrame(gl: GL10?) {


        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)


        positionTableInScene()
        textureShaderProgram.useProgram()
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, texture)
        table.bindData(textureShaderProgram)
        table.draw()


        //draw the first mallet
        positionObjectInScene(0f, mallet.height/2f, -0.4f)
        colorShaderProgram.useProgram()
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
        mallet.bindData(colorShaderProgram)
        mallet.draw()

        //draw another mallet
        positionObjectInScene(0f, mallet.height/2f, 0.4f)
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mallet.draw()

        //draw the puck
        positionObjectInScene(0f, puck.height/2f, 0f)
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        puck.bindData(colorShaderProgram)
        puck.draw()

    }

    fun positionTableInScene() {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

    fun positionObjectInScene(x:Float, y:Float, z:Float) {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

}