package it.unibo.tuprolog.parser.exceptions

/**
 * A human-readable description of one token or construct that could have appeared at an error.
 *
 * @property description concise expectation suitable for diagnostics and editor integrations
 */
data class SyntaxExpectation(
    val description: String,
)
