package it.unibo.tuprolog.core

internal open class RuleImpl(override val head: Struct, override val body: Term)
    : ClauseImpl(head, body), Rule {

}

