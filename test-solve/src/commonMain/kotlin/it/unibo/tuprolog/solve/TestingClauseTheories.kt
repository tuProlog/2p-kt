package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.allPrologStandardTestingTheoryToRespectiveGoalsAndSolutions
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.core.List as LogicList
import kotlin.collections.listOf as ktListOf

/**
 * An object containing a collection of notable databases to be used when testing Solver functionality
 *
 * @author Enrico
 */
object TestingClauseTheories {

    internal val aContext = DummyInstances.executionContext

    internal val haltException = HaltException(context = aContext)

    internal fun instantiationError(functor: String, arity: Int, culprit: Var, index: Int) =
        InstantiationError.forArgument(aContext, Signature(functor, arity), culprit, index)

    internal fun typeError(functor: String, arity: Int, actualValue: Term, index: Int) =
        TypeError.forArgument(aContext, Signature(functor, arity), TypeError.Expected.CALLABLE, actualValue, index)

    internal val systemError = SystemError(context = aContext)
    internal val timeOutException = TimeOutException(context = aContext, exceededDuration = 1)

    /** Utility function to deeply replace all occurrences of one functor with another in a Struct */
    internal fun Struct.replaceAllFunctors(oldFunctor: String, withFunctor: String): Struct =
        prolog {
            val synonymReplacer = object : TermVisitor<Term> {
                override fun defaultValue(term: Term): Term = term
                override fun visit(term: Struct): Term = when (term.functor) {
                    oldFunctor -> withFunctor(term.args.single().accept(this))
                    else -> term.args.map { it.accept(this) }.let {
                        if (it.none()) term
                        else term.functor(it.first(), *it.drop(1).toTypedArray())
                    }
                }
            }

            this@replaceAllFunctors.accept(synonymReplacer) as Struct
        }

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
    val simpleFactTheory by lazy {
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
     * Notable [simpleFactTheory] request goals and respective expected [Solution]s
     * ```prolog
     * ?- f(A).
     * ?- g(A).
     * ?- h(A).
     * ```
     */
    val simpleFactTheoryNotableGoalToSolutions by lazy {
        prolog {
            ktListOf(
                "f"("A").hasSolutions(
                    { yes("A" to "a") }
                ),
                "g"("A").hasSolutions(
                    { yes("A" to "a") },
                    { yes("A" to "b") }
                ),
                "h"("A").hasSolutions(
                    { yes("A" to "a") },
                    { yes("A" to "b") },
                    { yes("A" to "c") }
                )
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
    val simpleCutTheory = prolog {
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
     * Notable [simpleCutTheory] request goals and respective expected [Solution]s
     * ```prolog
     * ?- f(A).
     * ?- g(A).
     * ?- h(A).
     * ```
     */
    val simpleCutTheoryNotableGoalToSolutions by lazy {
        prolog {
            ktListOf(
                "f"("A").hasSolutions(
                    { yes("A" to "only") }
                ),
                "g"("A").hasSolutions(
                    { yes("A" to "a") },
                    { yes("A" to "only") }
                ),
                "h"("A").hasSolutions(
                    { yes("A" to "a") },
                    { yes("A" to "c") },
                    { yes("A" to "d") }
                )
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
    val simpleCutAndConjunctionTheory = prolog {
        theory(
            { "f"("X", "Y") `if` ("q"("X") and "!" and "r"("Y")) },
            { "f"("X", "Y") `if` "r"("X") },
            { "q"("a") },
            { "q"("b") },
            { "r"("a1") },
            { "r"("b1") }
        )
    }

    /**
     * Notable [simpleCutAndConjunctionTheory] request goals and respective expected [Solution]s
     * ```prolog
     * ?- f(A, B).
     * ```
     */
    val simpleCutAndConjunctionTheoryNotableGoalToSolutions by lazy {
        prolog {
            ktListOf(
                "f"("A", "B").hasSolutions(
                    { yes("A" to "a", "B" to "a1") },
                    { yes("A" to "a", "B" to "b1") }
                )
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
    val cutConjunctionAndBacktrackingTheory = prolog {
        theory(
            { "a"("X") `if` "b"("X") },
            { "a"(6) },
            { "b"("X") `if` ("c"("X") and "d"("X")) },
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
     * Notable [cutConjunctionAndBacktrackingTheory] request goals and respective expected [Solution]s
     * ```prolog
     * ?- a(X).
     * ```
     */
    val cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions by lazy {
        prolog {
            ktListOf(
                "a"("X").hasSolutions(
                    { yes("X" to 2) },
                    { yes("X" to 4) },
                    { yes("X" to 6) }
                )
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
    val infiniteComputationTheory = prolog {
        theory(
            { "a" `if` "b" },
            { "b" `if` "a" }
        )
    }

    /**
     * Notable [infiniteComputationTheory] request goals and respective expected [Solution]s
     * ```prolog
     * ?- a(X).
     * ```
     */
    val infiniteComputationTheoryNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                atomOf("a").hasSolutions({ halt(timeOutException) })
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
    val customReverseListTheory by lazy {
        prolog {
            theory(
                { "my_reverse"("L1", "L2") `if` "my_rev"("L1", "L2", emptyList) },
                { "my_rev"(emptyList, "L2", "L2") `if` "!" },
                {
                    "my_rev"(consOf("X", "Xs"), "L2", "Acc") `if`
                            "my_rev"("Xs", "L2", consOf("X", "Acc"))
                }
            )
        }
    }

    /**
     * Notable [customReverseListTheory] request goals and respective expected [Solution]s
     * ```prolog
     * ?- my_reverse([1, 2, 3, 4], L).
     * ```
     */
    val customReverseListTheoryNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                "my_reverse"(listOf(1, 2, 3, 4), "L").hasSolutions(
                    { yes("L" to listOf(4, 3, 2, 1)) }
                )
            )
        }
    }

    /**
     * A database that implements custom "range" to generate lists; it should test backtracking functionality along with arithmetic
     *
     * ```prolog
     * range(N, N, [N]) :- !.
     * range(M, N, [M | Ns]) :-
     *       M < N,
     *       M1 is M + 1,
     *       range(M1, N, Ns).
     * ```
     */
    val customRangeListGeneratorTheory by lazy {
        prolog {
            theory(
                { ("range"("N", "N", listOf("N")) `if` "!") },
                {
                    ("range"("M", "N", listFrom(ktListOf(varOf("M")), varOf("Ns"))) `if` (
                            "<"("M", "N") and
                                    ("M1" `is` (varOf("M") + 1)) and
                                    "range"("M1", "N", "Ns")
                            ))
                }
            )
        }
    }

    /**
     * Notable [customRangeListGeneratorTheory] request goals and respective expected [Solution]s
     * ```prolog
     * ?- range(1, 4, [1, 2, 3, 4]).
     * ?- range(1, 4, [1, 2, 3, 4, 5]).
     * ?- range(1, 4, L).
     * ?- range(1, 1, L).
     * ?- range(2, 1, L).
     * ?- range(A, 4, [2, 3, 4]).
     * ?- range(2, A, [2, 3, 4]).
     * ```
     */
    val customRangeListGeneratorTheoryNotableGoalToSolution by lazy {
        prolog {
            ktListOf(
                "range"(1, 4, listOf(1, 2, 3, 4)).hasSolutions({ yes() }),
                "range"(1, 4, listOf(1, 2, 3, 4, 5)).hasSolutions({ no() }),
                "range"(1, 4, "L").hasSolutions(
                    { yes("L" to listOf(1, 2, 3, 4)) }
                ),
                "range"(1, 1, "L").hasSolutions(
                    { yes("L" to listOf(1)) }
                ),
                "range"(2, 1, "L").hasSolutions({ no() }),
                "range"("A", 4, listOf(2, 3, 4)).hasSolutions({ yes("A" to 2) }),
                "range"(2, "A", listOf(2, 3, 4)).hasSolutions({ halt(instantiationError("range", 3, varOf("A"), 2)) })
            )
        }
    }

    /**
     * Call primitive request goals and respective expected [Solution]s
     *
     * Contained requests:
     * ```prolog
     * ?- call(true).
     * ?- call(fail).
     * ?- call(halt).
     * ?- call((true, true)).
     * ?- call('!').
     * ?- call(X).
     * ?- call((true, 1)).
     * ```
     */
    val callTestingGoalsToSolutions by lazy {
        prolog {
            ktListOf(
                "call"(true).hasSolutions({ yes() }),
                "call"(false).hasSolutions({ no() }),
                "call"("halt").hasSolutions({ halt(haltException) }),
                "call"("true" and "true").hasSolutions({ yes() }),
                "call"("!").hasSolutions({ yes() }),
                "call"("X").hasSolutions({ halt(instantiationError("call", 1, varOf("X"), 0)) }),
                "call"("true" and 1).hasSolutions({ halt(typeError("call", 1, ("true" and 1), 0)) })
            )
        }
    }

    /**
     * Catch primitive request goals and respective expected [Solution]s
     *
     * Contained requests:
     * ```prolog
     * ?- catch(true, _, fail).
     * ?- catch(catch(throw(external(deepBall)), internal(I), fail), external(E), true).
     * ?- catch(throw(first), X, throw(second)).
     * ?- catch(throw(hello), X, true).
     * ?- catch((throw(hello), fail), X, true).
     * ```
     */
    val catchTestingGoalsToSolutions by lazy {
        prolog {
            ktListOf(
                "catch"(true, `_`, false).hasSolutions({ yes() }),
                "catch"("catch"("throw"("external"("deepBall")), "internal"("I"), false), "external"("E"), true)
                    .hasSolutions({ yes("E" to "deepBall") }),
                "catch"("throw"("first"), "X", "throw"("second")).hasSolutions(
                    { halt(systemError) }
                ),
                "catch"("throw"("hello"), "X", true).hasSolutions({ yes("X" to "hello") }),
                "catch"("throw"("hello") and false, "X", true).hasSolutions({ yes("X" to "hello") })
            )
        }
    }

    /**
     * Halt primitive request goals and respective expected [Solution]s
     *
     * Contained requests:
     * ```prolog
     * ?- halt.
     * ?- catch(halt, _, true).
     * ?- catch(catch(throw(something), _, halt), _, true).
     * ```
     */
    val haltTestingGoalsToSolutions by lazy {
        prolog {
            ktListOf(
                atomOf("halt").hasSolutions({ halt(haltException) }),
                "catch"("halt", `_`, true).hasSolutions({ halt(haltException) }),
                "catch"("catch"("throw"("something"), `_`, "halt"), `_`, true).hasSolutions({ halt(haltException) })
            )
        }
    }

    /** Collection of all Prolog example (custom created and from Prolog Standard) databases and their respective callable goals with expected solutions */
    val allPrologTestingTheoriesToRespectiveGoalsAndSolutions by lazy {
        mapOf(
            simpleFactTheory to simpleFactTheoryNotableGoalToSolutions,
            simpleCutTheory to simpleCutTheoryNotableGoalToSolutions,
            simpleCutAndConjunctionTheory to simpleCutAndConjunctionTheoryNotableGoalToSolutions,
            cutConjunctionAndBacktrackingTheory to cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions,
            customReverseListTheory to customReverseListTheoryNotableGoalToSolution,
            Theory.empty() to callTestingGoalsToSolutions,
            Theory.empty() to catchTestingGoalsToSolutions,
            Theory.empty() to haltTestingGoalsToSolutions
        ) + allPrologStandardTestingTheoryToRespectiveGoalsAndSolutions
    }
}
