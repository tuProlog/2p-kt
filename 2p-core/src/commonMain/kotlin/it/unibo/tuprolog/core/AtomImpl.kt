package it.unibo.tuprolog.core

import kotlin.collections.List

internal open class AtomImpl(override val functor: String) : StructImpl(functor, arrayOf()), Atom {
    override val args: Array<Term> = super<StructImpl>.args

    override val argsList: List<Term>
        get() = super<StructImpl>.argsList
}