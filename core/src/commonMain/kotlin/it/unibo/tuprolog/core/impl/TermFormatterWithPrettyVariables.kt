package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Set

internal class TermFormatterWithPrettyVariables : AbstractTermFormatter() {

    private val variables: MutableMap<String, MutableMap<Var, String>> = mutableMapOf()

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
}