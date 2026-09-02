package it.unibo.tuprolog.parser.tree

import it.unibo.tuprolog.parser.operators.OperatorDefinition

/**
 * Resolved interpretation of one operator token.
 *
 * @property tokenId absolute ID of the operator token
 * @property definition definition selected from the active operator table
 * @property role prefix, infix, or postfix role used at this occurrence
 */
data class OperatorUse(
    val tokenId: Int,
    val definition: OperatorDefinition,
    val role: OperatorRole,
)
