package it.unibo.tuprolog.solve.primitiveimpl.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import it.unibo.tuprolog.solve.primitiveimpl.Call
import it.unibo.tuprolog.solve.primitiveimpl.Conjunction
import it.unibo.tuprolog.solve.primitiveimpl.Cut
import it.unibo.tuprolog.solve.primitiveimpl.Throw
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
import kotlin.collections.listOf as ktListOf

/**
 * Utils singleton to help testing [Call]
 *
 * @author Enrico
 */
internal object CallUtils {

    /** Call primitive working examples, with expected responses */
    internal val requestSolutionMap by lazy {
        mapOf(
                Struct.of(Call.signature.name, Truth.`true`()).let {
                    createSolveRequest(it, primitives = mapOf(Call.descriptionPair)) to ktListOf(
                            Solution.Yes(it, Substitution.empty())
                    )
                },
                Struct.of(Call.signature.name, Tuple.of(Truth.`true`(), Truth.`true`())).let {
                    createSolveRequest(it, primitives = mapOf(Call.descriptionPair, Conjunction.descriptionPair)) to ktListOf(
                            Solution.Yes(it, Substitution.empty())
                    )
                },
                Struct.of(Call.signature.name, Atom.of("!")).let {
                    createSolveRequest(it, primitives = mapOf(Call.descriptionPair, Cut.descriptionPair)) to ktListOf(
                            Solution.Yes(it, Substitution.empty())
                    )
                },
                Struct.of(Call.signature.name, SolverTestUtils.threeResponseRequest.query!!).let { query ->
                    Scope.of(*SolverTestUtils.threeResponseRequest.arguments.map { it as Var }.toTypedArray()).run {
                        createSolveRequest(query, database = SolverTestUtils.factDatabase, primitives = mapOf(Call.descriptionPair)) to ktListOf(
                                Solution.Yes(query, Substitution.of(varOf("A"), atomOf("a"))),
                                Solution.Yes(query, Substitution.of(varOf("A"), atomOf("b"))),
                                Solution.Yes(query, Substitution.of(varOf("A"), atomOf("c")))
                        )
                    }
                },
                Scope.empty {
                    structOf(Call.signature.name, tupleOf(structOf("g", varOf("A")), atomOf("!"))).let { query ->
                        createSolveRequest(query,
                                database = SolverTestUtils.factDatabase,
                                primitives = mapOf(Conjunction.descriptionPair, Cut.descriptionPair, Call.descriptionPair)
                        ) to ktListOf(
                                Solution.Yes(query, Substitution.of(varOf("A"), atomOf("a")))
                        )
                    }
                }
                // TODO: once tests will be refactored, here will go all other examples, because "call" should call them and results should be the same
        )
    }

    /** Requests that should throw errors */
    internal val requestToErrorSolutionMap by lazy {
        mapOf(
                Struct.of(Call.signature.name, Var.of("X")).let {
                    createSolveRequest(it, primitives = mapOf(Call.descriptionPair, Throw.descriptionPair)).run {
                        this to ktListOf(
                                Solution.Halt(it, HaltException(
                                        context = this.context,
                                        cause = SystemError(
                                                context = this.context,
                                                cause = InstantiationError(
                                                        context = this.context,
                                                        extraData = Var.of("X")
                                                ),
                                                extraData = InstantiationError(
                                                        context = this.context,
                                                        extraData = Var.of("X")
                                                ).errorStruct
                                        )
                                )))
                    }
                },
                Struct.of(Call.signature.name, Tuple.of(Truth.`true`(), Integer.of(1))).let {
                    createSolveRequest(it, primitives = mapOf(Call.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair)).run {
                        this to ktListOf(
                                Solution.Halt(it, HaltException(
                                        context = this.context,
                                        cause = with(TypeError(
                                                expectedType = TypeError.Expected.CALLABLE,
                                                actualValue = Tuple.of(Truth.`true`(), Integer.of(1)),
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
        )
    }

    /** Requests that will throw exceptions directly, if primitive invoked */
    internal val exposedErrorThrowingRequests by lazy {
        mapOf(
                Struct.of(Call.signature.name, Var.of("A")).let {
                    createSolveRequest(it, primitives = mapOf(Call.descriptionPair, Throw.descriptionPair)) to InstantiationError::class
                },
                Struct.of(Call.signature.name, Tuple.of(Truth.`true`(), Integer.of(1))).let {
                    createSolveRequest(it, primitives = mapOf(Call.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair)) to TypeError::class
                }
        )
    }

    /**
     * A request to test that [Call] limits [Cut] to have effect only inside its goal;
     *
     * `call/1` is said to be *opaque* (or not transparent) to cut.
     */
    internal val requestToSolutionOfCallWithCut by lazy {
        Scope.empty {
            structOf("call", tupleOf(structOf("g", varOf("A")), structOf("call", atomOf("!")))).let { query ->
                createSolveRequest(query,
                        database = SolverTestUtils.factDatabase,
                        primitives = mapOf(Conjunction.descriptionPair, Cut.descriptionPair, Call.descriptionPair)
                ) to ktListOf(
                        Solution.Yes(query, Substitution.of(varOf("A"), atomOf("a"))),
                        Solution.Yes(query, Substitution.of(varOf("A"), atomOf("b")))
                )
            }
        }
    }
}
