package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

interface TermObjectifier : Objectifier<Term>, TermVisitor<Any> {

    override fun objectify(value: Term): Any {
        return visit(value)
    }

    companion object {
        val instance: TermObjectifier = termObjectifier()
    }

}