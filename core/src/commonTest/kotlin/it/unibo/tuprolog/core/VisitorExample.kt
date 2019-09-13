package it.unibo.tuprolog.core

fun main() {
    val term: Term = Scope.empty {
        ruleOf(
                structOf("member", varOf("X"), consOf(whatever(), varOf("T"))),
                structOf("member", varOf("X"), varOf("T"))
        )
    }

    println(term.toString())

    println(term.accept(object : TermVisitor<String> {

        override fun defaultValue(term: Term): String = "__ERROR__"

        override fun visit(term: Var): String {
            return term.name
        }

        override fun visit(term: Constant): String {
            return term.value.toString()
        }

        override fun visit(term: Struct): String {
            return "'${term.functor}'(${term.argsSequence.map { it.accept(this) }.joinToString(", ")})"
        }
    }))
}