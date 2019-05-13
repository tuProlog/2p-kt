package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Set

internal open class SetImpl(override val args: Array<Term>) : StructImpl(Set.FUNCTOR, args), Set {

    override val functor: String
        get() = super<Set>.functor

    override fun toString(): String {
        return "{${args.joinToString(", ")}}"
    }
}