package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
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
     * q(a).
     * q(b).
     * r(a1).
     * r(b1).
     * ```
     */
    val simpleCutAndConjunctionDatabase = prolog {
        theory(
                { "f"("X", "Y") `if` tupleOf("q"("X"), "!", "r"("Y")) },
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
}

/** Utility function to help writing tests; it creates a [Solution.Yes] with receiver query and provided substitution */
fun Struct.yesSolution(withSubstitution: Substitution = Substitution.empty()) = Solution.Yes(this, withSubstitution as Substitution.Unifier)

/** Utility function to help writing tests; it creates a [Solution.No] with receiver query */
fun Struct.noSolution() = Solution.No(this)

/** Utility function to help writing tests; it creates a [Solution.Halt] with receiver query and provided exception */
fun Struct.haltSolution(withException: TuPrologRuntimeException) = Solution.Halt(this, withException)
