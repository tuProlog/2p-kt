package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.output.TermUi
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.TimeOutException

object TuPrologUtils {

    private fun printSolution(sol: Solution) {
        when (sol) {
            is Solution.Yes -> {
                printYesSolution(sol)
            }
            is Solution.No -> {
                printNoSolution(sol)
            }
            is Solution.Halt -> {
                printHaltSolution(sol)
            }
        }
    }

    private fun printYesSolution(sol: Solution.Yes) {
        TermUi.echo("yes: ${sol.solvedQuery.format(TermFormatter.prettyExpressions())}.")
        if (sol.substitution.isNotEmpty()) {
            val sep = "\n    "
            val substitutions = sol.substitution.entries.joinToString(sep) {
                val prettyVariable = it.key.format(TermFormatter.prettyVariables())
                val prettyValue = it.value.format(TermFormatter.prettyVariables())
                "$prettyVariable = $prettyValue"
            }
            TermUi.echo("    $substitutions")
        }
    }

    private fun printHaltSolution(sol: Solution.Halt) {
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
                val stacktrace = ex.prologStackTrace.joinToString(sep) { it.toString() }
                TermUi.echo("    at $stacktrace")
            }
        }
    }

    fun printParseException(e: ParseException) {
        TermUi.echo(e.message?.capitalize(), err = true)
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

    private fun printNoSolution(sol: Solution.No) {
        TermUi.echo("no.")
    }

    fun printSolutions(solutions: Iterator<Solution>) {
        var first = true
        while (solutions.hasNext()) {
            if (!first) {
                val cmd = TermUi.prompt("", promptSuffix = "")?.trim()
                if (cmd != ";") break
            } else {
                first = false
            }
            printSolution(solutions.next())
        }
        printEndOfSolutions()
    }

    fun printNumSolutions(solutions: Iterator<Solution>, maxSolutions: Int) {
        var i = 0
        while (i < maxSolutions && solutions.hasNext()) {
            i++
            printSolution(solutions.next())
        }
        printEndOfSolutions()
    }
}