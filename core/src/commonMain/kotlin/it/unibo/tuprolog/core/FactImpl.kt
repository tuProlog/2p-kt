package it.unibo.tuprolog.core

internal class FactImpl(override val head: Struct)
    : RuleImpl(head, TruthImpl.True), Fact {

    override val body: Term
        get() = super<RuleImpl>.body
}