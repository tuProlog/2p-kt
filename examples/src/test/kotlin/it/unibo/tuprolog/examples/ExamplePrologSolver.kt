package it.unibo.tuprolog.examples

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.classic.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.function.Compute
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.libs.oop.OOPLib
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Ignore
import kotlin.test.Test

class ExamplePrologSolver {
    @Test
    fun exampleYesSolutionList() {
        val prolog = Solver.prolog.solverWithDefaultBuiltins(
            staticKb = Theory.of(
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
        val prolog = Solver.prolog.solverWithDefaultBuiltins(
            staticKb = Theory.of(
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
        val prolog = Solver.prolog.solverWithDefaultBuiltins(
            staticKb = Theory.of(
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
        val prolog = Solver.prolog.solverOf()

        val goal = Struct.of("f", Var.of("X"))

        val solution = prolog.solveOnce(goal)

        println(solution) // No(query=f(X_0))
    }

    @Test
    fun exampleHaltSolutions() {
        val prolog = Solver.prolog.solverWithDefaultBuiltins()

        val solution = prolog.solveOnce(Struct.of("halt", Integer.of(2)))

        println(solution) // Halt(query=halt(2), exception=it.unibo.tuprolog.solve.exception.HaltException)
        println((solution.exception as HaltException).exitStatus) // 2
    }

    @Test
    fun manySolutionsLazy() {
        val prolog = Solver.prolog.solverOf(
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
        val prolog = Solver.prolog.solverOf(
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

    @Test
    fun mutableVsNonMutableSolvers() {
        val theory = Theory.of(
            Fact.of(Struct.of("f", Atom.of("a"))), // f(a).
            Fact.of(Struct.of("f", Atom.of("b"))), // f(b).
            Fact.of(Struct.of("f", Atom.of("c"))) // f(c).
        )

        val fact = Struct.of("g", Integer.ONE) // g(1).

        // solvers require information to be provided at instantiation time:
        val solver: Solver = Solver.prolog.solverOf(
            libraries = Runtime.of(DefaultBuiltins),
            staticKb = theory
        )

        solver.solveOnce(Struct.of("assert", fact))
        println(solver.dynamicKb) // MutableIndexedTheory{ g(1) :- true }

        // mutable solvers can be lately configured:
        val mutableSolver: MutableSolver = Solver.prolog.mutableSolverOf()

        mutableSolver.loadLibrary(DefaultBuiltins)
        mutableSolver.loadStaticKb(theory)

        mutableSolver.assertZ(fact)
        println(solver.dynamicKb) // MutableIndexedTheory{ g(1) :- true }
    }

    @Test
    fun customTheoriesSolver() {
        val theory1 = Theory.of(
            Fact.of(Struct.of("f", Atom.of("a"))), // f(a).
            Fact.of(Struct.of("f", Atom.of("b"))), // f(b).
        )
        val theory2 = Theory.of(
            Fact.of(Struct.of("g", Integer.of(1))), // g(1).
            Fact.of(Struct.of("g", Integer.of(2))), // g(2).
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            staticKb = theory1,
            dynamicKb = theory2
        )

        println(solver.staticKb) // IndexedTheory{ f(a) :- true. f(b) :- true }
        println(solver.dynamicKb) // MutableIndexedTheory{ g(1) :- true. g(2) :- true }
    }

    @Test
    fun customTheoriesMutableSolver() {
        val theory1 = Theory.of(
            Fact.of(Struct.of("f", Atom.of("a"))), // f(a).
            Fact.of(Struct.of("f", Atom.of("b"))), // f(b).
        )
        val theory2 = Theory.of(
            Fact.of(Struct.of("g", Integer.of(1))), // g(1).
            Fact.of(Struct.of("g", Integer.of(2))), // g(2).
        )

        val solver = Solver.prolog.mutableSolverWithDefaultBuiltins()
        println(solver.staticKb) // IndexedTheory{  }
        println(solver.dynamicKb) // MutableIndexedTheory{  }

        solver.loadStaticKb(theory1)
        solver.loadDynamicKb(theory2)
        println(solver.staticKb) // IndexedTheory{ f(a) :- true. f(b) :- true }
        println(solver.dynamicKb) // MutableIndexedTheory{ g(1) :- true. g(2) :- true }
    }

    @Test
    fun directives() {
        val theory = Theory.of(
            Fact.of(Struct.of("f", Atom.of("a"))), // f(a).
            Fact.of(Struct.of("f", Atom.of("b"))), // f(b).
            Directive.of(Struct.of("dynamic", Indicator.of("g", 1))), // :- dynamic(g/1).
            Fact.of(Struct.of("g", Integer.of(1))), // g(1).
            Fact.of(Struct.of("g", Integer.of(2))), // g(2).
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            staticKb = theory
        )

        println(solver.staticKb) // IndexedTheory{ f(a) :- true. f(b) :- true. :- dynamic(g/1) }
        println(solver.dynamicKb) // MutableIndexedTheory{ g(1) :- true. g(2) :- true }
    }

    @Test
    fun usingLibraries() {
        val solver = Solver.prolog.solverOf(
            libraries = Runtime.of(DefaultBuiltins, IOLib, OOPLib)
        )

        println(solver.libraries.keys) // [prolog.lang, prolog.io, prolog.oop]
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    @Test
    fun definingLibraries() {
        val myLibrary = object : Library by
        Library.of(
            alias = "alias.of.the.lib",
            primitives = mapOf(
                Signature("f", 2) to Primitive { request: Solve.Request<ExecutionContext> ->
                    TODO("compute response sequence here")
                }
            ),
            clauses = emptyList(),
            operators = OperatorSet(),
            functions = mapOf(
                Signature("+", 2) to LogicFunction { request: Compute.Request<ExecutionContext> ->
                    TODO("compute response here")
                }
            )
        ) {}

        println(myLibrary)
    }
}
