package xyz.rthqks.benchmark

import android.graphics.BitmapFactory
import androidx.benchmark.BenchmarkRule
import androidx.benchmark.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.rthqks.section4.Matrix2
import xyz.rthqks.section4.MySDK

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class SDKBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun multiplyWithNewInstanceCreation() {
        val sdk = MySDK.instance
        val m1 = Matrix2(1f, 2f, 3f, 4f)
        val m2 = Matrix2(4f, 3f, 2f, 1f)
        benchmarkRule.measureRepeated {
            sdk.multiply2x2(m1, m2)
        }
    }

    @Test
    fun multiplyWithResultMatrix() {
        val sdk = MySDK.instance
        val m1 = Matrix2(1f, 2f, 3f, 4f)
        val m2 = Matrix2(4f, 3f, 2f, 1f)
        val r = Matrix2()
        benchmarkRule.measureRepeated {
            sdk.multiply2x2(m1, m2, r)
        }
    }

    @Test
    fun grayscale() {
        val sdk = MySDK.instance
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.test)
        benchmarkRule.measureRepeated {
            sdk.grayscale(bitmap)
        }
    }
}