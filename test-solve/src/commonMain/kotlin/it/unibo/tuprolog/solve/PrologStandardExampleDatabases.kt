package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.instantiationError
import it.unibo.tuprolog.solve.TestingClauseDatabases.replaceAllFunctors
import it.unibo.tuprolog.solve.TestingClauseDatabases.systemError
import it.unibo.tuprolog.solve.TestingClauseDatabases.timeOutException
import it.unibo.tuprolog.solve.TestingClauseDatabases.typeError
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.collections.listOf as ktListOf

/**
 * An object containing the collection of Prolog Standard databases and requests, testing ISO functionality
 *
 * @author Enrico
 */
object PrologStandardExampleDatabases {

    /**
     * The clause database used in Prolog Standard reference manual, when explaining solver functionality and search-trees
     *
     * ```prolog
     * p(X, Y) :- q(X), r(X, Y).
     * p(X, Y) :- s(X).
     * s(d).
     * q(a).
     * q(b).
     * q(c).
     * r(b, b1).
     * r(c, c1).
     * ```
     */
    val prologStandardExampleDatabase by lazy {
        prolog {
            theory(
                { "p"("X", "Y") `if` ("q"("X") and "r"("X", "Y")) },
                { "p"("X", "Y") `if` "s"("X") },
                { "s"("d") },
                { "q"("a") },
                { "q"("b") },
                { "q"("c") },
                { "r"("b", "b1") },
                { "r"("c", "c1") }
            )
        }
    }

    /**
     * Notable [prologStandardExampleDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- p(U, V).
     * ```
     */
    val prologStandardExampleDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                "p"("U", "V").hasSolutions(
                    { yes("U" to "b", "V" to "b1") },
                    { yes("U" to "c", "V" to "c1") },
                    { yes("U" to "d") }
                )
            )
        }
    }

    /**
     * Same as [prologStandardExampleDatabase] but first clause contains cut
     *
     * ```prolog
     * p(X, Y) :- q(X), !, r(X, Y).
     * p(X, Y) :- s(X).
     * s(d).
     * q(a).
     * q(b).
     * q(c).
     * r(b, b1).
     * r(c, c1).
     * ```
     */
    val prologStandardExampleWithCutDatabase by lazy {
        prolog {
            theory({ "p"("X", "Y") `if` ("q"("X") and "!" and "r"("X", "Y")) }) +
                    theoryOf(*prologStandardExampleDatabase.clauses.drop(1).toTypedArray())
        }
    }

    /**
     * Notable [prologStandardExampleWithCutDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- p(U, V).
     * ```
     */
    val prologStandardExampleWithCutDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                "p"("U", "V").hasSolutions({ no() })
            )
        }
    }

    /**
     * The database used in Prolog standard while writing examples for Conjunction
     * ```prolog
     * legs(A, 6) :- insect(A).
     * legs(A, 4) :- animal(A).
     * insect(bee).
     * insect(ant).
     * fly(bee).
     * ```
     */
    val conjunctionStandardExampleDatabase by lazy {
        prolog {
            theory(
                { "legs"("A", 6) `if` "insect"("A") },
                { "legs"("A", 4) `if` "animal"("A") },
                { "insect"("bee") },
                { "insect"("ant") },
                { "fly"("bee") }
            )
        }
    }

    /**
     * Notable [conjunctionStandardExampleDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- (insect(X) ; legs(X, 6)) , fly(X).
     * ```
     */
    val conjunctionStandardExampleDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                (("insect"("X") or "legs"("X", 6)) and "fly"("X")).hasSolutions(
                    { yes("X" to "bee") },
                    { yes("X" to "bee") },
                    { no() }
                ),
                (("insect"("X") and "fly"("X")) or ("legs"("X", 6) and "fly"("X"))).hasSolutions(
                    { yes("X" to "bee") },
                    { yes("X" to "bee") },
                    { no() }
                )
            )
        }
    }

    /**
     * The database used in Prolog standard while writing examples for Call
     * ```prolog
     * a(1).
     * a(2).
     * ```
     */
    val callStandardExampleDatabase by lazy {
        prolog {
            theory(
                { "a"(1) },
                { "a"(2) }
            )
        }
    }

    /**
     * Prolog Standard examples to test call primitive with [callStandardExampleDatabase]
     * ```prolog
     * ?- call('!') ; true.
     * ?- Z = !, call( (Z = !, a(X), Z) ).
     * ?- call( (Z = !, a(X), Z) ).
     * ?- call(fail).
     * ?- call(true, X).
     * ?- call(true, fail, 1).
     * ```
     */
    val callStandardExampleDatabaseGoalsToSolution by lazy {
        prolog {
            ktListOf(
                ("call"("!") or true).hasSolutions({ yes() }, { yes() }),
                ("="("Z", "!") and "call"("="("Z", "!") and "a"("X") and "Z")).hasSolutions(
                    { yes("X" to 1, "Z" to "!") }
                ),
                "call"("="("Z", "!") and "a"("X") and "Z").hasSolutions(
                    { yes("X" to 1, "Z" to "!") },
                    { yes("X" to 2, "Z" to "!") }
                ),
                "call"(false).hasSolutions({ no() }),
                "call"(true and "X").hasSolutions({ halt(instantiationError) }),
                "call"("true" and "false" and 1).hasSolutions({ halt(typeError) })
            )
        }
    }

    /**
     * The database used in Prolog standard while writing examples for Catch
     * ```prolog
     * p.
     * p :- throw(b).
     * r(X) :- throw(X).
     * q :- catch(p, B, true), r(c).
     * ```
     */
    val catchAndThrowStandardExampleDatabase by lazy {
        prolog {
            theory(
                { "p" },
                { "p" `if` "throw"("b") },
                { "r"("X") `if` "throw"("X") },
                { "q" `if` ("catch"("p", "B", true) and "r"("c")) }
            )
        }
    }

    /**
     * Notable [catchAndThrowStandardExampleDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- catch(p, X, true).
     * ?- catch(q, C, true).
     * ?- catch(throw(exit(1)), exit(X), true).
     * ?- catch(throw(true), X, X).
     * ?- catch(throw(fail), X, X).
     * ?- catch(throw(f(X, X)), f(X, g(X)), true).
     * ?- catch(throw(1), X, (fail; X)).
     * ?- catch(throw(fail), true, G).
     * ```
     */
    val catchAndThrowStandardExampleDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                "catch"("p", "X", true).hasSolutions(
                    { yes() },
                    { yes("X" to "b") }
                ),
                "catch"("q", "C", true).hasSolutions({ yes("C" to "c") }),
                "catch"("throw"("exit"(1)), "exit"("X"), true).hasSolutions({ yes("X" to 1) }),
                "catch"("throw"(true), "X", "X").hasSolutions({ yes("X" to true) }),
                "catch"("throw"(false), "X", "X").hasSolutions({ no() }),
                "catch"("throw"("f"("X", "X")), "f"("X", "g"("X")), true).hasSolutions({ halt(systemError) }),
                "catch"("throw"(1), "X", false or "X").hasSolutions({ halt(typeError) }),
                "catch"("throw"(false), true, "G").hasSolutions({ halt(systemError) })
            )
        }
    }

    /**
     * The database used in Prolog standard while writing examples for Not
     * ```prolog
     * shave(barber, X) :- \+ shave(X, X).
     *
     * test_Prolog_unifiable(X, Y) :- \+ \+ X = Y.
     *
     * p1 :- \+ q1.
     * q1 :- fail.
     * q1 :- true.
     * p2 :- \+ q2.
     * q2 :- !, fail.
     * q2 :- true.
     * ```
     */
    val notStandardExampleDatabase by lazy {
        prolog {
            theory(
                { "shave"("barber", "X") `if` "not"("shave"("X", "X")) },

                { "test_Prolog_unifiable"("X", "Y") `if` "not"("not"("X" equalsTo "Y")) },

                { "p1" `if` "not"("q1") },
                { "q1" `if` false },
                { "q1" `if` true },
                { "p2" `if` "not"("q2") },
                { "q2" `if` ("!" and false) },
                { "q2" `if` true }
            )
        }
    }

    /**
     * Notable [notStandardExampleDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- X = 3, \+((X = 1 ; X = 2)).
     * ?- \+(fail).
     * ?- \+(!) ; X = 1.
     * ?- \+((X = 1 ; X = 2)), X = 3.
     * ?- X = 1, \+((X = 1 ; X = 2)).
     * ?- \+((fail, 1)).
     *
     * ?- shave(barber, 'Donald').
     * ?- shave(barber, barber).
     *
     * ?- test_Prolog_unifiable(f(a, X), f(X, a)).
     * ?- test_Prolog_unifiable(f(a, X), f(X, b)).
     * ?- test_Prolog_unifiable(X, f(X)).
     *
     * ?- p1.
     * ?- p2.
     * ```
     */
    val notStandardExampleDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                (("X" equalsTo 3) and "\\+"(("X" equalsTo 1) or ("X" equalsTo 2))).hasSolutions({ yes("X" to 3) }),
                "\\+"("fail").hasSolutions({ yes() }),
                ("\\+"("!") or ("X" equalsTo 1)).hasSolutions({ yes("X" to 1) }),
                ("\\+"(("X" equalsTo 1) or ("X" equalsTo 2)) and ("X" equalsTo 3)).hasSolutions({ no() }),
                (("X" equalsTo 1) and "\\+"(("X" equalsTo 1) or ("X" equalsTo 2))).hasSolutions({ no() }),
                "\\+"("fail" and 1).hasSolutions({ halt(typeError) }),

                "shave"("barber", "'Donald'").hasSolutions({ yes() }),
                "shave"("barber", "barber").hasSolutions({ halt(timeOutException) }),

                "test_Prolog_unifiable"("f"("a", "X"), "f"("X", "a")).hasSolutions({ yes() }),
                "test_Prolog_unifiable"("f"("a", "X"), "f"("X", "b")).hasSolutions({ no() }),
                "test_Prolog_unifiable"("X", "f"("X")).hasSolutions({ no() }),

                atomOf("p1").hasSolutions({ no() }),
                atomOf("p2").hasSolutions({ yes() })
            ).flatMap { (goal, solutions) ->
                ktListOf(
                    goal to solutions,
                    goal.replaceAllFunctors("\\+", "not").let { it to solutions.changeQueriesTo(it) }
                )
            }
        }
    }

    /**
     * The database used in Prolog standard while writing examples for If-Then
     * ```prolog
     * legs(A, 6) :- insect(A).
     * legs(horse, 4).
     * insect(bee).
     * insect(ant).
     * ```
     */
    val ifThenStandardExampleDatabase by lazy {
        prolog {
            theory(
                { "legs"("A", 6) `if` "insect"("A") },
                { "legs"("horse", 4) },
                { "insect"("bee") },
                { "insect"("ant") }
            )
        }
    }

    /**
     * Notable [ifThenStandardExampleDatabase] request goals and respective expected [Solution]s
     * ```prolog
     * ?- X = 0 -> true.
     * ?- legs(A, 6) -> true.
     * ?- X \= 0 -> true.
     * ?- fail -> (true ; true).
     * ```
     */
    val ifThenStandardExampleDatabaseNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                ("->"("X" equalsTo 0, true)).hasSolutions({ yes("X" to 0) }),
                ("->"("legs"("A", 6), true)).hasSolutions({ yes("A" to "bee") }),
                ("->"("\\="("X", 0), true)).hasSolutions({ no() }),
                ("->"(false, ";"(true, true))).hasSolutions({ no() })
            )
        }
    }

    /**
     * Notable Prolog Standard example request goals and respective expected [Solution]s for If-Then-Else
     * ```prolog
     * ?- (X = 0 -> true ; fail).
     * ?- (X = 1, (X = 0 -> fail ; true)).
     * ?- (((!, X = 1, fail) -> true ; fail) ; X = 2).
     * ?- fail -> true ; true.
     * ?- ((!, X = 1, fail) -> true ; fail).
     * ```
     */
    val ifThenElseStandardExampleNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                ("->"("X" equalsTo 0, true) or false).hasSolutions({ yes("X" to 0) }),
                ("X" equalsTo 1 and ("->"("X" equalsTo 0, false) or true)).hasSolutions({ yes("X" to 1) }),
                (("->"("!" and ("X" equalsTo 1) and false, true) or false) or ("X" equalsTo 2)).hasSolutions({ yes("X" to 2) }),
                ("->"(false, true) or true).hasSolutions({ yes() }),
                ("->"("!" and ("X" equalsTo 1) and false, true) or false).hasSolutions({ no() })
            )
        }
    }

    /** Collection of all Prolog Standard example databases and their respective callable goals with expected solutions */
    val allPrologStandardTestingDatabasesToRespectiveGoalsAndSolutions by lazy {
        mapOf(
            prologStandardExampleDatabase to prologStandardExampleDatabaseNotableGoalToSolution,
            prologStandardExampleWithCutDatabase to prologStandardExampleWithCutDatabaseNotableGoalToSolution,
            conjunctionStandardExampleDatabase to conjunctionStandardExampleDatabaseNotableGoalToSolution,
            callStandardExampleDatabase to callStandardExampleDatabaseGoalsToSolution,
            catchAndThrowStandardExampleDatabase to catchAndThrowStandardExampleDatabaseNotableGoalToSolution,
            notStandardExampleDatabase to notStandardExampleDatabaseNotableGoalToSolution,
            ifThenStandardExampleDatabase to ifThenStandardExampleDatabaseNotableGoalToSolution,
            ClauseDatabase.empty() to ifThenElseStandardExampleNotableGoalToSolution
        )
    }
}
