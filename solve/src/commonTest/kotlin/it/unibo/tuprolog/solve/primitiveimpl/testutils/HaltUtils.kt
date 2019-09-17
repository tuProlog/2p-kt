package it.unibo.tuprolog.solve.primitiveimpl.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.primitiveimpl.Call
import it.unibo.tuprolog.solve.primitiveimpl.Conjunction
import it.unibo.tuprolog.solve.primitiveimpl.Halt
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest

/**
 * Utils singleton to help testing [Halt]
 *
 * @author Enrico
 */
internal object HaltUtils {

    /** An halt primitive that will throw immediately HaltException */
    internal val exposedHaltBehaviourRequest = createSolveRequest(Atom.of("halt"), primitives = mapOf(Halt.descriptionPair))

    /** Halt primitive working examples (when wrapped inside other queries), with expected responses */
    internal val requestSolutionMap by lazy {
        mapOf(
                Struct.of("call", Atom.of("halt")).let {
                    createSolveRequest(it, primitives = mapOf(Halt.descriptionPair, Call.descriptionPair)).run {
                        this to listOf(
                                Solution.Halt(it, HaltException(context = this.context))
                        )
                    }
                },
                Tuple.of(Atom.of("halt"), Atom.of("halt")).let {
                    createSolveRequest(it, primitives = mapOf(Halt.descriptionPair, Conjunction.descriptionPair)).run {
                        this to listOf(
                                Solution.Halt(it, HaltException(context = this.context))
                        )
                    }
                },
                Struct.of("call", Tuple.of(Atom.of("halt"), Atom.of("halt"))).let {
                    createSolveRequest(it, primitives = mapOf(Halt.descriptionPair, Call.descriptionPair, Conjunction.descriptionPair)).run {
                        this to listOf(
                                Solution.Halt(it, HaltException(context = this.context))
                        )
                    }
                },
                Tuple.of(Atom.of("halt"), SolverTestUtils.threeResponseRequest.query!!).let { query ->
                    Scope.of(*SolverTestUtils.threeResponseRequest.arguments.map { it as Var }.toTypedArray()).run {
                        createSolveRequest(
                                query,
                                database = SolverTestUtils.factDatabase,
                                primitives = mapOf(Conjunction.descriptionPair, Halt.descriptionPair)
                        ).run {
                            this to listOf(
                                    Solution.Halt(query, HaltException(context = this.context))
                            )
                        }
                    }
                }
        )
    }

}
