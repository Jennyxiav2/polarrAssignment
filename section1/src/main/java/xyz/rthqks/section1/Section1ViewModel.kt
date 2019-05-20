package xyz.rthqks.section1

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import xyz.rthqks.section1.tree.TreeUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class Section1ViewModel: ViewModel() {
    val rawLiveData = MutableLiveData<String>()
    val encodedLiveData = MutableLiveData<String>()
    val decodedLiveData = MutableLiveData<String>()

    @SuppressLint("CheckResult")
    fun generateTree(depth: Int, numChildren: Int) {
        val tree = TreeUtils.generateTree(depth, numChildren)

        val outputStream = ByteArrayOutputStream(1024)
        TreeUtils.serializeToStream(tree, outputStream)

        outputStream.flush()

        // original tree to string
        rawLiveData.value = TreeUtils.serialize(tree.root)

        // "encrypted" bytes to string
        encodedLiveData.value = outputStream.toString()

        // delay "decryption" by 1 second
        Completable.complete()
            .delay(1L, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val inputStream = ByteArrayInputStream(outputStream.toByteArray())
                val tree1 = TreeUtils.deserializeFromStream(inputStream)

                decodedLiveData.value = TreeUtils.serialize(tree1.root)
            }
    }

    companion object {
        const val TAG = "Section1"
    }
}
