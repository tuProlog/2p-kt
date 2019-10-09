package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.SolverUtils
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import it.unibo.tuprolog.solve.solver.SolverUtils.responseBy

/**
 * Implementation of primitive handling `','/2` behaviour
 *
 * @author Enrico
 */
internal object Conjunction : PrimitiveWrapper<ExecutionContextImpl>(Signature(Tuple.FUNCTOR, 2)) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> =
            sequence {
                val (leftSubGoal, rightSubGoal) = with(request) {
                    SolverUtils.orderedWithStrategy(arguments.asSequence(), context, context.solverStrategies::predicationChoiceStrategy)
                }.toList()

                val leftSubSolveRequest = request.newSolveRequest(prepareForExecution(leftSubGoal))

                var cutExecuted = false
                SolverSLD().solve(leftSubSolveRequest).forEach { leftResponse ->

                    if (leftResponse.sideEffectManager?.run { shouldCutExecuteInPrimitive() } == true)
                        cutExecuted = true

                    when (leftResponse.solution) {
                        is Solution.Yes -> {
                            val rightSubSolveRequest = leftSubSolveRequest.newSolveRequest(
                                    prepareForExecution(rightSubGoal.apply(leftResponse.solution.substitution)),
                                    leftResponse.solution.substitution,
                                    baseSideEffectManager = leftResponse.sideEffectManager,
                                    logicalParentRequest = request
                            )

                            SolverSLD().solve(rightSubSolveRequest).forEach { rightResponse ->
                                if (rightResponse.sideEffectManager?.run { shouldCutExecuteInPrimitive() } == true)
                                    cutExecuted = true

                                yield(request.responseBy(rightResponse))
                            }
                        }
                        else -> yield(request.responseBy(leftResponse))
                    }
                    if (cutExecuted) return@sequence
                }
            }
}
