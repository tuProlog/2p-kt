package it.unibo.tuprolog.core

interface Directive : Clause {

    override val head: Struct?
        get() = null

    override val isRule: Boolean
        get() = false

    override val isFact: Boolean
        get() = false

    override val isDirective: Boolean
        get() = true

    companion object {
        fun of(body1: Term, vararg body: Term): Directive {
            return DirectiveImpl(Struct.conjunction(sequenceOf(body1) + sequenceOf(*body)))
        }
    }
}