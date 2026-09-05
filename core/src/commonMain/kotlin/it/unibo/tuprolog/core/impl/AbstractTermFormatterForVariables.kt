package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Terms.ANONYMOUS_VAR_NAME
import it.unibo.tuprolog.core.Var

internal abstract class AbstractTermFormatterForVariables(
    quoted: Boolean = true,
    numberVars: Boolean = false,
    ignoreOps: Boolean = false,
    tagsOptions: TermFormatter.TagsFormattingOptions = TermFormatter.TagsFormattingOptions(),
) : AbstractTermFormatter(quoted, numberVars, ignoreOps, tagsOptions) {
    private val variables: MutableMap<String, MutableMap<Var, String>> = mutableMapOf()

    protected abstract fun formatVar(
        variable: Var,
        suffix: String,
    ): String

    override fun visitVar(term: Var): String =
        when {
            term.isAnonymous -> {
                ANONYMOUS_VAR_NAME
            }

            variables.containsKey(term.name) -> {
                val homonymous = variables[term.name]!!
                if (homonymous.containsKey(term)) {
                    formatVar(term, homonymous[term]!!)
                } else {
                    val homonymousCount = homonymous.size
                    val suffix = homonymousCount.toString()
                    homonymous[term] = suffix
                    formatVar(term, suffix)
                }
            }

            else -> {
                variables[term.name] = mutableMapOf(term to "")
                formatVar(term, "")
            }
        }.plusTagsOf(term)
}
