package it.unibo.tuprolog.solve.primitiveimpl.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.primitiveimpl.Call
import it.unibo.tuprolog.solve.primitiveimpl.Catch
import it.unibo.tuprolog.solve.primitiveimpl.Conjunction
import it.unibo.tuprolog.solve.primitiveimpl.Throw
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
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
                Struct.of(Catch.signature.name, Truth.`true`(), Var.anonymous(), Truth.fail()).let {
                    createSolveRequest(it, primitives = mapOf(Call.descriptionPair, Catch.descriptionPair)) to ktListOf(
                            Solution.Yes(it, Substitution.empty())
                    )
                },
                *CallUtils.requestSolutionMap.map { (callRequest, solutions) ->
                    Struct.of(Catch.signature.name, callRequest.query!!, Var.anonymous(), Truth.fail()).let {
                        createSolveRequest(it,
                                callRequest.context.libraries.theory,
                                callRequest.context.libraries.primitives + Catch.descriptionPair
                        ) to solutions.map { callSolution -> callSolution.copy(query = it) }
                    }
                }.toTypedArray(),
                *CallUtils.requestSolutionMap.map { (callRequest, solutions) ->
                    Struct.of(Catch.signature.name, callRequest.arguments[0], Var.anonymous(), Truth.fail()).let {
                        createSolveRequest(it,
                                callRequest.context.libraries.theory,
                                callRequest.context.libraries.primitives + Catch.descriptionPair
                        ) to solutions.map { callSolution -> callSolution.copy(query = it) }
                    }
                }.toTypedArray(),
                *CallUtils.requestToErrorSolutionMap.map { (callRequest, solutions) ->
                    Scope.empty {
                        structOf(Catch.signature.name, callRequest.arguments[0], varOf("X"), Truth.`true`()).let {
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
                    structOf(Catch.signature.name,
                            structOf(Catch.signature.name,
                                    structOf(Throw.signature.name, structOf("external", atomOf("deepBall"))),
                                    structOf("internal", varOf("I")),
                                    Truth.fail()
                            ),
                            structOf("external", varOf("E")),
                            Truth.`true`()
                    ).let {
                        createSolveRequest(it, primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Throw.descriptionPair)) to
                                ktListOf(Solution.Yes(it, Substitution.of(varOf("E"), atomOf("deepBall"))))
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
                    structOf(Catch.signature.name, atomOf("p"), varOf("X"), Truth.`true`()).let {
                        createSolveRequest(
                                it,
                                primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair),
                                database = prologStandardCatchExamplesDatabase
                        ) to ktListOf(
                                Solution.Yes(it, Substitution.empty()),
                                Solution.Yes(it, Substitution.of(varOf("X"), atomOf("b")))
                        )
                    }
                },
                Scope.empty {
                    structOf(Catch.signature.name, atomOf("q"), varOf("C"), Truth.`true`()).let {
                        createSolveRequest(
                                it,
                                primitives = mapOf(Call.descriptionPair, Catch.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair),
                                database = prologStandardCatchExamplesDatabase
                        ) to ktListOf(
                                Solution.Yes(it, Substitution.of(varOf("C"), atomOf("c")))
                        )
                    }
                }

        )
    }

    /**
     * Prolog standard examples for `throw/1` primitive
     *
     * Contains those requests against [prologStandardCatchExamplesDatabase]:
     *
     * - TODO from prolog standard
     */
    internal val prologStandardThrowExamples by lazy {
        // TODO: 19/09/2019
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
                Fact.of(Atom.of("p")),
                Rule.of(Atom.of("p"),
                        Struct.of(Throw.signature.name, Atom.of("b"))
                ),
                Scope.empty {
                    Rule.of(structOf("r", varOf("X")),
                            structOf(Throw.signature.name, varOf("X"))
                    )
                },
                Rule.of(Atom.of("q"),
                        Struct.of(
                                Catch.signature.name,
                                Atom.of("p"),
                                Var.of("B"),
                                Truth.`true`()
                        ),
                        Struct.of("r", Atom.of("c"))
                )
        )
    }

}
