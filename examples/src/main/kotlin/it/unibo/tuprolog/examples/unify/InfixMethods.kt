package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.unify.Unificator.Companion.unifyWith

fun main() {
    val term = Struct.of("father", Atom.of("abraham"), Atom.of("isaac"))
    val template = Struct.of("father", Var.of("X"), Atom.of("isaac"))

    println(term mguWith  template)     // {X_0=abraham}
    println(term matches template)      // true
    println(term unifyWith template)    // father(abraham, isaac)
}