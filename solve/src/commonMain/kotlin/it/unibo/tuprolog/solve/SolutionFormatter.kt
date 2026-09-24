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
        /**
         * Creates a [SolutionFormatter] rendering terms with [termFormatter] (defaults to pretty-printed
         * expressions). When [groundQueriesHaveBooleanSolution] is `true`, a `yes` solution to a *ground* query
         * (no variables at all) is rendered simply as `yes.`, rather than repeating the (identical) solved query.
         */
        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun of(
            termFormatter: TermFormatter = TermFormatter.prettyExpressions(),
            groundQueriesHaveBooleanSolution: Boolean = false,
        ): SolutionFormatter = SolutionFormatterImpl(termFormatter, groundQueriesHaveBooleanSolution)

        /** Creates a [SolutionFormatter] that pretty-prints expressions using the given [operators] for notation. */
        @JsName("withOperators")
        @JvmStatic
        @JvmOverloads
        fun withOperators(
            operators: OperatorSet,
            groundQueriesHaveBooleanSolution: Boolean = false,
        ): SolutionFormatter = of(TermFormatter.prettyExpressions(true, operators), groundQueriesHaveBooleanSolution)
    }
}
