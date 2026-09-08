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

/**
 * Wraps the `guru.nidi:graphviz-java` library (which in turn shells out to a native Graphviz `dot`
 * installation, or falls back to a bundled JS implementation) to turn Graphviz DOT source -- typically
 * produced by [it.unibo.tuprolog.bdd.toDotString] from a [it.unibo.tuprolog.bdd.BinaryDecisionDiagram] --
 * into a PNG image. Since actually rendering an image the first time can be slow and may fail (e.g. if no
 * usable Graphviz engine can be found on the host), availability is probed once asynchronously via
 * [initialize] and cached in [isAvailable]/[isReady], instead of being checked synchronously on every call.
 */
object GraphvizRenderer {
    private var available: Future<Boolean> = CompletableFuture.completedFuture(false)

    /**
     * Kicks off, on a background thread, a one-shot probe that renders a trivial diagram (the terminal
     * node [BinaryDecisionDiagram.terminalOf] `true`) to determine whether this Graphviz installation can
     * actually produce PNGs; the outcome becomes visible through [isReady] (once the probe completes) and
     * [isAvailable] (its result). Call this once at application startup, before any [renderAsPNG]/
     * [it.unibo.tuprolog.ui.gui.GraphRenderView] is expected to work; until the probe completes, [isAvailable]
     * reports `false` and [isReady] reports `false`. Calling it again restarts the probe.
     */
    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun initialize() {
        available =
            CompletableFuture.supplyAsync {
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

    /**
     * Whether the [initialize] probe has completed and determined that Graphviz can render PNGs on this
     * host. `false` both while the probe is still running (see [isReady]) and once it has failed.
     */
    val isAvailable: Boolean get() = if (available.isDone) available.get() else false

    /** Whether the [initialize] probe has completed, regardless of its outcome (see [isAvailable]). */
    val isReady: Boolean get() = available.isDone

    /**
     * Renders [graph] (Graphviz DOT source) to a PNG image, written to [imageOutputStream].
     *
     * @throws UnsupportedOperationException if [isAvailable] is `false`, i.e. the [initialize] probe has not
     * (yet, or successfully) established that Graphviz can render on this host.
     */
    fun renderAsPNG(
        graph: String,
        imageOutputStream: OutputStream,
    ) {
        if (!isAvailable) {
            throw UnsupportedOperationException("Graphviz renderer is not available")
        }
        Graphviz
            .fromString(graph)
            .render(Format.PNG)
            .toOutputStream(imageOutputStream)
    }
}
