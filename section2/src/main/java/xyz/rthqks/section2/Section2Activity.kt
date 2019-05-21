package xyz.rthqks.section2

import android.content.Intent
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_section2.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Section2Activity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var surfaceView: GLSurfaceView
    private lateinit var viewModel: Section2ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section2)
        viewModel = ViewModelProviders.of(this)[Section2ViewModel::class.java]

        imageView = findViewById(R.id.image)
        surfaceView = findViewById(R.id.surface_view)
        surfaceView.setEGLContextClientVersion(3)

        surfaceView.setRenderer(object : GLSurfaceView.Renderer {
            override fun onDrawFrame(gl: GL10?) {
                Log.d("Section2Activity", "onDrawFrame")
                viewModel.runGlHistogram()
            }

            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                Log.d("Section2Activity", "onSurfaceChanged")
            }

            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                Log.d("Section2Activity", "onSurfaceCreated")
                surfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
                viewModel.initGl()
            }

        })

        viewModel.setSurfaceView(surfaceView)

        findViewById<Button>(R.id.select_image).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, OPEN_DOCUMENT_CODE)
        }

        viewModel.originalLiveData.observe(this, Observer {
                imageView.setImageBitmap(it)
        })

        val text1 = findViewById<TextView>(R.id.text1)
        val text2 = findViewById<TextView>(R.id.text2)
        val text3 = findViewById<TextView>(R.id.text3)

        viewModel.singleThreadLiveData.observe(this, Observer {
            text1.text = it
        })

        viewModel.multiThreadLiveData.observe(this, Observer {
            text2.text = it
        })

        viewModel.glLiveData.observe(this, Observer {
            text3.text = it
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.let {
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(it.data))
            viewModel.setBitmap(bitmap)

        }

    }

    companion object {
        private const val OPEN_DOCUMENT_CODE: Int = 0
    }
}
