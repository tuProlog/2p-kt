package it.unibo.tuprolog.parser.tree

import it.unibo.tuprolog.parser.operators.OperatorDefinition

data class OperatorUse(
    val tokenId: Int,
    val definition: OperatorDefinition,
    val role: OperatorRole,
)