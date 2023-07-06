package it.unibo.tuprolog.ui.gui

import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.toDotString
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.lang.UnsupportedOperationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

object GraphvizRenderer {

    private var available: Future<Boolean> = CompletableFuture.completedFuture(false)

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun initialize() {
        available = CompletableFuture.supplyAsync {
            try {
                val outputStream = ByteArrayOutputStream()
                val sampleBDD = BinaryDecisionDiagram.terminalOf<Boolean>(true)
                Graphviz
                    .fromString(sampleBDD.toDotString())
                    .render(Format.PNG)
                    .toOutputStream(outputStream)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    val isAvailable: Boolean get() = if (available.isDone) available.get() else false

    val isReady: Boolean get() = available.isDone

    fun renderAsPNG(graph: String, imageOutputStream: OutputStream) {
        if (!isAvailable) {
            throw UnsupportedOperationException("Graphviz renderer is not available")
        }
        Graphviz
            .fromString(graph)
            .render(Format.PNG)
            .toOutputStream(imageOutputStream)
    }
}
