package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.classicWithDefaultBuiltins
import it.unibo.tuprolog.theory.Theory

class TuPrologCmd : CliktCommand(invokeWithoutSubcommand = true, allowMultipleSubcommands = true) {

    companion object {
        const val DEFAULT_TIMEOUT: Int = 10000 // 10 s
    }

    private val files: List<String> by option("-f", "--file", help = "The file name").multiple()
    private val timeout by option("-t", "--timeout", help = "duration of the solution").int().default(DEFAULT_TIMEOUT)

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
                theory += loadTheoryFromFile(file)
            }
        }
        return theory
    }

    private fun readEvalPrintLoop(solver: Solver) {
        var query: String? = TuPrologUtils.readQuery()
        while (query != null) {
            try {
                val solutions = solver.solve(Struct.parse(query), this.getTimeout()).iterator()
                TuPrologUtils.printSolutions(solutions)
            } catch (e: ParseException) {
                TuPrologUtils.printParseException(e)
            }
            TermUi.echo("")
            query = TuPrologUtils.readQuery()
        }
    }

    fun getTimeout(): TimeDuration {
        val duration: TimeDuration = timeout.toLong()
        return duration
    }

    fun getSolver(): Solver {
        val theory: Theory = this.loadTheory()
        val solve: Solver = Solver.classicWithDefaultBuiltins(staticKb = theory)
        return solve
    }
}


