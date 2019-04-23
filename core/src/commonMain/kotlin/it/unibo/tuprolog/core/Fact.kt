package it.unibo.tuprolog.core

internal interface Fact : Rule {

    override val body: Term
        get() = Truth.`true`()

    override val isFact: Boolean
        get() = true

}