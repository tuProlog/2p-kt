package it.unibo.tuprolog.ui.gui.presentation

/** One outcome of resolving a query, already formatted as display-ready text so a frontend never needs to know
 * about the underlying `Term`/`Solution` types. */
sealed interface SolutionPresentation {
    /** The query text this solution answers. */
    val query: String

    /** The goal succeeded; [solvedQuery]/[bindings] are pre-formatted, and [metadata] carries any extension's
     * per-solution extras (e.g. PLP's probability), keyed by a well-known string name. */
    data class Yes(
        override val query: String,
        val bindings: List<BindingPresentation> = emptyList(),
        val solvedQuery: String? = null,
        val metadata: Map<String, String> = emptyMap(),
    ) : SolutionPresentation

    /** The goal failed. */
    data class No(
        override val query: String,
    ) : SolutionPresentation

    /** The resolution was aborted, e.g. by an exception or a timeout ([isTimeout]). */
    data class Halt(
        override val query: String,
        val message: String,
        val logicStackTrace: List<String> = emptyList(),
        val isTimeout: Boolean = false,
    ) : SolutionPresentation
}
