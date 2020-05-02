package it.unibo.tuprolog.examples

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator

fun main() {
    val unificator = Unificator.default
    val term = Struct.of("father", Atom.of("abraham"), Atom.of("isaac"))
    val template = Struct.of("father", Var.of("X"), Atom.of("isaac"))

    val substitution = unificator.mgu(term, template)
    val match = unificator.match(term, template)
    val unified = unificator.unify(term, template)

    println(substitution) // {X_0=abraham}
    println(match)        // true
    println(unified)  // father(abraham, isaac)
}