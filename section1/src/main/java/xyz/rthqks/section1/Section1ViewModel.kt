package xyz.rthqks.section1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.rthqks.section1.tree.TreeUtils

class Section1ViewModel: ViewModel() {
    val rawLiveData = MutableLiveData<String>()
    val encodedLiveData = MutableLiveData<String>()
    val decodedLiveData = MutableLiveData<String>()

    fun generateTree(depth: Int, numChildren: Int) {
        val tree = TreeUtils.generateTree(depth, numChildren)

        rawLiveData.value = TreeUtils.serializeJson(tree)

        encodedLiveData.value = TreeUtils.serializeEncode(tree) {
            it xor 0xCAFEBAB
        }

        val deserialized = TreeUtils.deserializeDecode(encodedLiveData.value!!) {
            it xor 0xCAFEBAB
        }

        decodedLiveData.value = TreeUtils.serializeJson(deserialized)
    }

    companion object {
        const val TAG = "Section1"
    }
}
