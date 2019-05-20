package xyz.rthqks.polarrassignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import xyz.rthqks.section1.Section1Activity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.section_1).setOnClickListener {
            startActivity(Intent(this, Section1Activity::class.java))
        }
    }
}
