package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import it.unibo.tuprolog.Info
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.libs.oop.OOPLib
import it.unibo.tuprolog.theory.Theory

/**
 * Root [clikt](https://ajalt.github.io/clikt/) command of the 2P-Kt REPL: `java -jar 2p-repl.jar`.
 *
 * Run with no subcommand, it starts an interactive Prolog read-eval-print loop: it builds a
 * [it.unibo.tuprolog.solve.Solver] (see [getSolver]), repeatedly prompts the user for a dot-terminated query
 * with `?-` (see [AbstractTuPrologCommand.readQuery]), parses it with
 * [it.unibo.tuprolog.core.Struct.Companion.parse], solves it, and prints the resulting solutions -- until the
 * standard input stream is closed, at which point it prints a farewell message and exits.
 *
 * Supported options (see the corresponding property for details): `-T`/`--theory` (repeatable, one or more
 * theory files to load into the static KB), `-t`/`--timeout` (per-query solving timeout, in milliseconds),
 * and `--oop` (also load `:oop-lib`'s [it.unibo.tuprolog.solve.libs.oop.OOPLib]). The `solve` subcommand
 * ([TuPrologSolveQuery]) reuses the same solver to evaluate a single query non-interactively instead.
 *
 * The solver is always built with `:io-lib`'s [it.unibo.tuprolog.solve.libs.io.IOLib] loaded (for `write/1`,
 * `nl/0`, file inclusion, and the like -- see `IOLib`'s own KDoc for the full predicate list), on top of
 * [it.unibo.tuprolog.solve.classic.ClassicSolverFactory]'s ISO-standard resolution engine
 * (via [it.unibo.tuprolog.solve.Solver.Companion.prolog]).
 *
 * @param additionalLibraries extra [Library] instances to load into the solver alongside `IOLib` (and `OOPLib`
 *   when `--oop` is given), e.g. for an embedder that wants to expose custom predicates through this same CLI.
 */
class TuPrologCmd(
    vararg additionalLibraries: Library,
) : AbstractTuPrologCommand(
        invokeWithoutSubcommand = true,
        allowMultipleSubcommands = true,
        name = "java -jar 2p-repl.jar",
        help = "Start a Prolog Read-Eval-Print loop",
    ) {
    companion object {
        /** Default value, in milliseconds, of the `-t`/`--timeout` option: 1 second. */
        const val DEFAULT_TIMEOUT: Int = 1000 // 1 s
    }

    private val additionalLibraries: Array<out Library> = additionalLibraries

    private val files: List<String> by option("-T", "--theory", help = "Path of theory file to be loaded")
        .multiple()

    private val timeout by option(
        "-t",
        "--timeout",
        help = "Maximum amount of time for computing a solution (default: $DEFAULT_TIMEOUT ms)",
    ).int().default(DEFAULT_TIMEOUT)

    private val oop by option(
        "--oop",
        help = "Loads the OOP library",
    ).flag(default = false)

    /**
     * Builds a solver (see [getSolver]) and, if no subcommand was invoked on the command line, starts the
     * interactive read-eval-print loop with it. When a subcommand (e.g. `solve`) was invoked instead, clikt
     * dispatches to that subcommand's own `run` after this method returns, reusing the solver built here
     * through [getSolver]/[getTimeout].
     */
    override fun run() {
        val solve: Solver = getSolver()
        val subcommand = this.currentContext.invokedSubcommand

        if (subcommand == null) {
            readEvalPrintLoop(solve)
        }
        // nota: se subcommand è diverso da null, il controllo fluisce automaticamente al metodo run di subcommand
    }

    private fun loadTheory(): Theory {
        var theory: Theory = Theory.empty()
        for (file in this.files) {
            if (isReadableFile(file)) {
                try {
                    val t = loadTheoryFromFile(file)
                    echo("# Successfully loaded ${t.size} clauses from $file")
                    theory += t
                } catch (e: ParseException) {
                    echo(
                        """
                        |# Error while parsing theory file: $file
                        |#     Message: ${e.message}
                        |#     Line   : ${e.line}
                        |#     Column : ${e.column}
                        |#     Clause : ${e.clauseIndex}
                        """.trimMargin(),
                        err = true,
                    )
                }
            }
        }
        echo("")
        return theory
    }

    private fun readEvalPrintLoop(solver: Solver) {
        try {
            var query: String? = readQuery()
            while (query != null) {
                try {
                    val goal = Struct.parse(query, solver.operators)
                    val solutions = solver.solve(goal, this.getTimeout()).iterator()
                    printSolutions(solutions, solver.operators)
                } catch (e: ParseException) {
                    printParseException(e)
                }
                echo("")
                query = readQuery()
            }
        } catch (_: NullInputException) {
            echo("\n# goodbye.")
            throw ProgramResult(0)
        }
    }

    /**
     * The `-t`/`--timeout` option's value (milliseconds, defaulting to [DEFAULT_TIMEOUT]), converted to the
     * [TimeDuration] expected by [it.unibo.tuprolog.solve.Solver.solve]. Exposed so [TuPrologSolveQuery] can
     * reuse the same timeout its parent command was configured with.
     */
    fun getTimeout(): TimeDuration = timeout.toLong()

    /**
     * Builds a fresh [Solver] according to this command's options: it loads and merges every theory file
     * given via `-T`/`--theory` (skipping any that [isReadableFile] deems unreadable, and reporting a
     * [it.unibo.tuprolog.core.parsing.ParseException] for any that fails to parse) into the static knowledge
     * base, always loads `:io-lib`'s [it.unibo.tuprolog.solve.libs.io.IOLib] plus this command's
     * [additionalLibraries], additionally loads `:oop-lib`'s [it.unibo.tuprolog.solve.libs.oop.OOPLib] when
     * `--oop` was given, enables the [it.unibo.tuprolog.solve.flags.TrackVariables] flag, and routes solve
     * warnings to the terminal (with their logic stack trace). As a side effect, it echoes the 2P-Kt version
     * and the alias of each loaded library to standard output.
     *
     * Exposed so [TuPrologSolveQuery] can obtain the same, fully-configured solver its parent command builds.
     */
    fun getSolver(): Solver {
        echo("# 2P-Kt version ${Info.VERSION}")
        val theory: Theory = this.loadTheory()
        val outputChannel =
            OutputChannel.of<Warning> { w ->
                echo("# ${w.message}", err = true)
                val sep = "\n    at "
                val formatter = TermFormatter.Companion.prettyExpressions(w.context.operators)
                val stacktrace = w.logicStackTrace.joinToString(sep) { it.format(formatter) }
                echo("#    at $stacktrace", err = true)
            }
        val libraries =
            if (oop) {
                Runtime.of(IOLib, OOPLib, *additionalLibraries)
            } else {
                Runtime.of(IOLib, *additionalLibraries)
            }
        return Solver.prolog
            .newBuilder()
            .runtime(libraries)
            .staticKb(theory)
            .flag(TrackVariables) { ON }
            .warnings(outputChannel)
            .build()
            .also {
                for ((_, library) in it.libraries) {
                    echo("# Successfully loaded library `${library.alias}`")
                }
                echo("")
            }
    }
}
