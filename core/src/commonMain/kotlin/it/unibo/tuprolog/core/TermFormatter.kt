package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.SimpleTermFormatter
import it.unibo.tuprolog.core.impl.TermFormatterWithPrettyExpressions
import it.unibo.tuprolog.core.impl.TermFormatterWithPrettyVariables
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.jvm.JvmStatic

interface TermFormatter : Formatter<Term>, TermVisitor<String> {
    override fun format(value: Term): String {
        return value.accept(this)
    }

    companion object {
        @JvmStatic
        val prettyVariables: TermFormatter
            get() = TermFormatterWithPrettyVariables()

        @JvmStatic
        val prologDefaults: TermFormatter
            get() = prettyExpressions(OperatorSet.DEFAULT)

        @JvmStatic
        fun prettyExpressions(prettyVariables: Boolean, operatorSet: OperatorSet): TermFormatter {
            return if (prettyVariables) {
                TermFormatterWithPrettyExpressions(TermFormatterWithPrettyVariables(), operatorSet)
            } else {
                TermFormatterWithPrettyExpressions(SimpleTermFormatter, operatorSet)
            }
        }

        @JvmStatic
        fun prettyExpressions(operatorSet: OperatorSet): TermFormatter {
            return prettyExpressions(true, operatorSet)
        }

        @JvmStatic
        fun prettyExpressions(prettyVariables: Boolean): TermFormatter {
            return prettyExpressions(prettyVariables, OperatorSet.DEFAULT)
        }

        @JvmStatic
        fun prettyExpressions(): TermFormatter {
            return prettyExpressions(true, OperatorSet.DEFAULT)
        }
    }
}