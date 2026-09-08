package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Formatter
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.impl.SolutionFormatterImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A [Formatter] specialized in turning [Solution]s into human-readable [String]s, e.g. for a REPL or logging.
 *
 * Formatting typically delegates to an underlying [TermFormatter] to render the [Solution.solvedQuery]/
 * [Solution.substitution] terms involved.
 */
interface SolutionFormatter : Formatter<Solution> {
    companion object {
        /** Creates a [SolutionFormatter] rendering terms with [termFormatter] (defaults to pretty-printed expressions). */
        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun of(termFormatter: TermFormatter = TermFormatter.prettyExpressions()): SolutionFormatter =
            SolutionFormatterImpl(termFormatter)

        /** Creates a [SolutionFormatter] that pretty-prints expressions using the given [operators] for notation. */
        @JsName("withOperators")
        @JvmStatic
        fun withOperators(operators: OperatorSet): SolutionFormatter =
            of(TermFormatter.prettyExpressions(true, operators))
    }
}
