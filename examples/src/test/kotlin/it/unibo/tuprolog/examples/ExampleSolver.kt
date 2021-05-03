package it.unibo.tuprolog.examples

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.theory.Theory
import org.junit.Ignore
import org.junit.Test

class ExampleSolver {
    @Test
    fun exampleYesSolutionList() {
        val prolog = Solver.classic.solverWithDefaultBuiltins(
            staticKb = Theory.indexedOf(
                Fact.of(Struct.of("f", Atom.of("a"))),
                Fact.of(Struct.of("f", Atom.of("b"))),
                Fact.of(Struct.of("f", Atom.of("c")))
            )
        )

        val goal = Struct.of("f", Var.of("X"))

        val solutions: List<Solution> = prolog.solveList(goal)

        println(solutions.size) // 3
        println(solutions)
        // [Yes(query=f(X_2), substitution={X_2=a}), Yes(query=f(X_2), substitution={X_2=b}) Yes(query=f(X_2), substitution={X_2=c})]

        for (solution in solutions) {
            println(solution.query) // f(X_2), f(X_2), f(X_2)
            println(solution.isYes) // true, true, true
            println(solution.substitution) // {X_2=a}, {X_2=b}, {X_2=c}
        }
    }

    @Test
    fun exampleYesSolutions() {
        val prolog = Solver.classic.solverWithDefaultBuiltins(
            staticKb = Theory.indexedOf(
                Fact.of(Struct.of("f", Atom.of("a"))),
                Fact.of(Struct.of("f", Atom.of("b"))),
                Fact.of(Struct.of("f", Atom.of("c")))
            )
        )

        val goal = Struct.of("f", Var.of("X"))

        val solutions: Sequence<Solution> = prolog.solve(goal)

        println(solutions)
        // kotlin.sequences.ConstrainedOnceSequence@7f362f22

        val i = solutions.iterator()
        while (i.hasNext()) {
            val solution = i.next()
            println(solution.query) // f(X_2), f(X_2), f(X_2)
            println(solution.isYes) // true, true, true
            println(solution.substitution) // {X_2=a}, {X_2=b}, {X_2=c}
        }
    }

    @Test
    fun exampleTimeoutSolutions() {
        val prolog = Solver.classic.solverWithDefaultBuiltins(
            staticKb = Theory.indexedOf(
                Fact.of(Struct.of("f", Atom.of("a"))),
                Fact.of(Struct.of("f", Atom.of("b"))),
                Fact.of(Struct.of("f", Atom.of("c")))
            )
        )

        val goal = Struct.of("f", Var.of("X"))

        val solutions = prolog.solve(goal, SolveOptions.allLazilyWithTimeout(1 /* ms */))

        for (solution in solutions) {
            println(solution.query) // f(X_2)
            println(solution.isHalt) // true
            println(solution.exception) // it.unibo.tuprolog.solve.exception.TimeOutException
        }
    }

    @Test
    fun exampleNoSolutions() {
        val prolog = Solver.classic.solverOf()

        val goal = Struct.of("f", Var.of("X"))

        val solution = prolog.solveOnce(goal)

        println(solution) // No(query=f(X_0))
    }

    @Test
    fun exampleHaltSolutions() {
        val prolog = Solver.classic.solverWithDefaultBuiltins()

        val solution = prolog.solveOnce(Struct.of("halt", Integer.of(2)))

        println(solution) // Halt(query=halt(2), exception=it.unibo.tuprolog.solve.exception.HaltException)
        println((solution.exception as HaltException).exitStatus) // 2
    }

    @Test
    fun manySolutionsLazy() {
        val prolog = Solver.classic.solverOf(
            staticKb = Theory.of(
                { factOf(structOf("nat", atomOf("z"))) },
                {
                    ruleOf(
                        structOf("nat", structOf("s", varOf("Z"))),
                        structOf("nat", varOf("Z"))
                    )
                }
            )
        )

        val goal = Struct.of("nat", Var.of("X"))
        val solutions = prolog.solve(goal, SolveOptions.someLazily(limit = 1000))

        println(solutions)
    }

    @Test
    @Ignore
    fun manySolutionsEager() {
        val prolog = Solver.classic.solverOf(
            staticKb = Theory.of(
                { factOf(structOf("nat", atomOf("z"))) },
                {
                    ruleOf(
                        structOf("nat", structOf("s", varOf("Z"))),
                        structOf("nat", varOf("Z"))
                    )
                }
            )
        )

        val goal = Struct.of("nat", Var.of("X"))
        val solutions = prolog.solve(goal, SolveOptions.someEagerly(limit = 1000))
        // AFTER A LONG WHILE
        println(solutions)
    }
}
