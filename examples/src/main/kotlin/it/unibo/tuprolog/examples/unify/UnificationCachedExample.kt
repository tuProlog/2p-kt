package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Unificator

fun main() {
    val unificator = Unificator.default
    val cached = Unificator.cached(unificator, capacity = 5)

    val term = Struct.of("father", Atom.of("abraham"), Atom.of("isaac"))
    val template = Struct.of("father", Var.of("X"), Atom.of("isaac"))

    val substitution: Substitution = cached.mgu(term, template)
    val match: Boolean = cached.match(term, template)
    val unified: Term? = cached.unify(term, template)

    println(substitution) // {X_0=abraham}
    println(match)        // true
    println(unified)      // father(abraham, isaac)
}