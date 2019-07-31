package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.Term

internal object EmptySetImpl : SetImpl(null), EmptySet {

    override val args: Array<Term> by lazy { super<EmptySet>.args }

    override val argsList: List<Term> by lazy { super<SetImpl>.argsList }

    override val functor: String = super<EmptySet>.functor

    override val isGround: Boolean = super<EmptySet>.isGround
}