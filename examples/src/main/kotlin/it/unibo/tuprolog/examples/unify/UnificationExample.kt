package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Unificator

// TODO: with/without occurs-check

fun main() {
    val unificator = Unificator.default

    val term = Struct.of("father", Atom.of("abraham"), Atom.of("isaac"))
    val template = Struct.of("father", Var.of("X"), Atom.of("isaac"))

    val substitution: Substitution = unificator.mgu(term, template)
    val match: Boolean = unificator.match(term, template)
    val unified: Term? = unificator.unify(term, template)

    println(substitution) // {X_0=abraham}
    println(match)        // true
    println(unified)      // father(abraham, isaac)
}