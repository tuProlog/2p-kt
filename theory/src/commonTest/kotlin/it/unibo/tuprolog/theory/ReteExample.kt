package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*

fun main() {
    // f(a) :- do_something.
    // f(a, b(X), do_something_else) :- true.
    // g(c(_, _)) :- true.

    val theory = ReteTree.of(
            Clause.of(Atom.of("a"), Var.of("A"), Var.of("A")),
            Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
            Rule.of(Struct.of("f", Atom.of("a")), Atom.of("do_something")),
            Rule.of(Struct.of("f", Atom.of("a"), Struct.of("b", Var.of("X"))), Atom.of("do_something_else")),
            Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
            with(Scope.empty()) {
                ruleOf(structOf("g", structOf("c", varOf("A"), varOf("B"))), varOf("A"))
            },
            Fact.of(Struct.of("g", Struct.of("c", Var.anonymous(), Var.anonymous())))
    )
    println(theory.toString(true))

//    println(theory.get(Clause.of(Struct.of("f", Var.anonymous()), Var.anonymous())).joinToString("\n"))

    println(theory.get("f", 1).joinToString("\n", "[", "]"))
    println(theory.get("f", 2).joinToString("\n", "[", "]"))
}
