package it.unibo.tuprolog.parser.operators

import it.unibo.tuprolog.parser.exceptions.InvalidOperatorDefinitionException

data class OperatorDefinition(
    val name: String,
    val specifier: OperatorSpecifier,
    val priority: Int,
) {
    init {
        if (name.isEmpty()) {
            throw InvalidOperatorDefinitionException("Operator names cannot be empty")
        }
        if (priority !in MIN_PRIORITY..MAX_PRIORITY) {
            throw InvalidOperatorDefinitionException(
                "Operator priority must be in $MIN_PRIORITY..$MAX_PRIORITY, found $priority",
            )
        }
    }

    companion object {
        const val MIN_PRIORITY: Int = 1
        const val MAX_PRIORITY: Int = 1200
    }
}
