package it.unibo.tuprolog.ui.gui.presentation

/** One loaded library's contents, already formatted as display-ready text/signatures, for the libraries
 * inspector panel. */
data class LibraryPresentation(
    val alias: String,
    val predicates: List<String> = emptyList(),
    val operators: List<OperatorPresentation> = emptyList(),
    val functions: List<String> = emptyList(),
    /** The library's own Markdown help. */
    val help: String = "",
    /** Markdown documentation, keyed by the same `name/arity` strings as [predicates] and [functions]. */
    val documentation: Map<String, String> = emptyMap(),
) {
    /** The documentation of [operator]'s signature: infix specifiers (`xfx`, `xfy`, `yfx`) have arity 2, others 1. */
    fun documentationOf(operator: OperatorPresentation): String? =
        documentation["${operator.name}/${if (operator.specifier.length == 3) 2 else 1}"]
}
