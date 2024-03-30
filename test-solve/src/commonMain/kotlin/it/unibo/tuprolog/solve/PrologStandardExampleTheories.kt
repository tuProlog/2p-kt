package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.instantiationError
import it.unibo.tuprolog.solve.TestingClauseTheories.systemError
import it.unibo.tuprolog.solve.TestingClauseTheories.timeOutException
import it.unibo.tuprolog.solve.TestingClauseTheories.typeError
import it.unibo.tuprolog.theory.Theory

/**
 * An object containing the collection of Prolog Standard databases and requests, testing ISO functionality
 *
 * @author Enrico
 */
object PrologStandardExampleTheories {
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
    val prologStandardExampleTheory by lazy {
        logicProgramming {
            theory(
                { "p"("X", "Y") `if` ("q"("X") and "r"("X", "Y")) },
                { "p"("X", "Y") `if` "s"("X") },
                { "s"("d") },
                { "q"("a") },
                { "q"("b") },
                { "q"("c") },
                { "r"("b", "b1") },
                { "r"("c", "c1") },
            )
        }
    }

    /**
     * Notable [prologStandardExampleTheory] request goals and respective expected [Solution]s
     * ```prolog
     * ?- p(U, V).
     * ```
     */
    val prologStandardExampleTheoryNotableGoalToSolution by lazy {
        logicProgramming {
            listOf(
                "p"("U", "V").hasSolutions(
                    { yes("U" to "b", "V" to "b1") },
                    { yes("U" to "c", "V" to "c1") },
                    { yes("U" to "d", "V" to "Y") },
                ),
            )
        }
    }

    /**
     * Same as [prologStandardExampleTheory] but first clause contains cut
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
    val prologStandardExampleWithCutTheory by lazy {
        logicProgramming {
            theory({ "p"("X", "Y") `if` ("q"("X") and "!" and "r"("X", "Y")) }) +
                theoryOf(*prologStandardExampleTheory.clauses.drop(1).toTypedArray())
        }
    }

    /**
     * Notable [prologStandardExampleWithCutTheory] request goals and respective expected [Solution]s
     * ```prolog
     * ?- p(U, V).
     * ```
     */
    val prologStandardExampleWithCutTheoryNotableGoalToSolution by lazy {
        logicProgramming {
            listOf(
                "p"("U", "V").hasSolutions({ no() }),
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
    val conjunctionStandardExampleTheory by lazy {
        logicProgramming {
            theory(
                { "legs"("A", 6) `if` "insect"("A") },
                { "legs"("A", 4) `if` "animal"("A") },
                { "insect"("bee") },
                { "insect"("ant") },
                { "fly"("bee") },
            )
        }
    }

    /**
     * Notable [conjunctionStandardExampleTheory] request goals and respective expected [Solution]s
     * ```prolog
     * ?- (insect(X) ; legs(X, 6)) , fly(X).
     * ```
     */
    val conjunctionStandardExampleTheoryNotableGoalToSolution by lazy {
        logicProgramming {
            listOf(
                (("insect"("X") or "legs"("X", 6)) and "fly"("X")).hasSolutions(
                    { yes("X" to "bee") },
                    { yes("X" to "bee") },
                    { no() },
                ),
                (("insect"("X") and "fly"("X")) or ("legs"("X", 6) and "fly"("X"))).hasSolutions(
                    { yes("X" to "bee") },
                    { yes("X" to "bee") },
                    { no() },
                ),
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
    val callStandardExampleTheory by lazy {
        logicProgramming {
            theory(
                { "a"(1) },
                { "a"(2) },
            )
        }
    }

    /**
     * Prolog Standard examples to test call primitive with [callStandardExampleTheory]
     * ```prolog
     * ?- call('!') ; true.
     * ?- Z = !, call( (Z = !, a(X), Z) ).
     * ?- call( (Z = !, a(X), Z) ).
     * ?- call(fail).
     * ?- call(true, X).
     * ?- call(true, fail, 1).
     * ```
     */
    fun callStandardExampleTheoryGoalsToSolution(errorSignature: Signature) =
        logicProgramming {
            listOf(
                ("call"("!") or true).hasSolutions({ yes() }, { yes() }),
                ("="("Z", "!") and "call"("="("Z", "!") and "a"("X") and "Z")).hasSolutions(
                    { yes("X" to 1, "Z" to "!") },
                ),
                "call"("="("Z", "!") and "a"("X") and "Z").hasSolutions(
                    { yes("X" to 1, "Z" to "!") },
                    { yes("X" to 2, "Z" to "!") },
                ),
                "call"(false).hasSolutions({ no() }),
                "call"(true and "X").hasSolutions({ halt(instantiationError(errorSignature, varOf("X"))) }),
                "call"(
                    true and (false and 1),
                ).hasSolutions({ halt(typeError(errorSignature, true and (false and 1))) }),
            )
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
    val catchAndThrowTheoryExample by lazy {
        logicProgramming {
            theory(
                { "p" },
                { "p" `if` "throw"("b") },
                { "r"("X") `if` "throw"("X") },
                { "q" `if` ("catch"("p", "B", true) and "r"("c")) },
            )
        }
    }

    /**
     * Notable [catchAndThrowTheoryExample] request goals and respective expected [Solution]s
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
    val catchAndThrowTheoryExampleNotableGoalToSolution by lazy {
        logicProgramming {
            listOf(
                "catch"("p", "X", true).hasSolutions(
                    { yes("X" to "E") },
                    { yes("X" to "b") },
                ),
                "catch"("q", "C", true).hasSolutions({ yes("C" to "c") }),
                "catch"("throw"("exit"(1)), "exit"("X"), true).hasSolutions({ yes("X" to 1) }),
                "catch"("throw"(true), "X", "X").hasSolutions({ yes("X" to true) }),
                "catch"("throw"(false), "X", "X").hasSolutions({ no() }),
                "catch"("throw"("f"("X", "X")), "f"("X", "g"("X")), true).hasSolutions(
                    { halt(systemError("f"("X", "X"))) },
                ),
                "catch"("throw"(1), "X", false or "X").hasSolutions({ halt(typeError(";", 2, intOf(1))) }),
                "catch"("throw"(false), true, "G").hasSolutions({ halt(systemError(truthOf(false))) }),
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
    val notStandardExampleTheory by lazy {
        logicProgramming {
            theory(
                { "shave"("barber", "X") `if` "not"("shave"("X", "X")) },
                { "test_Prolog_unifiable"("X", "Y") `if` "not"("not"("X" equalsTo "Y")) },
                { "p1" `if` "not"("q1") },
                { "q1" `if` false },
                { "q1" `if` true },
                { "p2" `if` "not"("q2") },
                { "q2" `if` ("!" and false) },
                { "q2" `if` true },
            )
        }
    }

    /**
     * Notable [notStandardExampleTheory] request goals and respective expected [Solution]s
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
    fun notStandardExampleTheoryNotableGoalToSolution(
        nafErrorSignature: Signature,
        notErrorSignature: Signature,
    ) = logicProgramming {
        listOf(
            (("X" equalsTo 3) and "\\+"(("X" equalsTo 1) or ("X" equalsTo 2))).hasSolutions({ yes("X" to 3) }),
            "\\+"(fail).hasSolutions({ yes() }),
            ("\\+"("!") or ("X" equalsTo 1)).hasSolutions({ yes("X" to 1) }),
            ("\\+"(("X" equalsTo 1) or ("X" equalsTo 2)) and ("X" equalsTo 3)).hasSolutions({ no() }),
            (("X" equalsTo 1) and "\\+"(("X" equalsTo 1) or ("X" equalsTo 2))).hasSolutions({ no() }),
            "\\+"(fail and 1).hasSolutions({ halt(typeError(nafErrorSignature, fail and 1)) }),
            "shave"("barber", "'Donald'").hasSolutions({ yes() }),
            "shave"("barber", "barber").hasSolutions({ halt(timeOutException) }),
            "test_Prolog_unifiable"("f"("a", "X"), "f"("X", "a")).hasSolutions({ yes() }),
            "test_Prolog_unifiable"("f"("a", "X"), "f"("X", "b")).hasSolutions({ no() }),
            "test_Prolog_unifiable"("X", "f"("X")).hasSolutions({ no() }),
            atomOf("p1").hasSolutions({ no() }),
            atomOf("p2").hasSolutions({ yes() }),
            (("X" equalsTo 3) and "\\+"(("X" equalsTo 1) or ("X" equalsTo 2))).hasSolutions({ yes("X" to 3) }),
            "not"(fail).hasSolutions({ yes() }),
            ("not"("!") or ("X" equalsTo 1)).hasSolutions({ yes("X" to 1) }),
            ("not"(("X" equalsTo 1) or ("X" equalsTo 2)) and ("X" equalsTo 3)).hasSolutions({ no() }),
            (("X" equalsTo 1) and "not"(("X" equalsTo 1) or ("X" equalsTo 2))).hasSolutions({ no() }),
            "not"(fail and 1).hasSolutions({ halt(typeError(notErrorSignature, fail and 1)) }),
        )
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
    val ifThenStandardExampleTheory by lazy {
        logicProgramming {
            theory(
                { "legs"("A", 6) `if` "insect"("A") },
                { "legs"("horse", 4) },
                { "insect"("bee") },
                { "insect"("ant") },
            )
        }
    }

    /**
     * Notable [ifThenStandardExampleTheory] request goals and respective expected [Solution]s
     * ```prolog
     * ?- X = 0 -> true.
     * ?- legs(A, 6) -> true.
     * ?- X \= 0 -> true.
     * ?- fail -> (true ; true).
     * ```
     */
    val ifThenStandardExampleTheoryNotableGoalToSolution by lazy {
        logicProgramming {
            listOf(
                ("->"("X" equalsTo 0, true)).hasSolutions({ yes("X" to 0) }),
                ("->"("legs"("A", 6), true)).hasSolutions({ yes("A" to "bee") }),
                ("->"("\\="("X", 0), true)).hasSolutions({ no() }),
                ("->"(false, ";"(true, true))).hasSolutions({ no() }),
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
        logicProgramming {
            listOf(
                ("->"("X" equalsTo 0, true) or false).hasSolutions({ yes("X" to 0) }),
                ("X" equalsTo 1 and ("->"("X" equalsTo 0, false) or true)).hasSolutions({ yes("X" to 1) }),
                (
                    (
                        "->"(
                            "!" and ("X" equalsTo 1) and false,
                            true,
                        ) or false
                    ) or ("X" equalsTo 2)
                ).hasSolutions({ yes("X" to 2) }),
                ("->"(false, true) or true).hasSolutions({ yes() }),
                ("->"("!" and ("X" equalsTo 1) and false, true) or false).hasSolutions({ no() }),
            )
        }
    }

    /** Collection of all Prolog Standard example databases and their respective callable goals with expected solutions */
    fun allPrologStandardTestingTheoryToRespectiveGoalsAndSolutions(
        callErrorSignature: Signature,
        nafErrorSignature: Signature,
        notErrorSignature: Signature,
    ) = mapOf(
        prologStandardExampleTheory to prologStandardExampleTheoryNotableGoalToSolution,
        prologStandardExampleWithCutTheory to prologStandardExampleWithCutTheoryNotableGoalToSolution,
        conjunctionStandardExampleTheory to conjunctionStandardExampleTheoryNotableGoalToSolution,
        callStandardExampleTheory to callStandardExampleTheoryGoalsToSolution(callErrorSignature),
        catchAndThrowTheoryExample to catchAndThrowTheoryExampleNotableGoalToSolution,
        notStandardExampleTheory to notStandardExampleTheoryNotableGoalToSolution(nafErrorSignature, notErrorSignature),
        ifThenStandardExampleTheory to ifThenStandardExampleTheoryNotableGoalToSolution,
        Theory.empty() to ifThenElseStandardExampleNotableGoalToSolution,
    )
}
