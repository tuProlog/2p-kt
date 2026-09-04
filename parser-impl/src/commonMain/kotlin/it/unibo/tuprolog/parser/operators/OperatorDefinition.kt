package it.unibo.tuprolog.parser.operators

import it.unibo.tuprolog.parser.exceptions.InvalidOperatorDefinitionException

/**
 * Declares one Prolog operator name, specifier, and priority.
 *
 * One name may have several simultaneous definitions as long as their [specifier]s differ. Prolog
 * priorities range from [MIN_PRIORITY] to [MAX_PRIORITY], with lower values binding more tightly.
 *
 * @property name non-empty operator spelling
 * @property specifier fixity and operand constraints
 * @property priority Prolog priority in `1..1200`
 * @throws InvalidOperatorDefinitionException if [name] is empty or [priority] is outside the range
 */
data class OperatorDefinition(
    val name: String,
    val specifier: Associativity,
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
        /** Strongest valid Prolog operator priority. */
        const val MIN_PRIORITY: Int = 1

        /** Weakest valid Prolog operator priority and the top-level expression limit. */
        const val MAX_PRIORITY: Int = 1200
    }
}
