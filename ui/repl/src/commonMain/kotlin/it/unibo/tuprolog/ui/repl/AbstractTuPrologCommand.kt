package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.prompt
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolutionFormatter
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.timing.TIME_TAG
import it.unibo.tuprolog.solve.timing.TimedSolutions
import it.unibo.tuprolog.solve.timing.formatTime
import it.unibo.tuprolog.solve.timing.time
import it.unibo.tuprolog.utils.time.formatLocalTimestamp
import kotlin.time.Clock

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
        number: Int,
        operatorSet: OperatorSet,
        groundQueriesHaveBooleanSolution: Boolean,
    ) {
        val exception = (sol as? Solution.Halt)?.exception
        if (exception is HaltException) {
            echo("# goodbye.")
            throw ProgramResult(exception.exitStatus)
        }
        val plain =
            sol
                .replaceTags(sol.tags - TIME_TAG)
                .format(SolutionFormatter.withOperators(operatorSet, groundQueriesHaveBooleanSolution))
        val keyword = plain.takeWhile { it.isLetter() }
        val prefix = "$number. ${sol.time?.let { "[${it.formatTime()}] " }.orEmpty()}"
        terminal.println(prefix + TextStyles.bold(keyword) + plain.drop(keyword.length))
    }

    /** Prints a timestamped query header for non-interactive solving. */
    protected fun printQuery(query: String) {
        terminal.println(
            "[${formatLocalTimestamp(Clock.System.now().toEpochMilliseconds())}] " +
                TextStyles.bold("?-") + " ${query.trimEnd().removeSuffix(".")}.",
        )
    }

    /**
     * Prints [e]'s message to the standard error stream, prefixed with `# ` and with its first character
     * capitalized, in place of a raw stack trace -- so a malformed query or theory file reports a short,
     * user-facing diagnostic rather than crashing the REPL.
     */
    protected fun printParseException(e: ParseException) {
        echo("# ${e.message?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}", err = true)
    }

    private fun printEndOfSolutions(solutions: TimedSolutions) {
        echo("% resolution time: ${solutions.timing.active.formatTime()}")
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
        val query: String? =
            terminal.prompt(
                "[${formatLocalTimestamp(Clock.System.now().toEpochMilliseconds())}] ${TextStyles.bold("?-")}",
                promptSuffix = " ",
            )
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

    /**
     * Prints solutions from [solutions] one at a time, formatting each with [operatorSet]'s operators, in the
     * conventional Prolog top-level style: after the first solution, the user is prompted (with an empty prompt)
     * to type `;` to request the next solution, or anything else to stop. Each solution shows the time spent
     * finding it, and the whole resolution's time is printed at the end.
     *
     * Used by both [TuPrologCmd]'s interactive REPL loop and [TuPrologSolveQuery]'s `solve` subcommand when no
     * `-n`/`--numberOfSolutions` limit was given.
     */
    protected fun printSolutions(
        timedSolutions: TimedSolutions,
        operatorSet: OperatorSet,
        groundQueriesHaveBooleanSolution: Boolean = false,
    ) {
        val solutions = timedSolutions.iterator()
        var number = 0
        while (solutions.hasNext()) {
            if (number > 0) {
                val cmd = terminal.prompt("", hideInput = false)?.trim()
                if (cmd != ";") break
            }
            printSolution(solutions.next(), ++number, operatorSet, groundQueriesHaveBooleanSolution)
        }
        printEndOfSolutions(timedSolutions)
    }

    /**
     * Prints up to [maxSolutions] solutions from [solutions], formatting each with [operatorSet]'s operators,
     * without prompting between them -- unlike [printSolutions], it stops automatically once the limit is
     * reached (or [solutions] is exhausted, whichever comes first).
     *
     * Used by [TuPrologSolveQuery]'s `solve` subcommand when a positive `-n`/`--numberOfSolutions` was given.
     */
    protected fun printNumSolutions(
        timedSolutions: TimedSolutions,
        maxSolutions: Int,
        operatorSet: OperatorSet,
        groundQueriesHaveBooleanSolution: Boolean = false,
    ) {
        val solutions = timedSolutions.iterator()
        var i = 0
        while (i < maxSolutions && solutions.hasNext()) {
            i++
            printSolution(solutions.next(), i, operatorSet, groundQueriesHaveBooleanSolution)
        }
        printEndOfSolutions(timedSolutions)
    }
}
