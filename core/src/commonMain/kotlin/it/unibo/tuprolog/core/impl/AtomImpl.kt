package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

internal open class AtomImpl(override val functor: String) : StructImpl(functor, emptyArray()), Atom {

    override val args: Array<Term> = super<StructImpl>.args

    override val argsList: List<Term>
        get() = emptyList()

    override val isGround: Boolean
        get() = true

    override val variables: Sequence<Var>
        get() = emptySequence()
}
