package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.TimeOutException
import kotlin.collections.listOf as ktListOf

/**
 * An object containing a collection of notable databases to be used when testing Solver functionality
 *
 * @author Enrico
 */
object TestingClauseDatabases {

    /**
     * A database containing the following facts:
     * ```prolog
     * f(a).
     * g(a).
     * g(b).
     * h(a).
     * h(b).
     * h(c).
     * ```
     */
    val simpleFactDatabase by lazy {
        prolog {
            theory(
                    { "f"("a") },
                    { "g"("a") },
                    { "g"("b") },
                    { "h"("a") },
                    { "h"("b") },
                    { "h"("c") }
            )
        }
    }

    /**
     * Notable [simpleFactDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- f(A).
     * ?- g(A).
     * ?- h(A).
     * ```
     */
    val simpleFactDatabaseNotableGoalToSolutions by lazy {
        prolog {
            ktListOf(
                    "f"("A").run {
                        to(ktListOf(
                                yesSolution("A" to "a")
                        ))
                    },
                    "g"("A").run {
                        to(ktListOf(
                                yesSolution("A" to "a"),
                                yesSolution("A" to "b")
                        ))
                    },
                    "h"("A").run {
                        to(ktListOf(
                                yesSolution("A" to "a"),
                                yesSolution("A" to "b"),
                                yesSolution("A" to "c")
                        ))
                    }
            )
        }
    }

    /**
     * A database containing the following rules:
     * ```prolog
     * f(only) :- !.
     * f(a).
     *
     * g(a).
     * g(only) :- !.
     * g(b).
     *
     * h(A) :- e(A).
     * h(A) :- d(A).
     * e(a) :- !.
     * e(b).
     * d(c).
     * d(d).
     * ```
     */
    val simpleCutDatabase = prolog {
        theory(
                { "f"("only") `if` "!" },
                { "f"("a") },

                { "g"("a") },
                { "g"("only") `if` "!" },
                { "g"("b") },

                { "h"("A") `if` "e"("A") },
                { "h"("A") `if` "d"("A") },
                { "e"("a") `if` "!" },
                { "e"("b") },
                { "d"("c") },
                { "d"("d") }
        )
    }

    /**
     * Notable [simpleCutDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- f(A).
     * ?- g(A).
     * ?- h(A).
     * ```
     */
    val simpleCutDatabaseNotableGoalToSolutions by lazy {
        prolog {
            ktListOf(
                    "f"("A").run {
                        to(ktListOf(
                                yesSolution("A" to "only")
                        ))
                    },
                    "g"("A").run {
                        to(ktListOf(
                                yesSolution("A" to "a"),
                                yesSolution("A" to "only")
                        ))
                    },
                    "h"("A").run {
                        to(ktListOf(
                                yesSolution("A" to "a"),
                                yesSolution("A" to "c"),
                                yesSolution("A" to "d")
                        ))
                    }
            )
        }
    }

    /**
     * A database containing the following rules:
     * ```prolog
     * f(X, Y) :- q(X), !, r(Y).
     * f(X, Y) :- r(X).
     * q(a).
     * q(b).
     * r(a1).
     * r(b1).
     * ```
     */
    val simpleCutAndConjunctionDatabase = prolog {
        theory(
                { "f"("X", "Y") `if` tupleOf("q"("X"), "!", "r"("Y")) },
                { "f"("X", "Y") `if` "r"("X") },
                { "q"("a") },
                { "q"("b") },
                { "r"("a1") },
                { "r"("b1") }
        )
    }

    /**
     * Notable [simpleCutAndConjunctionDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- f(A, B).
     * ```
     */
    val simpleCutAndConjunctionDatabaseNotableGoalToSolutions by lazy {
        prolog {
            ktListOf(
                    "f"("A", "B").run {
                        to(ktListOf(
                                yesSolution(Substitution.of("A" to "a", "B" to "a1")),
                                yesSolution(Substitution.of("A" to "a", "B" to "b1"))
                        ))
                    }
            )
        }
    }

    /**
     * A database containing the following rules:
     * ```prolog
     * a(X) :- b(X).
     * a(6).
     * b(X) :- c(X), d(X).
     * b(4) :- !.
     * b(5).
     * c(1).
     * c(2) :- !.
     * c(3).
     * d(2).
     * d(3).
     * ```
     */
    val cutConjunctionAndBacktrackingDatabase = prolog {
        theory(
                { "a"("X") `if` "b"("X") },
                { "a"(6) },
                { "b"("X") `if` tupleOf("c"("X"), "d"("X")) },
                { "b"(4) `if` "!" },
                { "b"(5) },
                { "c"(1) },
                { "c"(2) `if` "!" },
                { "c"(3) },
                { "d"(2) },
                { "d"(3) }
        )
    }

    /**
     * Notable [cutConjunctionAndBacktrackingDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- a(X).
     * ```
     */
    val cutConjunctionAndBacktrackingDatabaseNotableGoalToSolutions by lazy {
        prolog {
            ktListOf(
                    "a"("X").run {
                        to(ktListOf(
                                yesSolution("X" to 2),
                                yesSolution("X" to 4),
                                yesSolution("X" to 6)
                        ))
                    }
            )
        }
    }

    /**
     * A database containing the following rules:
     * ```prolog
     * a :- b.
     * b :- a.
     * ```
     */
    val infiniteComputationDatabase = prolog {
        theory(
                { "a" `if` "b" },
                { "b" `if` "a" }
        )
    }

    /**
     * Notable [infiniteComputationDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- a(X).
     * ```
     */
    val infiniteComputationDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                    atomOf("a").run {
                        to(ktListOf(
                                haltSolution(TimeOutException(context = DummyInstances.executionContext, exceededDuration = 0))
                        ))
                    }
            )
        }
    }

    /**
     * A database that implements custom "reverse" over lists; it should test backtracking functionality
     *
     * ```prolog
     * my_reverse(L1, L2) :- my_rev(L1, L2, []).
     *
     * my_rev([], L2, L2) :- !.
     * my_rev([X | Xs], L2, Acc) :- my_rev(Xs, L2, [X | Acc]).
     * ```
     */
    val customReverseListDatabase by lazy {
        prolog {
            theory(
                    { "my_reverse"("L1", "L2") `if` "my_rev"("L1", "L2", List.empty()) },
                    { "my_rev"(List.empty(), "L2", "L2") `if` "!" },
                    {
                        "my_rev"(consOf("X", "Xs"), "L2", "Acc") `if`
                                "my_rev"("Xs", "L2", consOf("X", "Acc"))
                    }
            )
        }
    }

    /**
     * Notable [customReverseListDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- p(U, V).
     * ```
     */
    val customReverseListDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                    "my_reverse"(listOf(1, 2, 3, 4), "L").run {
                        to(ktListOf(yesSolution("L" to listOf(4, 3, 2, 1))))
                    }
            )
        }
    }
}
