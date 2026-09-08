package it.unibo.tuprolog.examples.concurrent

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.examples.concurrent.ConcurrentSolverPerformance.nQueens
import it.unibo.tuprolog.examples.concurrent.ConcurrentSolverPerformance.queryNQueens
import it.unibo.tuprolog.examples.concurrent.ConcurrentSolverPerformance.runClassic
import it.unibo.tuprolog.examples.concurrent.ConcurrentSolverPerformance.runConcurrent
import it.unibo.tuprolog.examples.concurrent.ConcurrentSolverPerformance.theoryNQueens
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.concurrent.ConcurrentSolverFactory
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.ClausesReader

/**
 * Compares the wall-clock time taken to solve the N-Queens counting problem with
 * `it.unibo.tuprolog.solve.concurrent.ConcurrentSolverFactory` (backed by `it.unibo.tuprolog.solve.concurrent.ConcurrentSolver`,
 * which explores alternative choice points on separate threads/coroutines) against the same
 * query solved with `it.unibo.tuprolog.solve.classic.ClassicSolverFactory` (a single-threaded,
 * depth-first resolution engine).
 *
 * The theory is loaded from the bundled `nQueens.pl` resource, which counts (rather than
 * enumerates) all solutions to the N-Queens problem for a given board size via
 * `queenCountSolution/2`. This makes the example a useful, self-contained micro-benchmark for
 * illustrating the trade-offs of `:solve-concurrent` versus `:solve-classic`: concurrent
 * resolution can overlap the exploration of independent branches, but incurs coordination
 * overhead that may or may not pay off depending on the problem size and hardware.
 */
@Suppress("ktlint:standard:property-naming")
object ConcurrentSolverPerformance {
    /** The board size (and number of queens) used for the benchmark. */
    const val nQueens = 8

    /** The N-Queens theory, parsed from the `nQueens.pl` classpath resource. */
    val theoryNQueens = loadTheoryFromFile("nQueens.pl")

    /** The query counting all solutions to the N-Queens problem for [nQueens] queens. */
    val queryNQueens = Struct.of("queenCountSolution", Integer.of(nQueens), Var.of("Count"))

    private fun loadTheoryFromFile(fileName: String): Theory {
        val inputStream = ConcurrentSolverPerformance::class.java.getResourceAsStream(fileName)!!
        return ClausesReader.withDefaultOperators().readTheory(inputStream)
    }

    private fun computeTime(block: () -> Unit): Long {
        val start = currentTimeInstant()
        block()
        val end = currentTimeInstant()
        return end - start
    }

    /**
     * Solves [query] against [theory] using a concurrent solver built via
     * `it.unibo.tuprolog.solve.concurrent.ConcurrentSolverFactory.solverWithDefaultBuiltins`, and
     * prints the elapsed time in milliseconds.
     */
    fun runConcurrent(
        theory: Theory,
        query: Struct,
    ) {
        val solver = ConcurrentSolverFactory.solverWithDefaultBuiltins(staticKb = theory)
        val executionTime = computeTime { solver.solveOnce(query) }
        println("Concurrent Execution time: ${executionTime}ms")
    }

    /**
     * Solves [query] against [theory] using the classic, single-threaded solver built via
     * `it.unibo.tuprolog.solve.classic.ClassicSolverFactory.solverWithDefaultBuiltins`, and prints
     * the elapsed time in milliseconds.
     */
    fun runClassic(
        theory: Theory,
        query: Struct,
    ) {
        val solver = ClassicSolverFactory.solverWithDefaultBuiltins(staticKb = theory)
        val executionTime = computeTime { solver.solveOnce(query) }
        println("Classic Execution time: ${executionTime}ms")
    }
}

/**
 * Entry point running the N-Queens benchmark first with the concurrent solver, then with the
 * classic solver, printing the elapsed time of each run to standard output so the two engines
 * can be compared directly for the same theory and query.
 */
fun main() {
    println("Start of concurrent execution of nQueens with n = $nQueens")
    runConcurrent(theoryNQueens, queryNQueens)
    println("End of concurrent execution.")
    println("Start of classic execution of nQueens with n = $nQueens")
    runClassic(theoryNQueens, queryNQueens)
    println("End of classic execution.")
}
