package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

internal open class AtomImpl(override val functor: String) : StructImpl(functor, arrayOf()), Atom {

    override val args: Array<Term> = super<StructImpl>.args

    override val argsList: List<Term> by lazy { emptyList<Term>() }

    override val isGround: Boolean by lazy { super<Atom>.isGround }

    override val variables: Sequence<Var> by lazy { super<Atom>.variables }
}
