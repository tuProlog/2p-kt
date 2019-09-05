package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.primitiveimpl.Conjunction
import it.unibo.tuprolog.solve.primitiveimpl.Cut
import it.unibo.tuprolog.solve.testutils.DummyInstances
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import it.unibo.tuprolog.core.List as LogicList
import kotlin.collections.listOf as ktListOf

/**
 * Test class for [SolverSLD]
 *
 * @author Enrico
 */
internal class SolverSLDTest {

    @Test
    fun firstInformalTest() {
        val goal = Solve.Request(
                Signature("p", 1),
                ktListOf(Var.of("X")),
                ExecutionContext(
                        Libraries(Library.of(alias = "test", theory = ClauseDatabase.of(
                                Fact.of(Struct.of("p", Atom.of("a"))),
                                Fact.of(Struct.of("p", Atom.of("b"))),
                                Rule.of(Struct.of("p", Atom.of("b")), Truth.fail()),
                                Fact.of(Struct.of("p", Var.of("X"))),
                                Rule.of(Atom.of("p"), Atom.of("a")),
                                Rule.of(Atom.of("p"), Atom.of("c")),
                                Rule.of(Atom.of("p"), Truth.fail())
                        ))),
                        emptyMap(),
                        ClauseDatabase.of(),
                        ClauseDatabase.of()
                )
        )

        Solver.sld().solve(goal).toList().forEach { println("$it\n") }
    }

    @Test
    fun prologStandardExample() {
        val database = ClauseDatabase.of(
                Scope.empty().run {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("q", varOf("X")),
                            structOf("r", varOf("X"), varOf("Y"))
                    )
                },
                Scope.empty().run {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("s", varOf("X")))
                },
                Scope.empty().run { factOf(structOf("s", atomOf("d"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("a"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("b"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("c"))) },
                Scope.empty().run { factOf(structOf("r", atomOf("b"), atomOf("b1"))) },
                Scope.empty().run { factOf(structOf("r", atomOf("c"), atomOf("c1"))) }
        )

        val goal = Solve.Request(
                Signature("p", 2),
                ktListOf(Var.of("U"), Var.of("V")),
                DummyInstances.executionContext.copy(
                        libraries = Libraries(Library.of(
                                alias = "testLib",
                                theory = database,
                                primitives = mapOf(Conjunction.descriptionPair)
                        ))
                )
        )

        Solver.sld().solve(goal).toList().forEach { println("$it\n") }
    }

    @Test
    fun prologStandardExampleWithCut() {
        val database = ClauseDatabase.of(
                Scope.empty().run {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("q", varOf("X")),
                            atomOf("!"),
                            structOf("r", varOf("X"), varOf("Y"))
                    )
                },
                Scope.empty().run {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("s", varOf("X")))
                },
                Scope.empty().run { factOf(structOf("s", atomOf("d"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("a"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("b"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("c"))) },
                Scope.empty().run { factOf(structOf("r", atomOf("b"), atomOf("b1"))) },
                Scope.empty().run { factOf(structOf("r", atomOf("c"), atomOf("c1"))) }
        )

        val goal = Solve.Request(
                Signature("p", 2),
                ktListOf(Var.of("U"), Var.of("V")),
                DummyInstances.executionContext.copy(
                        libraries = Libraries(Library.of(
                                alias = "testLib",
                                theory = database,
                                primitives = mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
                        ))
                )
        )

        Solver.sld().solve(goal).toList().forEach { println("$it\n") }
    }

    @Test
    fun anotherCutExample() {
        val database = ClauseDatabase.of(
                Scope.empty().run {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("q", varOf("X")),
                            atomOf("!"),
                            structOf("r", varOf("Y"))
                    )
                },
                Scope.empty().run {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("s", varOf("X")))
                },
                Scope.empty().run { factOf(structOf("s", atomOf("d"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("a"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("b"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("c"))) },
                Scope.empty().run { factOf(structOf("r", atomOf("b1"))) },
                Scope.empty().run { factOf(structOf("r", atomOf("c1"))) }
        )

        val goal = Solve.Request(
                Signature("p", 2),
                ktListOf(Var.of("U"), Var.of("V")),
                DummyInstances.executionContext.copy(
                        libraries = Libraries(Library.of(
                                alias = "testLib",
                                theory = database,
                                primitives = mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
                        ))
                )
        )

        Solver.sld().solve(goal).toList().forEach { println("$it\n") }
    }

    @Test
    fun backtrackingWorksAsExpected() {
        /*
            my_reverse(L1,L2) :- my_rev(L1,L2,[]).

            my_rev([],L2,L2) :- !.
            my_rev([X|Xs],L2,Acc) :- my_rev(Xs,L2,[X|Acc]).
         */

        val database = ClauseDatabase.of(
                Scope.empty().run {
                    ruleOf(
                            structOf("my_reverse", varOf("L1"), varOf("L2")),
                            structOf("my_rev", varOf("L1"), varOf("L2"), listOf())
                    )
                },
                Scope.empty().run {
                    ruleOf(
                            structOf("my_rev", listOf(), varOf("L2"), varOf("L2")),
                            atomOf("!")
                    )
                },
                Scope.empty().run {
                    ruleOf(
                            structOf("my_rev", listFrom(ktListOf(varOf("X")), varOf("Xs")), varOf("L2"), varOf("Acc")),
                            structOf("my_rev", varOf("Xs"), varOf("L2"), listFrom(ktListOf(varOf("X")), varOf("Acc")))
                    )
                }
        )

        val goal = Solve.Request(
                Signature("my_reverse", 2),
                ktListOf(LogicList.of((1..4).map(Numeric.Companion::of)), Var.of("L")),
                DummyInstances.executionContext.copy(
                        libraries = Libraries(Library.of(
                                alias = "testLib",
                                theory = database,
                                primitives = mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
                        ))
                )
        )

        Solver.sld().solve(goal).toList().forEach { println("$it\n") }
    }
}
