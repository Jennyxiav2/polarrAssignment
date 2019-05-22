package xyz.rthqks.section4

import java.lang.IllegalArgumentException

class Matrix2() {
    // row-major
    val data = floatArrayOf(0f, 0f, 0f, 0f)

    constructor(data: FloatArray): this() {
        if (data.size != 4) {
            throw IllegalArgumentException("expected data.size=4, got data.size=${data.size}")
        }
        data.copyInto(this.data)
    }

    constructor(matrix2: Matrix2): this(matrix2.data)

    constructor(x1y1: Float, x1y2: Float, x2y1: Float, x2y2: Float): this() {
        data[0] = x1y1
        data[1] = x1y2
        data[2] = x2y1
        data[3] = x2y2
    }
}