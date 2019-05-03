package it.unibo.tuprolog.core

internal open class RuleImpl(override val head: Struct, override val body: Term)
    : ClauseImpl(head, body), Rule {

}

internal class DirectiveImpl(override val body: Term)
    : ClauseImpl(null, body), Directive {

    override val head: Struct?
        get() = super<Directive>.head
}