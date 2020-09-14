package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.output.defaultCliktConsole
import it.unibo.tuprolog.core.TermFormatter.Companion.prettyExpressions
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.TimeOutException

object TuPrologUtils {

    private fun printSolution(sol: Solution, operatorSet: OperatorSet) {
        when (sol) {
            is Solution.Yes -> {
                printYesSolution(sol, operatorSet)
            }
            is Solution.No -> {
                printNoSolution(sol, operatorSet)
            }
            is Solution.Halt -> {
                printHaltSolution(sol, operatorSet)
            }
        }
    }

    private fun printYesSolution(sol: Solution.Yes, operatorSet: OperatorSet) {
        val formatter = prettyExpressions(operatorSet)
        TermUi.echo("yes: ${sol.solvedQuery.format(formatter)}.")
        if (sol.substitution.isNotEmpty()) {
            val substitutions = sol.substitution.entries.joinToString("\n    ") {
                val prettyVariable = it.key.format(formatter)
                val prettyValue = it.value.format(formatter)
                "$prettyVariable = $prettyValue"
            }
            TermUi.echo("    $substitutions")
        }
    }

    private fun printHaltSolution(sol: Solution.Halt, operatorSet: OperatorSet) {
        when (val ex = sol.exception) {
            is TimeOutException -> {
                TermUi.echo("timeout.")
            }
            is HaltException -> {
                TermUi.echo("goodbye.")
                throw ProgramResult(0)
            }
            else -> {
                if (ex.message == null) {
                    TermUi.echo("halt.")
                } else {
                    TermUi.echo("halt: ${ex.message?.trim()}")
                }
                val sep = "\n    at "
                val formatter = prettyExpressions(operatorSet)
                val stacktrace = ex.prologStackTrace.joinToString(sep) { it.format(formatter) }
                TermUi.echo("    at $stacktrace")
            }
        }
    }

    fun printParseException(e: ParseException) {
        TermUi.echo("# ${e.message?.capitalize()}", err = true)
    }

    private fun printEndOfSolutions() {
        // do nothing
    }

    fun readQuery(): String {
        val query: String? = TermUi.prompt("?-", promptSuffix = " ")
        return when {
            query == null -> {
                throw NullInputException("The standard input has been close unexpectedly")
            }
            query.trim().endsWith('.') -> {
                query
            }
            else -> {
                readQueryMultiline(query)
            }
        }
    }

    private fun readQueryMultiline(query: String): String {
        val longQuery = StringBuilder(query)
        var lastRead = query
        while (!lastRead.trim().endsWith('.')) {
            lastRead = TermUi.prompt(">", promptSuffix = " ")
                ?: throw NullInputException("The standard input has been closed unexpectedly")
            longQuery.append(lastRead)
        }
        return longQuery.toString()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun printNoSolution(sol: Solution.No, operatorSet: OperatorSet) {
        TermUi.echo("no.")
    }

    fun printSolutions(solutions: Iterator<Solution>, operatorSet: OperatorSet) {
        var first = true
        while (solutions.hasNext()) {
            if (!first) {
                val cmd = defaultCliktConsole().promptForLine("", false)?.trim()
                if (cmd != ";") break
            } else {
                first = false
            }
            printSolution(solutions.next(), operatorSet)
        }
        printEndOfSolutions()
    }

    fun printNumSolutions(solutions: Iterator<Solution>, maxSolutions: Int, operatorSet: OperatorSet) {
        var i = 0
        while (i < maxSolutions && solutions.hasNext()) {
            i++
            printSolution(solutions.next(), operatorSet)
        }
        printEndOfSolutions()
    }
}
