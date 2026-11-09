package it.unibo.tuprolog.ui.gui.presentation

import it.unibo.tuprolog.solve.timing.formatTime
import kotlin.time.Duration

/** One outcome of resolving a query, already formatted as display-ready text so a frontend never needs to know
 * about the underlying `Term`/`Solution` types. */
sealed interface SolutionPresentation {
    /** The query text this solution answers. */
    val query: String

    /** The solver's active time spent producing this solution, when it was measured. */
    val time: Duration?

    /** The goal succeeded; [solvedQuery]/[bindings] are pre-formatted, and [metadata] carries any extension's
     * per-solution extras (e.g. PLP's probability), keyed by a well-known string name. */
    data class Yes(
        override val query: String,
        val bindings: List<BindingPresentation> = emptyList(),
        val solvedQuery: String? = null,
        val metadata: Map<String, String> = emptyMap(),
        override val time: Duration? = null,
    ) : SolutionPresentation

    /** The goal failed. */
    data class No(
        override val query: String,
        override val time: Duration? = null,
    ) : SolutionPresentation

    /** The resolution was aborted, e.g. by an exception or a timeout ([isTimeout]). */
    data class Halt(
        override val query: String,
        val message: String,
        val logicStackTrace: List<String> = emptyList(),
        val isTimeout: Boolean = false,
        override val time: Duration? = null,
    ) : SolutionPresentation
}

/** A trailing ` (1.234ms)`-style annotation with this solution's [SolutionPresentation.time], if measured. */
val SolutionPresentation.timeAnnotation: String
    get() = time?.let { " (${it.formatTime()})" }.orEmpty()

/**
 * A trailing ` (1.234ms)`-style annotation with the time of the whole resolution these solutions belong to (the sum
 * of their times), if any of them was measured.
 */
val List<SolutionPresentation>.totalTimeAnnotation: String
    get() =
        mapNotNull { it.time }
            .takeIf { it.isNotEmpty() }
            ?.reduce(Duration::plus)
            ?.let { " (${it.formatTime()})" }
            .orEmpty()
