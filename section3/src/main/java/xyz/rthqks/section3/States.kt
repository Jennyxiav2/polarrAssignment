package xyz.rthqks.section3

class Context {
    var state: State? = null
        set(value) {
            field = value
            field?.let {
                listener?.invoke(it)
            }
        }

    var listener: ((State) -> Unit)? = null
}

interface State {
    fun doAction(context: Context)
}

