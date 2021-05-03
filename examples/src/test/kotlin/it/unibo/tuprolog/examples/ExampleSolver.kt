package it.unibo.tuprolog.examples

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.classic.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.function.Compute
import it.unibo.tuprolog.solve.function.ExpressionReducer
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.libs.oop.OOPLib
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import org.gciatto.kt.math.BigInteger
import org.junit.Ignore
import org.junit.Test
import kotlin.collections.List as KtList

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

        val solutions: KtList<Solution> = prolog.solveList(goal)

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

    @Test
    fun mutableVsNonMutableSolvers() {
        val theory = Theory.of(
            Fact.of(Struct.of("f", Atom.of("a"))), // f(a).
            Fact.of(Struct.of("f", Atom.of("b"))), // f(b).
            Fact.of(Struct.of("f", Atom.of("c"))) // f(c).
        )

        val fact = Struct.of("g", Integer.ONE) // g(1).

        // solvers require information to be provided at instantiation time:
        val solver: Solver = Solver.classic.solverOf(
            libraries = Libraries.of(DefaultBuiltins),
            staticKb = theory
        )

        solver.solveOnce(Struct.of("assert", fact))
        println(solver.dynamicKb) // MutableIndexedTheory{ g(1) :- true }

        // mutable solvers can be lately configured:
        val mutableSolver: MutableSolver = Solver.classic.mutableSolverOf()

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

        val solver = Solver.classic.solverWithDefaultBuiltins(
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

        val solver = Solver.classic.mutableSolverWithDefaultBuiltins()
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

        val solver = Solver.classic.solverWithDefaultBuiltins(
            staticKb = theory
        )

        println(solver.staticKb) // IndexedTheory{ f(a) :- true. f(b) :- true. :- dynamic(g/1) }
        println(solver.dynamicKb) // MutableIndexedTheory{ g(1) :- true. g(2) :- true }
    }

    @Test
    fun usingLibraries() {
        val solver = Solver.classic.solverOf(
            libraries = Libraries.of(DefaultBuiltins, IOLib, OOPLib)
        )

        println(solver.libraries.keys) // [prolog.lang, prolog.io, prolog.oop]
    }

    @Test
    fun definingLibraries() {
        val myLibrary = object : AliasedLibrary by
        Library.aliased(
            alias = "alias.of.the.lib",
            operatorSet = OperatorSet(),
            theory = Theory.empty(),
            primitives = mapOf(
                Signature("f", 2) to { request: Solve.Request<ExecutionContext> ->
                    TODO("compute response sequence here")
                }
            ),
            functions = mapOf(
                Signature("+", 2) to { request: Compute.Request<ExecutionContext> ->
                    TODO("compute response here")
                }
            )
        ) {}

        println(myLibrary)
    }

    @Test
    fun customPrimitive() {
        val gt = Signature("gt", 2)

        fun greaterThan(req: Solve.Request<ExecutionContext>): Sequence<Solve.Response> {
            val arg1: Term = req.arguments[0] ; val arg2: Term = req.arguments[1]

            if (arg1 !is Numeric) {
                throw TypeError.forArgument(req.context, req.signature, TypeError.Expected.NUMBER, arg1, 0)
            }
            if (arg2 !is Numeric) {
                throw TypeError.forArgument(req.context, req.signature, TypeError.Expected.NUMBER, arg2, 1)
            }

            return if (arg1.castToNumeric().decimalValue > arg2.castToNumeric().decimalValue) {
                sequenceOf(req.replySuccess())
            } else {
                sequenceOf(req.replyFail())
            }
        }

        val mylib = Library.aliased(alias = "prolog.mylib", primitives = mapOf(gt to ::greaterThan))

        val solver = Solver.classic.solverOf(Libraries.of(mylib))

        println(solver.solveOnce(Struct.of("gt", Integer.ONE, Integer.ZERO))) // yes
        println(solver.solveOnce(Struct.of("gt", Integer.ZERO, Integer.ONE))) // no
        println(solver.solveOnce(Struct.of("gt", Integer.ONE, Atom.of("a")))) // type_error
    }

    @Test
    fun customBacktrackablePrimitive() {
        val nat = Signature("nat", 1)

        fun natural(req: Solve.Request<ExecutionContext>): Sequence<Solve.Response> {
            return when (val arg1: Term = req.arguments[0]) {
                is Numeric -> sequenceOf(if (arg1.intValue >= BigInteger.ZERO) req.replySuccess() else req.replyFail())
                is Var -> generateSequence(0) { it + 1 }.map { Integer.of(it) }.map { it mguWith arg1 }.map { req.replyWith(it) }
                else -> throw TypeError.forArgument(req.context, req.signature, TypeError.Expected.NUMBER, arg1, 0)
            }
        }

        val mylib = Library.aliased(alias = "prolog.mylib", primitives = mapOf(nat to ::natural))

        val solver = Solver.classic.solverOf(Libraries.of(mylib))

        println(solver.solveOnce(Struct.of("nat", Integer.ONE))) // yes
        println(solver.solveOnce(Struct.of("nat", Integer.MINUS_ONE))) // no
        println(solver.solveOnce(Struct.of("nat", Atom.of("a")))) // type_error
        println(solver.solve(Struct.of("nat", Var.of("X"))).take(3).toList()) // 0, 1, 2
    }

    @Test
    fun customSideEffectPrimitive() {
        val assert_all = Signature("assert_all", 1)

        fun assertAll(req: Solve.Request<ExecutionContext>): Sequence<Solve.Response> {
            return when (val arg1: Term = req.arguments[0]) {
                is List -> req.replySuccess {
                    addDynamicClauses(arg1.toList().filterIsInstance<Struct>().map { Rule.of(it) })
                }
                else -> throw TypeError.forArgument(req.context, req.signature, TypeError.Expected.LIST, arg1, 0)
            }.let { sequenceOf(it) }
        }

        val mylib = Library.aliased(alias = "prolog.mylib", primitives = mapOf(assert_all to ::assertAll))

        val solver = Solver.classic.solverOf(Libraries.of(mylib))

        val factsToAdd = List.of(Atom.of("a"), Atom.of("b"), Atom.of("c"))

        println(solver.solveOnce(Struct.of("assert_all", factsToAdd))) // yes
        println(solver.dynamicKb) // a. b. c.
    }

    @Test
    fun customFunction() {
        val next = Signature("next", 1)

        fun next(req: Compute.Request<ExecutionContext>): Compute.Response {
            return when (val arg1: Term = req.arguments[0]) {
                is Integer -> req.replyWith(Integer.of(arg1.intValue + BigInteger.ONE))
                else -> req.replyWith(arg1)
            }
        }

        val mylib = Library.aliased(alias = "prolog.mylib", functions = mapOf(next to ::next))

        val solver = Solver.classic.solverWithDefaultBuiltins(Libraries.of(mylib))

        val goal = Struct.of("is", Var.of("X"), Struct.of("next", Integer.ONE)) // X is next(1)
        println(solver.solveOnce(goal)) // X=2
    }

    @Test
    fun reducePrimitive() {
        val reduce = Signature("reduce", 2)

        fun reduce(req: Solve.Request<ExecutionContext>): Sequence<Solve.Response> {
            val arg1: Term = req.arguments[0]
            return when (val arg2: Term = req.arguments[1]) {
                is Var -> throw InstantiationError.forArgument(req.context, req.signature, arg2, 1)
                else -> {
                    val reducer = ExpressionReducer(req, 1)
                    req.replyWith(arg1 mguWith arg2.accept(reducer))
                }
            }.let { sequenceOf(it) }
        }

        val mylib = Library.aliased(alias = "prolog.mylib", primitives = mapOf(reduce to ::reduce))

        val solver = Solver.classic.solverWithDefaultBuiltins(Libraries.of(mylib))

        val expression = Struct.of("f", Struct.of("+", Integer.ONE, Integer.of(2))) // f(1 + 2)

        val goal = Struct.of("reduce", Var.of("X"), expression) // reduce(X, f(1 + 2))
        println(solver.solveOnce(goal)) // X=f(3)
    }

    @Test
    fun channels() {
        val messages = mutableListOf<String>()

        val solver = Solver.classic.solverWithDefaultBuiltins(
            otherLibraries = Libraries.of(IOLib),
            stdIn = InputChannel.of("hello"),
            stdOut = OutputChannel.of { messages += it }
        )

        val goal = Scope.empty {
            tupleOf(
                structOf("get_char", varOf("X")),
                structOf("write", varOf("X"))
            )
        } // ?- get_char(X), write(X).

        for (i in 0 until "hello".length) {
            println(solver.solveOnce(goal)) // X=h, H=e, H=l, ...
        }

        println(messages) // [h, e, l, l, o]
    }

    @Test
    fun unknown() {
        val solver1 = Solver.classic.solverOf(
            flags = FlagStore.DEFAULT,
            warnings = OutputChannel.warn() // default
        )

        solver1.solveOnce(Atom.of("missing")) // no (prints warning)
        // No such a predicate: missing/0

        val solver2 = Solver.classic.solverOf(
            flags = FlagStore.DEFAULT.set(Unknown, Unknown.FAIL),
            warnings = OutputChannel.warn() // default
        )

        solver2.solveOnce(Atom.of("missing")) // no (prints noting)

        val solver3 = Solver.classic.solverOf(
            flags = FlagStore.DEFAULT.set(Unknown, Unknown.ERROR),
            warnings = OutputChannel.warn() // default
        )

        solver3.solveOnce(Atom.of("missing")) // halt
        // existence_error(procedure, missing/0)
    }
}
