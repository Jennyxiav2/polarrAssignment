package xyz.rthqks.section3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class Section3Activity : AppCompatActivity() {

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section3)

        val button = findViewById<Button>(R.id.button)
        val text = findViewById<TextView>(R.id.text)

        val intersection = Intersection()

        intersection.listener = {
            text.text = "${text.text}\n$it"
        }

        button.setOnClickListener {
            intersection.setCarOnEW()
        }

        disposable = Single.just(0)
            .repeatWhen {
                it.delay((Math.random() * 10).toLong(), TimeUnit.SECONDS)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                intersection.setCarOnEW()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
