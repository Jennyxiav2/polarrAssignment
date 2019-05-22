package xyz.rthqks.section4

import android.graphics.Bitmap

interface SDK {
    fun grayscale(bitmap: Bitmap): Bitmap

    fun multiply2x2(a: Matrix2, b: Matrix2): Matrix2

    fun multiply2x2(a: Matrix2, b: Matrix2, result: Matrix2)

    fun textureToBitmap(textureId: Int, textureType: Int, width: Int, height: Int): Bitmap?
}

class MySDK private constructor() : SDK {
    private val sdk = SDKImpl()

    override fun grayscale(bitmap: Bitmap) = sdk.grayscale(bitmap)

    override fun multiply2x2(a: Matrix2, b: Matrix2) = sdk.multiply2x2(a, b)

    override fun multiply2x2(a: Matrix2, b: Matrix2, result: Matrix2) = sdk.multiply2x2(a, b, result)

    override fun textureToBitmap(textureId: Int, textureType: Int, width: Int, height: Int) =
        sdk.textureToBitmap(textureId, textureType, width, height)

    companion object {
        val instance: SDK by lazy {
            MySDK()
        }
    }
}