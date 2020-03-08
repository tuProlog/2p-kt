package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TermFormatterWithPrettyVariables
import kotlin.jvm.JvmStatic

interface TermFormatter : Formatter<Term>, TermVisitor<String> {
    override fun format(value: Term): String {
        return value.accept(this)
    }

    companion object {
        @JvmStatic
        val withPrettyVariables: TermFormatter
            get() = TermFormatterWithPrettyVariables()
    }
}