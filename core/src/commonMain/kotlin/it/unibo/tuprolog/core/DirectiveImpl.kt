package it.unibo.tuprolog.core

internal class DirectiveImpl(override val body: Term)
    : ClauseImpl(null, body), Directive {

    override val head: Struct?
        get() = super<Directive>.head
}