package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

internal open class AtomImpl(
    override val functor: String,
    tags: Map<String, Any>
) : StructImpl(functor, emptyArray(), tags), Atom {

    override val args: Array<Term> = super<StructImpl>.args

    override val argsList: List<Term>
        get() = emptyList()

    override val isGround: Boolean
        get() = true

    override val variables: Sequence<Var>
        get() = emptySequence()

    override fun tag(name: String, value: Any): Atom {
        return AtomImpl(functor, extendTags(name, value))
    }
}
