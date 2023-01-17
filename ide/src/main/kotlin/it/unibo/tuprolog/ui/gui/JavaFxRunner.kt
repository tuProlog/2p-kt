package it.unibo.tuprolog.ui.gui

import javafx.application.Platform
import java.util.concurrent.Executors

object JavaFxRunner : Runner {

    @Volatile
    private var backgroundThreadCount = 0

    private val backgroundExecutor = Executors.newCachedThreadPool {
        Thread(it, "background-thread-${++backgroundThreadCount}")
    }

    @Volatile
    private var ioThreadCount = 0

    private val ioExecutor = Executors.newCachedThreadPool {
        Thread(it, "io-thread-${++ioThreadCount}")
    }

    override fun ui(action: () -> Unit) = Platform.runLater(action)

    override fun background(action: () -> Unit) = backgroundExecutor.execute(action)

    override fun io(action: () -> Unit) = ioExecutor.execute(action)
}
