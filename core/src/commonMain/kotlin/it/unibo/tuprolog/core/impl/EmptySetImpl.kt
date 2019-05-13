package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.EmptySet
import kotlin.collections.List

internal object EmptySetImpl : SetImpl(emptyArray()), EmptySet {

    override val args: Array<Term>
        get() = super<EmptySet>.args

    override val argsList: List<Term>
        get() = super<SetImpl>.argsList

    override val functor: String
        get() = super<EmptySet>.functor

    override val isGround: Boolean
        get() = super<EmptySet>.isGround
}