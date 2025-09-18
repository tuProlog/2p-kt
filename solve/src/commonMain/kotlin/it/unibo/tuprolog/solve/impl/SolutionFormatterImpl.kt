package it.unibo.tuprolog.solve.impl

import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolutionFormatter
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.TimeOutException

internal class SolutionFormatterImpl(
    private val termFormatter: TermFormatter,
) : SolutionFormatter {
    override fun format(value: Solution): String =
        when (value) {
            is Solution.Yes -> formatYes(value)
            is Solution.No -> "no."
            is Solution.Halt ->
                when (val e = value.exception) {
                    is TimeOutException -> "timeout."
                    is HaltException -> "halt: ${e.exitStatus}"
                    else -> formatException(e)
                }
        }

    private fun formatException(e: ResolutionException): String =
        "halt: ${e.message ?: "<no message>"}" +
            e.logicStackTrace.joinToString(STACK_ITEM_SEPARATOR, STACK_ITEM_SEPARATOR) { termFormatter.format(it) }

    private fun formatYes(value: Solution.Yes): String =
        "yes: ${termFormatter.format(value.solvedQuery)}" +
            value.substitution.asIterable().joinToString(ASSIGNMENT_SEPARATOR, ASSIGNMENT_SEPARATOR) { (v, t) ->
                "${termFormatter.format(v)} = ${termFormatter.format(t)}"
            }

    companion object {
        private const val ASSIGNMENT_SEPARATOR = "\n    "
        private const val STACK_ITEM_SEPARATOR = "${ASSIGNMENT_SEPARATOR}at "
    }
}
