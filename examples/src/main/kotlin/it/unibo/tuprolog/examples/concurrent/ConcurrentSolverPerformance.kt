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

object ConcurrentSolverPerformance {

    const val nQueens = 8
    val theoryNQueens = loadTheoryFromFile("nQueens.pl")
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

    fun runConcurrent(theory: Theory, query: Struct) {
        val solver = ConcurrentSolverFactory.solverWithDefaultBuiltins(staticKb = theory)
        val executionTime = computeTime { solver.solveOnce(query) }
        println("Concurrent Execution time: ${executionTime}ms")
    }

    fun runClassic(theory: Theory, query: Struct) {
        val solver = ClassicSolverFactory.solverWithDefaultBuiltins(staticKb = theory)
        val executionTime = computeTime { solver.solveOnce(query) }
        println("Classic Execution time: ${executionTime}ms")
    }
}

fun main() {
    println("Start of concurrent execution of nQueens with n = $nQueens")
    runConcurrent(theoryNQueens, queryNQueens)
    println("End of concurrent execution.")
    println("Start of classic execution of nQueens with n = $nQueens")
    runClassic(theoryNQueens, queryNQueens)
    println("End of classic execution.")
}
