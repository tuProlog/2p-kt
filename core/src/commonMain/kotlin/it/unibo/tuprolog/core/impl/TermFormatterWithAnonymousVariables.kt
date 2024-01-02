package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Var

internal class TermFormatterWithAnonymousVariables(
    quoted: Boolean = true,
    numberVars: Boolean = false,
    ignoreOps: Boolean = false,
) : AbstractTermFormatter(quoted, numberVars, ignoreOps) {
    override fun visitVar(term: Var): String = "_${term.id}"
}
