package it.unibo.tuprolog.core

import kotlin.collections.List

internal open class AtomImpl(override val functor: String) : StructImpl(functor, arrayOf()), Atom {

    override val args: Array<Term> = super<StructImpl>.args

    override fun strictlyEquals(other: Term): Boolean {
        return other is AtomImpl
                && value == other.value
    }

    override val argsList: List<Term> by lazy {
        emptyList<Term>()
    }

    override val isGround: Boolean
        get() = super<Atom>.isGround
}