package xyz.rthqks.section3

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class Intersection {
    val nsContext = Context()
    val ewContext = Context()
    val ewCarContext = Context()

    val carWaiting = CarWaitingState()
    val carNotWaiting = CarNotWaitingState()

    val ewRedState = EWRedState()
    val ewYellowState = EWYellowState()
    val ewGreenState = EWGreenState()

    val nsRedState = NSRedState()
    val nsYellowState = NSYellowState()
    val nsGreenState = NSGreenState()

    var listener: ((String) -> Unit)? = null

    init {

        ewCarContext.listener = {
            listener?.invoke("Car state = ${it.javaClass.simpleName}")
            when (it) {
                is CarWaitingState -> {
                    delay(WAITING_TO_YELLOW_DELAY) {
                        nsYellowState.doAction(nsContext)
                    }

                }

                is CarNotWaitingState -> {

                }
            }
        }

        nsContext.listener = {
            listener?.invoke("NS state = ${it.javaClass.simpleName}")
            when (it) {
                is NSRedState -> {
                    ewGreenState.doAction(ewContext)
                }
                is NSYellowState -> {
                    delay(NS_YELLOW_DELAY) {
                        nsRedState.doAction(nsContext)
                    }
                }
                is NSGreenState -> {

                }
            }
        }

        ewContext.listener = {
            listener?.invoke("EW state = ${it.javaClass.simpleName}")
            when (it) {
                is EWRedState -> {
                    nsGreenState.doAction(nsContext)
                    carNotWaiting.doAction(ewCarContext)
                }
                is EWYellowState -> {
                    delay(EW_YELLOW_DELAY) {
                        ewRedState.doAction(ewContext)
                    }
                }
                is EWGreenState -> {
                    delay(EW_GREEN_DELAY) {
                        ewYellowState.doAction(ewContext)
                    }
                }
            }
        }

        // initial states
        nsGreenState.doAction(nsContext)
        ewRedState.doAction(ewContext)
    }

    fun setCarOnEW() {
        if (ewCarContext.state is CarNotWaitingState) {
            carWaiting.doAction(ewCarContext)
        }
    }

    companion object {
        const val TAG = "Intersection"
        const val WAITING_TO_YELLOW_DELAY = 5L
        const val NS_YELLOW_DELAY = 3L
        const val EW_YELLOW_DELAY = 3L
        const val EW_GREEN_DELAY = 7L
    }
}

class EWRedState: State {
    override fun doAction(context: Context) {
        context.state = this
    }
}

class EWYellowState: State {
    override fun doAction(context: Context) {
        context.state = this
    }
}

class EWGreenState: State {
    override fun doAction(context: Context) {
        context.state = this
    }
}

class NSRedState: State {
    override fun doAction(context: Context) {
        context.state = this
    }
}

class NSYellowState: State {
    override fun doAction(context: Context) {
        context.state = this
    }
}

class NSGreenState: State {
    override fun doAction(context: Context) {
        context.state = this
    }
}

class CarWaitingState: State {
    override fun doAction(context: Context) {
        context.state = this
    }
}

class CarNotWaitingState: State {
    override fun doAction(context: Context) {
        context.state = this
    }
}

@SuppressLint("CheckResult")
fun delay(seconds: Long, action: () -> Unit) {
    Completable.complete()
        .delay(seconds, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            action.invoke()
        }
}