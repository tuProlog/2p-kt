package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.FactImpl

interface Fact : Rule {

    override val body: Term
        get() = Truth.`true`()

    override val isFact: Boolean
        get() = true

    companion object {
        fun of(head: Struct): Fact = FactImpl(head)
    }
}
