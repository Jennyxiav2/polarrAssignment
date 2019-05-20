package xyz.rthqks.section1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class Section1Activity : AppCompatActivity() {
    private lateinit var viewModel: Section1ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section1)
        viewModel = ViewModelProviders.of(this)[Section1ViewModel::class.java]

        val depthView = findViewById<EditText>(R.id.depth)
        val numChildrenView = findViewById<EditText>(R.id.num_children)

        val rawView = findViewById<TextView>(R.id.tree_raw_text)
        val encodedView = findViewById<TextView>(R.id.tree_encoded_text)
        val decodedView = findViewById<TextView>(R.id.tree_decoded_text)

        viewModel.rawLiveData.observe(this, Observer {
            rawView.text = it
        })

        viewModel.encodedLiveData.observe(this, Observer {
            encodedView.text = it
        })

        viewModel.decodedLiveData.observe(this, Observer {
            decodedView.text = it
        })

        findViewById<Button>(R.id.generate).setOnClickListener {
            viewModel.generateTree(depthView.text.toString().toInt(), numChildrenView.text.toString().toInt())
        }
    }
}
