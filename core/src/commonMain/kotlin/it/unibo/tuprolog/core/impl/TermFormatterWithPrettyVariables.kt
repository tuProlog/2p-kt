package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Var

internal class TermFormatterWithPrettyVariables(
    quoted: Boolean = true,
    numberVars: Boolean = false,
    ignoreOps: Boolean = false
) : AbstractTermFormatterForVariables(quoted, numberVars, ignoreOps) {

    override fun formatVar(variable: Var, suffix: String): String {
        val baseName = variable.name + suffix
        return if (variable.isNameWellFormed) {
            baseName
        } else {
            Var.escapeName(baseName)
        }
    }
}
