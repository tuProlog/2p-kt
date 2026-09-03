package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Var

internal class TermFormatterWithPrettyVariables(
    quoted: Boolean = true,
    numberVars: Boolean = false,
    ignoreOps: Boolean = false,
    tagsOptions: TermFormatter.TagsFormattingOptions = TermFormatter.TagsFormattingOptions()
) : AbstractTermFormatterForVariables(quoted, numberVars, ignoreOps, tagsOptions) {
    override fun formatVar(
        variable: Var,
        suffix: String,
    ): String {
        val baseName = variable.name + suffix
        return if (variable.isNameWellFormed) {
            baseName
        } else {
            Var.escapeName(baseName)
        }
    }
}
