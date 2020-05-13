package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth

internal class FactImpl(override val head: Struct) : RuleImpl(head, Truth.TRUE), Fact {

    override val isWellFormed: Boolean = true

    override val body: Term = super<RuleImpl>.body
}