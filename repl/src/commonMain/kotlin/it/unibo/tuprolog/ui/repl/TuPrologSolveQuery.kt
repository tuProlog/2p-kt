package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration

class TuPrologSolveQuery : CliktCommand(help = "Compute a particular query and then terminate", name = "solve") {

    private val query: String by argument()
    private val maxSolutions: Int by option("-n", "--numberOfSolutions", help = "Number of solution to calculate").int()
        .default(0)

    private val parentCommand: TuPrologCmd
        get() = currentContext.parent?.command as TuPrologCmd


    override fun run() {
        val solver = parentCommand.getSolver()
        evalAndPrint(solver)
    }

    private fun evalAndPrint(solver: Solver) {
        if (query.isNotEmpty()) {
            try {
                val duration: TimeDuration = parentCommand.getTimeout()
                val solutions = solver.solve(Struct.parse(query), duration).iterator()
                if (maxSolutions == 0) {
                    TuPrologUtils.printSolutions(solutions)
                } else {
                    TuPrologUtils.printNumSolutions(solutions, maxSolutions)
                }
            } catch (e: ParseException) {
                TuPrologUtils.printParseException(e)
            }
        } else {
            TODO("throw adequate exception")
        }
    }
}