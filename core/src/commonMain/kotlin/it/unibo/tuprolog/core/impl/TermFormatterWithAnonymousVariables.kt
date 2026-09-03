package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Var

internal class TermFormatterWithAnonymousVariables(
    quoted: Boolean = true,
    numberVars: Boolean = false,
    ignoreOps: Boolean = false,
    tagsOptions: TermFormatter.TagsFormattingOptions = TermFormatter.TagsFormattingOptions()
) : AbstractTermFormatter(quoted, numberVars, ignoreOps, tagsOptions) {
    override fun visitVar(term: Var): String = "_${term.id}"
}
