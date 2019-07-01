package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Set as LogicSet

internal open class SetImpl(override val args: Array<Term>) : StructImpl(LogicSet.FUNCTOR, args), LogicSet {

    override val functor: String
        get() = super<LogicSet>.functor

    override fun toString(): String = "{${args.joinToString(", ")}}"
}