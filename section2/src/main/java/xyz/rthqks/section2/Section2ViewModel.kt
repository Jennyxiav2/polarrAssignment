package xyz.rthqks.section2

import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicBoolean

class Section2ViewModel : ViewModel() {

    private val shouldRunGl = AtomicBoolean(false)
    private lateinit var surfaceView: GLSurfaceView
    val originalLiveData = MutableLiveData<Bitmap>()
    val singleThreadLiveData = MutableLiveData<String>()
    val multiThreadLiveData = MutableLiveData<String>()
    val glLiveData = MutableLiveData<String>()

    val singleThread = SingleThread()
    val multiThread = MultiThread()
    val glHistogram = GLHistogram()

    fun setBitmap(bitmap: Bitmap) {
        originalLiveData.value = bitmap

        var start = SystemClock.elapsedRealtimeNanos()
        val sh = singleThread.histogram(bitmap)
        singleThreadLiveData.value =
            "Elapsed time for single threaded implementation:\n${(SystemClock.elapsedRealtimeNanos() - start) / 1e6}ms"

        start = SystemClock.elapsedRealtimeNanos()
        val mh = multiThread.histogram(bitmap)
        multiThreadLiveData.value =
            "Elapsed time for multi threaded implementation:\n${(SystemClock.elapsedRealtimeNanos() - start) / 1e6}ms"

//        Log.d(TAG, "r single: ${sh[0].joinToString()}")
//        Log.d(TAG, "g single: ${sh[1].joinToString()}")
//        Log.d(TAG, "b single: ${sh[2].joinToString()}")

        shouldRunGl.set(true)
        surfaceView.requestRender()
    }

    fun initGl() {
        Log.d(TAG, "init gl")
        glHistogram.init()
    }

    fun setSurfaceView(surfaceView: GLSurfaceView) {
        this.surfaceView = surfaceView
    }

    fun runGlHistogram() {
        if (shouldRunGl.getAndSet(false)) {
            Log.d(TAG, "runGlHistogram")
            originalLiveData.value?.let {
                var start = SystemClock.elapsedRealtimeNanos()
                val gh = glHistogram.histogram(it)
                glLiveData.postValue("Elapsed time for OpenGL implementation:\n${(SystemClock.elapsedRealtimeNanos() - start) / 1e6}ms")
//                Log.d("r GL Hist", gh[0].joinToString())
//                Log.d("g GL Hist", gh[1].joinToString())
//                Log.d("b GL Hist", gh[2].joinToString())
            }
        }
    }

    companion object {
        const val TAG = "Section2"
    }
}
