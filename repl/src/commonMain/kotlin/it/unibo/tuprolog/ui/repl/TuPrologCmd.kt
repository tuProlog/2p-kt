package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi
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
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.libs.oop.OOPLib
import it.unibo.tuprolog.theory.Theory

class TuPrologCmd(vararg additionalLibraries: Library) : CliktCommand(
    invokeWithoutSubcommand = true,
    allowMultipleSubcommands = true,
    name = "java -jar 2p-repl.jar",
    help = "Start a Prolog Read-Eval-Print loop"
) {

    companion object {
        const val DEFAULT_TIMEOUT: Int = 1000 // 1 s
    }

    private val additionalLibraries: Array<out Library> = additionalLibraries

    private val files: List<String> by option("-T", "--theory", help = "Path of theory file to be loaded")
        .multiple()

    private val timeout by option(
        "-t",
        "--timeout",
        help = "Maximum amount of time for computing a solution (default: $DEFAULT_TIMEOUT ms)"
    ).int().default(DEFAULT_TIMEOUT)

    private val oop by option(
        "--oop",
        help = "Loads the OOP library"
    ).flag(default = false)

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
                    TermUi.echo("# Successfully loaded ${t.size} clauses from $file")
                    theory += t
                } catch (e: ParseException) {
                    TermUi.echo(
                        """
                        |# Error while parsing theory file: $file
                        |#     Message: ${e.message}
                        |#     Line   : ${e.line}
                        |#     Column : ${e.column}
                        |#     Clause : ${e.clauseIndex}
                        """.trimMargin(),
                        err = true
                    )
                }
            }
        }
        TermUi.echo("")
        return theory
    }

    private fun readEvalPrintLoop(solver: Solver) {
        var query: String? = TuPrologUtils.readQuery()
        while (query != null) {
            try {
                val goal = Struct.parse(query, solver.operators)
                val solutions = solver.solve(goal, this.getTimeout()).iterator()
                TuPrologUtils.printSolutions(solutions, solver.operators)
            } catch (e: ParseException) {
                TuPrologUtils.printParseException(e)
            }
            TermUi.echo("")
            query = TuPrologUtils.readQuery()
        }
    }

    fun getTimeout(): TimeDuration {
        return timeout.toLong()
    }

    fun getSolver(): Solver {
        TermUi.echo("# 2P-Kt version ${Info.VERSION}")
        val theory: Theory = this.loadTheory()
        val outputChannel = OutputChannel.of<Warning> { w ->
            TermUi.echo("# ${w.message}", err = true)
            val sep = "\n    at "
            val formatter = TermFormatter.Companion.prettyExpressions(w.context.operators)
            val stacktrace = w.logicStackTrace.joinToString(sep) { it.format(formatter) }
            TermUi.echo("#    at $stacktrace", err = true)
        }
        val libraries = if (oop) {
            Runtime.of(IOLib, OOPLib, *additionalLibraries)
        } else {
            Runtime.of(IOLib, *additionalLibraries)
        }
        return Solver.prolog.solverWithDefaultBuiltins(
            staticKb = theory,
            otherLibraries = libraries,
            warnings = outputChannel
        ).also {
            for ((_, library) in it.libraries) {
                TermUi.echo("# Successfully loaded library `${library.alias}`")
            }
            TermUi.echo("")
        }
    }
}
