package it.unibo.tuprolog.libraries.stdlib.primitive.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.stdlib.primitive.Call
import it.unibo.tuprolog.libraries.stdlib.primitive.Catch
import it.unibo.tuprolog.libraries.stdlib.primitive.Conjunction
import it.unibo.tuprolog.libraries.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.changeQueriesTo
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import it.unibo.tuprolog.solve.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.collections.listOf as ktListOf

/**
 * Utils singleton to help testing [Catch]
 *
 * @author Enrico
 */
internal object CatchUtils {

    /**
     * Catch primitive examples, with expected responses
     *
     * Contained requests:
     * - `catch(true, _, fail).` **will result in** `Yes()`
     * - `catch(catch(throw(external(deepBall)), internal(I), fail), external(E), true)` **will result in** `Yes(E -> deepBall)`
     * - Plus all [CallUtils.requestSolutionMap] with and without `call` wrapping, and [CallUtils.requestToErrorSolutionMap]
     */
    internal val requestSolutionMap by lazy {
        mapOf(
                Struct.of(Catch.functor, Truth.`true`(), Var.anonymous(), Truth.fail()).let {
                    createSolveRequest(it, primitives = mapOf(Call.descriptionPair, Catch.descriptionPair)) to ktListOf(
                            Solution.Yes(it, Substitution.empty())
                    )
                },
                *CallUtils.requestSolutionMap.map { (callRequest, solutions) ->
                    Struct.of(Catch.functor, callRequest.query, Var.anonymous(), Truth.fail()).let {
                        createSolveRequest(it,
                                callRequest.context.libraries.theory,
                                callRequest.context.libraries.primitives + Catch.descriptionPair
                        ) to solutions.changeQueriesTo(it)
                    }
                }.toTypedArray(),
                *CallUtils.requestSolutionMap.map { (callRequest, solutions) ->
                    Struct.of(Catch.functor, callRequest.arguments[0], Var.anonymous(), Truth.fail()).let {
                        createSolveRequest(it,
                                callRequest.context.libraries.theory,
                                callRequest.context.libraries.primitives + Catch.descriptionPair
                        ) to solutions.changeQueriesTo(it)
                    }
                }.toTypedArray(),
                *CallUtils.requestToErrorSolutionMap.map { (callRequest, solutions) ->
                    Scope.empty {
                        structOf(Catch.functor, callRequest.arguments[0], varOf("X"), Truth.`true`()).let {
                            createSolveRequest(it,
                                    callRequest.context.libraries.theory,
                                    callRequest.context.libraries.primitives + Catch.descriptionPair
                            ) to solutions.map { callSolution ->
                                Solution.Yes(it, Substitution.of(varOf("X"), (callSolution.exception.cause?.cause as PrologError).errorStruct))
                            }
                        }
                    }
                }.toTypedArray(),
                Scope.empty {
                    structOf(Catch.functor,
                            structOf(Catch.functor,
                                    structOf(Throw.functor, structOf("external", atomOf("deepBall"))),
                                    structOf("internal", varOf("I")),
                                    Truth.fail()
                            ),
                            structOf("external", varOf("E")),
                            Truth.`true`()
                    ).let {
                        createSolveRequest(it, primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Throw.descriptionPair)) to
                                ktListOf(Solution.Yes(it, Substitution.of(varOf("E"), atomOf("deepBall"))))
                    }
                },
                Scope.empty {
                    structOf(Catch.functor,
                            structOf(Throw.functor, atomOf("first")),
                            varOf("X"),
                            structOf(Throw.functor, atomOf("second"))
                    ).let {
                        createSolveRequest(it, primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Throw.descriptionPair)).run {
                            this to ktListOf(Solution.Halt(it, HaltException(context = context)))
                        }
                    }
                }
        )
    }

    /**
     * Prolog standard examples for `catch/3` primitive
     *
     * Contains those requests against [prologStandardCatchExamplesDatabase]:
     *
     * - `catch(p, X, true)` **will result in** `Yes(), Yes(X -> b)`
     * - `catch(q, C, true)` **will result in** `Yes(C -> c)`
     */
    internal val prologStandardCatchExamples by lazy {
        mapOf(
                Scope.empty {
                    structOf(Catch.functor, atomOf("p"), varOf("X"), Truth.`true`()).let {
                        createSolveRequest(it,
                                primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair),
                                database = prologStandardCatchExamplesDatabase
                        ) to ktListOf(
                                Solution.Yes(it, Substitution.empty()),
                                Solution.Yes(it, Substitution.of(varOf("X"), atomOf("b")))
                        )
                    }
                },
                Scope.empty {
                    structOf(Catch.functor, atomOf("q"), varOf("C"), Truth.`true`()).let {
                        createSolveRequest(it,
                                primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair),
                                database = prologStandardCatchExamplesDatabase
                        ) to ktListOf(Solution.Yes(it, Substitution.of(varOf("C"), atomOf("c"))))
                    }
                }

        )
    }

    /**
     * Prolog standard examples for `throw/1` primitive
     *
     * Contains those requests against [prologStandardCatchExamplesDatabase]:
     *
     * - `catch(throw(exit(1)), exit(X), true).` **will result in** `Yes(X -> 1)`
     * - `catch(q, C, true).` **will result in** `Yes(C -> c)`
     * - `catch(throw(true), X, X).` **will result in** `Yes(X -> true)`
     * - `catch(throw(fail), X, X).` **will result in** `No()`
     */
    internal val prologStandardThrowExamples by lazy {
        mapOf(
                Scope.empty {
                    structOf(Catch.functor,
                            structOf(Throw.functor,
                                    structOf("exit", numOf(1))
                            ),
                            structOf("exit", varOf("X")),
                            Truth.`true`()
                    ).let {
                        createSolveRequest(it,
                                primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair),
                                database = prologStandardCatchExamplesDatabase
                        ) to ktListOf(Solution.Yes(it, Substitution.of(varOf("X"), numOf(1))))
                    }
                },
                Scope.empty {
                    structOf(Catch.functor, atomOf("q"), varOf("C"), Truth.`true`()).let {
                        createSolveRequest(it,
                                primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair),
                                database = prologStandardCatchExamplesDatabase
                        ) to ktListOf(Solution.Yes(it, Substitution.of(varOf("C"), atomOf("c"))))
                    }
                },
                Scope.empty {
                    structOf(Catch.functor, structOf(Throw.functor, Truth.`true`()), varOf("X"), varOf("X")).let {
                        createSolveRequest(it,
                                primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair),
                                database = prologStandardCatchExamplesDatabase
                        ) to ktListOf(Solution.Yes(it, Substitution.of(varOf("X"), Truth.`true`())))
                    }
                },
                Scope.empty {
                    structOf(Catch.functor, structOf(Throw.functor, Truth.fail()), varOf("X"), varOf("X")).let {
                        createSolveRequest(it,
                                primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair),
                                database = prologStandardCatchExamplesDatabase
                        ) to ktListOf(Solution.No(it))
                    }
                }
        )
    }

    /**
     * Prolog standard examples for `throw/1` primitive (that will throw errors)
     *
     * Contains those requests against [prologStandardCatchExamplesDatabase]:
     *
     * - `catch(throw(f(X, X)), f(X, g(X)), true).` **will result in** `Yes(X -> g(X))` if occur check disabled, `Halt(system_error)` otherwise
     * - `catch(throw(1), X, (fail; X)).` **will result in** `Halt(type_error(callable, (fail; 1)))`
     * - `catch(throw(fail), true, G).` **will result in** `Halt(system_error)`
     */
    internal val prologStandardThrowExamplesWithError by lazy {
        mapOf(
                Scope.empty {
                    structOf(Catch.functor,
                            structOf(Throw.functor,
                                    structOf("f", varOf("X"), varOf("X"))
                            ),
                            structOf("f", varOf("X"), structOf("g", varOf("X"))),
                            Truth.`true`()
                    ).let {
                        createSolveRequest(it,
                                primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair),
                                database = prologStandardCatchExamplesDatabase).run {
                            this to ktListOf(
                                    Solution.Halt(it, HaltException(
                                            context = this.context,
                                            cause = SystemError(
                                                    context = this.context,
                                                    extraData = structOf("f", varOf("X"), varOf("X"))
                                            )

                                    )))
                        }
                    }
                },
                Scope.empty {
                    structOf(Catch.functor,
                            structOf(Throw.functor, numOf(1)),
                            varOf("X"),
                            structOf(";", Truth.fail(), varOf("X"))
                    ).let {
                        createSolveRequest(it,
                                primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair),
                                database = prologStandardCatchExamplesDatabase).run {
                            this to ktListOf(
                                    Solution.Halt(it, HaltException(
                                            context = this.context,
                                            cause = with(TypeError(
                                                    expectedType = TypeError.Expected.CALLABLE,
                                                    actualValue = structOf(";", Truth.fail(), Integer.of(1)),
                                                    context = this.context
                                            )) {
                                                SystemError(
                                                        context = this.context,
                                                        cause = this,
                                                        extraData = this.errorStruct
                                                )
                                            }
                                    )))
                        }
                    }
                },
                Scope.empty {
                    structOf(Catch.functor, structOf(Throw.functor, Truth.fail()), Truth.`true`(), varOf("G")).let {
                        createSolveRequest(it,
                                primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair),
                                database = prologStandardCatchExamplesDatabase).run {
                            this to ktListOf(
                                    Solution.Halt(it, HaltException(
                                            context = this.context,
                                            cause = SystemError(
                                                    context = this.context,
                                                    extraData = Truth.fail()
                                            )

                                    )))
                        }
                    }
                }
        )
    }

    /**
     * The database used in Prolog Standard book to run examples of `catch/3` and `throw/1`
     *
     * ```
     * p.
     * p :- throw(b).
     * r(X) :- throw(X).
     * q :- catch(p, B, true), r(c).
     * ```
     */
    private val prologStandardCatchExamplesDatabase by lazy {
        ClauseDatabase.of(
                { factOf(atomOf("p")) },
                {
                    ruleOf(atomOf("p"),
                            structOf(Throw.functor, atomOf("b"))
                    )
                },
                {
                    ruleOf(structOf("r", varOf("X")),
                            structOf(Throw.functor, varOf("X"))
                    )
                },
                {
                    ruleOf(atomOf("q"),
                            structOf(
                                    Catch.functor,
                                    atomOf("p"),
                                    varOf("B"),
                                    Truth.`true`()
                            ),
                            structOf("r", atomOf("c"))
                    )
                }
        )
    }

}
