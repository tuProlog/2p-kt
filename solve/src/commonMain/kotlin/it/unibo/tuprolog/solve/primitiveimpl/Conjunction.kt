package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
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
object Conjunction : PrimitiveWrapper(Signature(Tuple.FUNCTOR, 2)) {

    override val uncheckedImplementation: Primitive = { mainRequest ->
        // TODO: 25/09/2019 remove that
        mainRequest as Solve.Request<ExecutionContextImpl>

        sequence {
            val (leftSubGoal, rightSubGoal) = with(mainRequest) {
                SolverUtils.orderedWithStrategy(arguments.asSequence(), context, context.solverStrategies::predicationChoiceStrategy)
            }.toList()

            val leftSubSolveRequest = mainRequest.newSolveRequest(prepareForExecution(leftSubGoal))

            var cutExecuted = false
            SolverSLD().solve(leftSubSolveRequest).forEach { leftResponse ->
                if (leftResponse.context!!.clauseScopedParents.any { it in leftResponse.context.toCutContextsParent }
                        || leftResponse.context.logicalParentRequests.any { it.context == leftResponse.context.throwRelatedToCutContextsParent })
                    cutExecuted = true

                when (leftResponse.solution) {
                    is Solution.Yes -> {
                        val rightSubSolveRequest = leftSubSolveRequest.newSolveRequest(
                                prepareForExecution(rightSubGoal.apply(leftResponse.context.substitution)),
                                leftResponse.context.substitution,
                                baseContext = leftResponse.context,
                                logicalParentRequest = mainRequest
                        )

                        SolverSLD().solve(rightSubSolveRequest).forEach { rightResponse ->
                            if (rightResponse.context!!.clauseScopedParents.any { it in rightResponse.context.toCutContextsParent }
                                    || rightResponse.context.logicalParentRequests.any { it.context == rightResponse.context.throwRelatedToCutContextsParent })
                                cutExecuted = true

                            yield(mainRequest.responseBy(rightResponse))
                        }
                    }
                    else -> yield(mainRequest.responseBy(leftResponse))
                }
                if (cutExecuted) return@sequence
            }
        }
    }
}
