package it.unibo.tuprolog.core

interface Fact : Rule {

    override val body: Term
        get() = Truth.`true`()

    override val isFact: Boolean
        get() = true

    companion object {
        fun of(head: Struct): Fact {
            return FactImpl(head)
        }
    }
}