package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.terminal.prompt
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolutionFormatter
import it.unibo.tuprolog.solve.exception.HaltException

abstract class AbstractTuPrologCommand(
    private val help: String = "",
    private val epilog: String = "",
    name: String? = null,
    override val invokeWithoutSubcommand: Boolean = false,
    override val printHelpOnEmptyArgs: Boolean = false,
    override val helpTags: Map<String, String> = emptyMap(),
    override val autoCompleteEnvvar: String? = "",
    override val allowMultipleSubcommands: Boolean = false,
    override val treatUnknownOptionsAsArgs: Boolean = false,
    override val hiddenFromHelp: Boolean = false,
) : CliktCommand(name) {
    override fun help(context: Context) = this.help

    override fun helpEpilog(context: Context) = this.epilog

    private fun printSolution(
        sol: Solution,
        operatorSet: OperatorSet,
    ) {
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

    private fun printYesSolution(
        sol: Solution.Yes,
        operatorSet: OperatorSet,
    ) {
        echo(sol.format(SolutionFormatter.withOperators(operatorSet)))
    }

    private fun printHaltSolution(
        sol: Solution.Halt,
        operatorSet: OperatorSet,
    ) {
        when (val ex = sol.exception) {
            is HaltException -> {
                echo("# goodbye.")
                throw ProgramResult(ex.exitStatus)
            }
            else -> echo(sol.format(SolutionFormatter.withOperators(operatorSet)))
        }
    }

    protected fun printParseException(e: ParseException) {
        echo("# ${e.message?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}", err = true)
    }

    private fun printEndOfSolutions() {
        // do nothing
    }

    protected fun readQuery(): String {
        val query: String? = terminal.prompt("?-", promptSuffix = " ")
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
            lastRead = terminal.prompt(">", promptSuffix = " ")
                ?: throw NullInputException("The standard input has been closed unexpectedly")
            longQuery.append(lastRead)
        }
        return longQuery.toString()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun printNoSolution(
        sol: Solution.No,
        operatorSet: OperatorSet,
    ) {
        echo(sol.format(SolutionFormatter.withOperators(operatorSet)))
    }

    protected fun printSolutions(
        solutions: Iterator<Solution>,
        operatorSet: OperatorSet,
    ) {
        var first = true
        while (solutions.hasNext()) {
            if (!first) {
                val cmd = terminal.prompt("", hideInput = false)?.trim()
                if (cmd != ";") break
            } else {
                first = false
            }
            printSolution(solutions.next(), operatorSet)
        }
        printEndOfSolutions()
    }

    protected fun printNumSolutions(
        solutions: Iterator<Solution>,
        maxSolutions: Int,
        operatorSet: OperatorSet,
    ) {
        var i = 0
        while (i < maxSolutions && solutions.hasNext()) {
            i++
            printSolution(solutions.next(), operatorSet)
        }
        printEndOfSolutions()
    }
}
