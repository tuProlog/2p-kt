package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.responseBy

/**
 * Implementation of primitive handling `catch/3` behaviour
 *
 * @author Enrico
 */
internal object Catch : PrimitiveWrapper<ExecutionContextImpl>(Signature("catch", 3)) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> =
            sequence {
                val goalArgument = request.arguments.first()

                SolverSLD().solve(request.newSolveRequest(Struct.of(Call.functor, goalArgument))).forEach { goalResponse ->
                    when {
                        // if i'm the catch selected by throw/1 primitive
                        (goalResponse.sideEffectManager as? SideEffectManagerImpl)
                                ?.run { isSelectedThrowCatch(request.context) }
                                ?: false -> {

                            val recoverGoalArgument = request.arguments.last().apply(goalResponse.solution.substitution)

                            // attaching recover goal's solve request to catch parent, to not re-execute the catch if error thrown
                            val recoverGoalSolveRequest = request.newSolveRequest(
                                    Struct.of(Call.functor, recoverGoalArgument),
                                    goalResponse.solution.substitution
                            ).run {
                                // ensure that this catch cannot be selected again, to catch some error
                                copy(context = context
                                        .copy(sideEffectManager = (context.sideEffectManager as? SideEffectManagerImpl)
                                                ?.run { ensureNoMoreSelectableCatch(request.context) }
                                                ?: context.sideEffectManager
                                        )
                                )
                            }

                            yieldAll(SolverSLD().solve(recoverGoalSolveRequest).map { request.responseBy(it) })
                        }
                        else -> yield(request.responseBy(goalResponse))
                    }
                }
            }
}
