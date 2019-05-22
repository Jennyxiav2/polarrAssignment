package xyz.rthqks.section2

import android.graphics.Bitmap
import android.opengl.GLES31
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class GLHistogram {

    private val vertexShaderSource = """
        #version 300 es
        precision highp float;
        precision highp int;
        in vec4 position;
        uniform int channel;

        void main() {
            float value = 0.0;

            if (channel == 0) {
                value = position.r;
            } else if (channel == 1) {
                value = position.g;
            } else if (channel == 2) {
                value = position.b;
            }

            gl_Position = vec4(value * 2.0 - 1.0 + 0.5/256.0, 0.0, 0.0, 1.0);
        }
    """.trimIndent()

    private val fragmentShaderSource = """
        #version 300 es
        precision highp float;
        precision highp int;
        out vec4 color;

        void main() {
            color = vec4(1.0);
        }

    """.trimIndent()

    private var program: Int = 0
    private var framebuffer: Int = 0
    private var imageBuffer: Int = 0
    private lateinit var vertexBufferData: ByteBuffer
    private val histogramData = Array<FloatBuffer>(3) {
        FloatBuffer.wrap(FloatArray(256))
    }


    // called from GLThread
    fun init() {

        val vertexShader = GLES31.glCreateShader(GLES31.GL_VERTEX_SHADER).also { shader ->
            GLES31.glShaderSource(shader, vertexShaderSource)
            GLES31.glCompileShader(shader)
            Log.d("GLSL: shader error", GLES31.glGetShaderInfoLog(shader))
        }

        val fragmentShader = GLES31.glCreateShader(GLES31.GL_FRAGMENT_SHADER).also { shader ->
            GLES31.glShaderSource(shader, fragmentShaderSource)
            GLES31.glCompileShader(shader)
            Log.d("GLSL: shader error", GLES31.glGetShaderInfoLog(shader))
        }

        program = GLES31.glCreateProgram().also {
            GLES31.glAttachShader(it, vertexShader)
            GLES31.glAttachShader(it, fragmentShader)
            GLES31.glLinkProgram(it)
        }

        // max image size * 4 bytes per pixel, RGBA format
        vertexBufferData = ByteBuffer.allocateDirect(4032 * 3024 * 4)
            .order(ByteOrder.nativeOrder())

        val buffers = IntArray(1)
        GLES31.glGenBuffers(1, buffers, 0)
        imageBuffer = buffers[0]

        val fbTexture = createTexture(256, 1)
        framebuffer = createFrameBuffer(fbTexture)

        GLES31.glEnable(GLES31.GL_BLEND)
        GLES31.glEnable(GLES31.GL_FUNC_ADD)
        GLES31.glBlendFunc(GLES31.GL_ONE, GLES31.GL_ONE)
    }

    // called from GLThread
    fun histogram(bitmap: Bitmap): Array<FloatArray> {
        val w = bitmap.width
        val h = bitmap.height

        val elements = w * h

        // copy image to buffer
        vertexBufferData.position(0)
        bitmap.copyPixelsToBuffer(vertexBufferData)

        // copy buffer to GPU
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, imageBuffer)
        val size = vertexBufferData.position()
        vertexBufferData.position(0)
        GLES31.glBufferData(GLES31.GL_ARRAY_BUFFER, size, vertexBufferData, GLES31.GL_STATIC_DRAW)

        GLES31.glUseProgram(program)
        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, framebuffer)
        GLES31.glViewport(0, 0, 256, 1)

        for (i in 0 until 3) {
            GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT)

            // set the channel for histogram
            GLES31.glGetUniformLocation(program, "channel").also {
                GLES31.glUniform1i(it, i)
            }

            GLES31.glGetAttribLocation(program, "position").also {
                GLES31.glEnableVertexAttribArray(it)
                GLES31.glVertexAttribPointer(it, 4, GLES31.GL_UNSIGNED_BYTE, true, 0, 0)
                GLES31.glDrawArrays(
                    GLES31.GL_POINTS,
                    0,
                    elements
                )

                GLES31.glDisableVertexAttribArray(it)
            }


            GLES31.glReadPixels(0, 0, 256, 1, GLES31.GL_RED, GLES31.GL_FLOAT, histogramData[i])
        }

        GLES31.glUseProgram(0)

        return histogramData.map { it.array() }.toTypedArray()
    }

    private fun createFrameBuffer(texture: Int): Int {
        val frameBufferHandle = IntArray(1)
        GLES31.glGenFramebuffers(1, frameBufferHandle, 0)
        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, frameBufferHandle[0])
        GLES31.glFramebufferTexture2D(
            GLES31.GL_FRAMEBUFFER,
            GLES31.GL_COLOR_ATTACHMENT0,
            GLES31.GL_TEXTURE_2D,
            texture,
            0
        )
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT)
        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, 0)
        return frameBufferHandle[0]
    }

    private fun createTexture(width: Int, height: Int): Int {
        val textureHandle = IntArray(1)

        GLES31.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {

            GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, textureHandle[0])

            GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MIN_FILTER, GLES31.GL_NEAREST)
            GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MAG_FILTER, GLES31.GL_NEAREST)

            GLES31.glTexParameterf(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_S, GLES31.GL_CLAMP_TO_EDGE.toFloat())
            GLES31.glTexParameterf(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_WRAP_T, GLES31.GL_CLAMP_TO_EDGE.toFloat())

            GLES31.glTexImage2D(
                GLES31.GL_TEXTURE_2D,
                0,
                GLES31.GL_R32F,
                width,
                height,
                0,
                GLES31.GL_RED,
                GLES31.GL_FLOAT,
                null
            )
        }

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error loading texture.")
        }

        return textureHandle[0]
    }
}