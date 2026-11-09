package it.unibo.tuprolog.ui.gui.presentation

import it.unibo.tuprolog.solve.timing.formatTime
import it.unibo.tuprolog.utils.time.formatLocalTimestamp
import kotlin.math.round

/** A query header; older history has no timestamp. */
fun queryLabel(
    query: String,
    submittedAt: Long?,
): String = "${submittedAt?.let { "[${formatLocalTimestamp(it)}] " }.orEmpty()}?- ${query.trimEnd().removeSuffix(".")}."

/** The outcome keyword, shared by text and styled renderers. */
val SolutionPresentation.keyword: String
    get() =
        when (this) {
            is SolutionPresentation.Yes -> "yes"
            is SolutionPresentation.No -> "no"
            is SolutionPresentation.Halt -> if (isTimeout) "timeout" else "halt"
        }

/** The underlying goal, without numbering, outcome or timing decorations. */
val SolutionPresentation.logicText: String
    get() = (this as? SolutionPresentation.Yes)?.solvedQuery ?: query

/** A numbered outcome with its measured duration before the solution. */
fun SolutionPresentation.resultLabel(number: Int): String =
    "$number. ${time?.let { "[${it.formatTime()}] " }.orEmpty()}" +
        when (this) {
            is SolutionPresentation.Yes ->
                (solvedQuery?.let { "yes: $it" } ?: "yes.") +
                    metadata["probability"]
                        ?.toDoubleOrNull()
                        ?.let {
                            " (p=${round(
                                it * PROBABILITY_SCALE,
                            ) / DECIMAL_SCALE}%)"
                        }.orEmpty()
            is SolutionPresentation.No -> "no"
            is SolutionPresentation.Halt -> "$keyword: $message"
        }

/** Binding or stack trace rows nested beneath an outcome. */
val SolutionPresentation.detailLines: List<String>
    get() =
        when (this) {
            is SolutionPresentation.Yes -> bindings.map { "${it.variable} = ${it.value}" }
            is SolutionPresentation.No -> emptyList()
            is SolutionPresentation.Halt -> logicStackTrace.map { "at $it" }
        }

private const val PROBABILITY_SCALE = 1000.0
private const val DECIMAL_SCALE = 10.0
