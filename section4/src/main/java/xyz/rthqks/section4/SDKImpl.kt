package xyz.rthqks.section4

import android.graphics.*
import android.opengl.GLES31
import android.util.Log
import java.nio.ByteBuffer


class SDKImpl : SDK {
    private var framebuffer: Int = 0

    override fun grayscale(bitmap: Bitmap): Bitmap {
        val width: Int = bitmap.width
        val height: Int = bitmap.height
        val grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(grayscale)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorFilter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return grayscale
    }

    override fun multiply2x2(a: Matrix2, b: Matrix2): Matrix2 {
        val c = Matrix2()
        multiply2x2(a, b, c)
        return c
    }

    override fun multiply2x2(a: Matrix2, b: Matrix2, result: Matrix2) {
        result.data[0] = a.data[0] * b.data[0] + a.data[1] * b.data[2]
        result.data[1] = a.data[0] * b.data[1] + a.data[1] * b.data[3]

        result.data[2] = a.data[2] * b.data[0] + a.data[3] * b.data[2]
        result.data[3] = a.data[2] * b.data[1] + a.data[3] * b.data[3]
    }

    override fun textureToBitmap(textureId: Int, textureType: Int, width: Int, height: Int): Bitmap? {
        if (textureType != GLES31.GL_UNSIGNED_BYTE) {
            Log.w(TAG, "unexpected textureType, only 'GL_UNSIGNED_BYTE' is supported, got $textureType")
            return null
        }

        if (textureId == 0) {
            Log.w(TAG, "invalid textureId")
            return null
        }

        if (framebuffer == 0) {
            framebuffer = createFrameBuffer()
        }

        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, framebuffer)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, textureId)
        GLES31.glViewport(0, 0, width, height)

        GLES31.glFramebufferTexture2D(
            GLES31.GL_FRAMEBUFFER,
            GLES31.GL_COLOR_ATTACHMENT0,
            GLES31.GL_TEXTURE_2D,
            textureId,
            0
        )

        val buffer = ByteBuffer.allocateDirect(width * height * 4)
        GLES31.glReadPixels(0, 0, width, height, GLES31.GL_RGBA, textureType, buffer)
        val bitmap = BitmapFactory.decodeByteArray(buffer.array(), buffer.arrayOffset(), buffer.capacity())

        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, 0)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, 0)

        return bitmap
    }

    private fun createFrameBuffer(): Int {
        val frameBufferHandle = IntArray(1)
        GLES31.glGenFramebuffers(1, frameBufferHandle, 0)

        if (frameBufferHandle[0] == 0) {
            Log.w(TAG, "error creating framebuffer, must be in a valid egl context")
        }

        return frameBufferHandle[0]
    }

    companion object {
        const val TAG = "SDK"
    }
}