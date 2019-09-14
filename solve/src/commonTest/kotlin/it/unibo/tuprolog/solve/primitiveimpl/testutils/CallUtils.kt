package it.unibo.tuprolog.solve.primitiveimpl.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitiveimpl.Call
import it.unibo.tuprolog.solve.primitiveimpl.Conjunction
import it.unibo.tuprolog.solve.primitiveimpl.Cut
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
                Struct.of("call", Truth.`true`()).let {
                    createSolveRequest(it) to ktListOf(
                            Solution.Yes(it, Substitution.empty())
                    )
                },
                Struct.of("call", Tuple.of(Truth.`true`(), Truth.`true`())).let {
                    createSolveRequest(it, primitives = mapOf(Conjunction.descriptionPair)) to ktListOf(
                            Solution.Yes(it, Substitution.empty())
                    )
                },
                Struct.of("call", Atom.of("!")).let {
                    createSolveRequest(it, primitives = mapOf(Cut.descriptionPair)) to ktListOf(
                            Solution.Yes(it, Substitution.empty())
                    )
                },
                Struct.of("call", SolverTestUtils.threeResponseRequest.query!!).let { query ->
                    Scope.of(*SolverTestUtils.threeResponseRequest.arguments.map { it as Var }.toTypedArray()).run {
                        createSolveRequest(query, database = SolverTestUtils.factDatabase) to ktListOf(
                                Solution.Yes(query, Substitution.of(varOf("A"), atomOf("a"))),
                                Solution.Yes(query, Substitution.of(varOf("A"), atomOf("b"))),
                                Solution.Yes(query, Substitution.of(varOf("A"), atomOf("c")))
                        )
                    }
                },
                Scope.empty {
                    structOf("call", tupleOf(structOf("g", varOf("A")), atomOf("!"))).let { query ->
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

    /** Call primitive request with argument a non instantiated Var, should result in an "instantiation_error" */
    internal val requestOfCallWithNotInstantiatedVar by lazy {
        createSolveRequest(Struct.of("call", Var.of("X")))
    }

    /** Call primitive request with argument a non well-formed goal; should result in an "type_error(callable, Goal)" */
    internal val requestOfCallWithMalformedGoal by lazy {
        createSolveRequest(Struct.of("call", Tuple.of(Truth.`true`(), Integer.of(1))))
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
