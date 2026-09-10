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

/**
 * Base class for the [clikt](https://ajalt.github.io/clikt/) commands that make up the 2P-Kt REPL CLI
 * ([TuPrologCmd] and its `solve` subcommand [TuPrologSolveQuery]): it factors out everything both commands
 * need to interact with a user at a terminal -- reading (possibly multi-line, dot-terminated) Prolog queries,
 * and printing the [it.unibo.tuprolog.solve.Solution]s a [it.unibo.tuprolog.solve.Solver] produces for them --
 * so that subclasses only have to configure clikt options/arguments and implement [run].
 *
 * All constructor parameters simply forward to the corresponding [com.github.ajalt.clikt.core.CliktCommand]
 * configuration points; `help` and `epilog` are exposed instead through [help] and [helpEpilog] so they can be
 * set once from the constructor rather than overridden per-subclass.
 *
 * @param help the one-line help text shown for this command, e.g. in `--help` output or a parent command's
 *   subcommand listing.
 * @param epilog extra text appended after the option/argument listing in `--help` output.
 */
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

    /**
     * Prints [e]'s message to the standard error stream, prefixed with `# ` and with its first character
     * capitalized, in place of a raw stack trace -- so a malformed query or theory file reports a short,
     * user-facing diagnostic rather than crashing the REPL.
     */
    protected fun printParseException(e: ParseException) {
        echo("# ${e.message?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}", err = true)
    }

    private fun printEndOfSolutions() {
        // do nothing
    }

    /**
     * Prompts the user with `?-` and reads one Prolog query from the terminal, transparently continuing onto
     * further `>`-prompted lines (via [readQueryMultiline]) until the accumulated input ends with a `.`, since
     * a query may legitimately span multiple lines before its terminating full stop is typed.
     *
     * @return the raw, dot-terminated query text as typed by the user (not yet parsed).
     * @throws NullInputException if the terminal returns `null`, i.e. the standard input stream has been closed.
     */
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

    /**
     * Prints solutions from [solutions] one at a time, formatting each with [operatorSet]'s operators, in the
     * classic Prolog top-level style: after the first solution, the user is prompted (with an empty prompt)
     * to type `;` to request the next solution, or anything else to stop.
     *
     * Used by both [TuPrologCmd]'s interactive REPL loop and [TuPrologSolveQuery]'s `solve` subcommand when no
     * `-n`/`--numberOfSolutions` limit was given.
     */
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

    /**
     * Prints up to [maxSolutions] solutions from [solutions], formatting each with [operatorSet]'s operators,
     * without prompting between them -- unlike [printSolutions], it stops automatically once the limit is
     * reached (or [solutions] is exhausted, whichever comes first).
     *
     * Used by [TuPrologSolveQuery]'s `solve` subcommand when a positive `-n`/`--numberOfSolutions` was given.
     */
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
