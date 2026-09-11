package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration

/**
 * The `solve` subcommand of [TuPrologCmd]: `java -jar 2p-repl.jar solve QUERY [-n N]`.
 *
 * Rather than starting an interactive loop, it parses and solves a single, non-interactive [query] argument
 * with the solver [TuPrologCmd.getSolver] builds (inheriting the parent command's `-T`/`--theory`,
 * `-t`/`--timeout` and `--oop` options), prints the resulting solution(s), and then terminates -- suited to
 * scripting or one-off invocations where a full REPL session is unnecessary.
 *
 * By default all solutions are printed interactively, one at a time, prompting for `;` to continue (see
 * [AbstractTuPrologCommand.printSolutions]); passing `-n`/`--numberOfSolutions` with a positive value instead
 * prints up to that many solutions without prompting (see [AbstractTuPrologCommand.printNumSolutions]).
 */
class TuPrologSolveQuery :
    AbstractTuPrologCommand(
        help = "Compute a particular query and then terminate",
        name = "solve",
    ) {
    private val query: String by argument()
    private val maxSolutions: Int by option("-n", "--numberOfSolutions", help = "Number of solution to calculate")
        .int()
        .default(0)

    private val parentCommand: TuPrologCmd
        get() = currentContext.parent?.command as TuPrologCmd

    /**
     * Retrieves the parent [TuPrologCmd]'s solver (see [TuPrologCmd.getSolver]) and uses it to solve and print
     * this subcommand's [query].
     */
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
                    printSolutions(solutions, solver.operators)
                } else {
                    printNumSolutions(solutions, maxSolutions, solver.operators)
                }
            } catch (e: ParseException) {
                printParseException(e)
            }
        } else {
            TODO("throw adequate exception")
        }
    }
}
