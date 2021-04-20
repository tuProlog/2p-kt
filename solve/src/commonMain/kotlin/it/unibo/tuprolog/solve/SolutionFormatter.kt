package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Formatter
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.impl.SolutionFormatterImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface SolutionFormatter : Formatter<Solution> {
    companion object {
        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun of(termFormatter: TermFormatter = TermFormatter.prettyExpressions()): SolutionFormatter =
            SolutionFormatterImpl(termFormatter)

        @JsName("withOperators")
        @JvmStatic
        fun withOperators(operators: OperatorSet): SolutionFormatter =
            of(TermFormatter.prettyExpressions(true, operators))
    }
}
