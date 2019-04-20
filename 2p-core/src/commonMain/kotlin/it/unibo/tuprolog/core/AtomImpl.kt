package it.unibo.tuprolog.core

internal open class AtomImpl(override val functor: String) : StructImpl(functor, arrayOf()), Atom {
    override val args: Array<Term> = super<StructImpl>.args
}