package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Set

internal class TermFormatterWithPrettyVariables : TermFormatter {

    private val variables: MutableMap<String, MutableMap<Var, String>> = mutableMapOf()

    override fun defaultValue(term: Term): String {
        return term.toString()
    }

    private fun formatVar(variable: Var, suffix: String): String {
        val baseName = variable.name + suffix
        return if (variable.isNameWellFormed) {
            baseName
        } else {
            Var.escapeName(baseName)
        }
    }

    override fun visitVar(term: Var): String {
        return if (term.isAnonymous) {
            return Var.ANONYMOUS_VAR_NAME
        } else if (term.name in variables) {
            val homonymous = variables[term.name]!!
            if (term in homonymous) {
                formatVar(term, homonymous[term]!!)
            } else {
                val homonymousCount = homonymous.size
                val suffix = "_$homonymousCount"
                homonymous[term] = suffix
                formatVar(term, suffix)
            }
        } else {
            variables[term.name] = mutableMapOf(term to "")
            return formatVar(term, "")
        }
    }

    override fun visitStruct(term: Struct): String =
        (if (term.isFunctorWellFormed) term.functor else Struct.escapeFunctor(term.functor)) +
                term.argsSequence.map { it.accept(this) }.joinToString(", ", "(", ")")

    override fun visitSet(term: Set): String =
        term.unfoldedList.joinToString(", ", "{", "}") {
            it.accept(this)
        }

    override fun visitCons(term: Cons): String =
        with(term.unfoldedList) {
            val last = last()
            val base = subList(0, lastIndex).joinToString(", ", "[", "") {
                it.accept(this@TermFormatterWithPrettyVariables)
            }
            val lastString = if (last is EmptyList) {
                "]"
            } else {
                " | ${last.accept(this@TermFormatterWithPrettyVariables)}]"
            }
            return base + lastString
        }

    override fun visitTuple(term: Tuple): String =
        term.unfoldedList.joinToString(", ", "(", ")") {
            it.accept(this)
        }

    override fun visitRule(term: Rule): String =
        "${term.head.accept(this)} :- ${term.body.accept(this)}"

    override fun visitFact(term: Fact): String =
        "${term.head.accept(this)} :- true"

    override fun visitDirective(term: Directive): String =
        ":- ${term.body.accept(this)}"
}