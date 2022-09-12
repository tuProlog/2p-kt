package it.unibo.tuprolog.ui.gui

import java.util.concurrent.Executor

open class DefaultJvmRunner(private val executor: Executor) : Runner {
    override fun ui(action: () -> Unit) {
        executor.execute(action)
    }

    override fun background(action: () -> Unit) {
        executor.execute(action)
    }

    override fun io(action: () -> Unit) {
        executor.execute(action)
    }
}
