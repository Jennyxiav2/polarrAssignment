package xyz.rthqks.section2

import android.graphics.Bitmap
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors


class MultiThread {

    private val NUM_THREADS = 8
    private val executorService = Executors.newFixedThreadPool(NUM_THREADS)

    val rHistogram = Array(NUM_THREADS){IntArray(256) }
    val gHistogram = Array(NUM_THREADS){IntArray(256) }
    val bHistogram = Array(NUM_THREADS){IntArray(256) }

    fun histogram(bitmap: Bitmap): Array<IntArray> {
        val w = bitmap.width
        val h = bitmap.height

        val hChunk = h / NUM_THREADS

        rHistogram.forEach { Arrays.fill(it, 0) }
        gHistogram.forEach { Arrays.fill(it, 0) }
        bHistogram.forEach { Arrays.fill(it, 0) }

        val latch = CountDownLatch(NUM_THREADS)
        for (t in 0 until NUM_THREADS) {
            executorService.submit {
                try {
                    val r = rHistogram[t]
                    val g = gHistogram[t]
                    val b = bHistogram[t]

                    val hStart = t * hChunk
                    // last slice gets a little extra work if the height is not divisible by NUM_THREADS
                    val hEnd = hStart + hChunk + if (t == NUM_THREADS - 1) h % NUM_THREADS else 0

                    for (i in hStart until hEnd) {
                        for (j in 0 until w) {
                            val pixel = bitmap.getPixel(j, i)
                            val alpha = pixel shr 24 and 0xff
                            val red = pixel shr 16 and 0xff
                            val green = pixel shr 8 and 0xff
                            val blue = pixel and 0xff

                            r[red] += 1
                            g[green] += 1
                            b[blue] += 1
                        }
                    }

                    latch.countDown()
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }

        latch.await()

        val rFinal = rHistogram.slice(1 until rHistogram.size).fold(rHistogram[0]) { acc, current ->
            current.forEachIndexed { index, i ->
                acc[index] += i
            }
            acc
        }

        val gFinal = gHistogram.slice(1 until gHistogram.size).fold(gHistogram[0]) { acc, current ->
            current.forEachIndexed { index, i ->
                acc[index] += i
            }
            acc
        }
        val bFinal = bHistogram.slice(1 until bHistogram.size).fold(bHistogram[0]) { acc, current ->
            current.forEachIndexed { index, i ->
                acc[index] += i
            }
            acc
        }
        return arrayOf(rFinal, gFinal, bFinal)
    }
}
