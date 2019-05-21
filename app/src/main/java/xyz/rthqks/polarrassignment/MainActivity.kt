package xyz.rthqks.polarrassignment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import xyz.rthqks.section1.Section1Activity
import xyz.rthqks.section2.Section2Activity
import xyz.rthqks.section3.Section3Activity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.section_1).setOnClickListener {
            startActivity(Intent(this, Section1Activity::class.java))
        }

        findViewById<View>(R.id.section_2).setOnClickListener {
            startActivity(Intent(this, Section2Activity::class.java))
        }

        findViewById<View>(R.id.section_3).setOnClickListener {
            startActivity(Intent(this, Section3Activity::class.java))
        }

    }
}
