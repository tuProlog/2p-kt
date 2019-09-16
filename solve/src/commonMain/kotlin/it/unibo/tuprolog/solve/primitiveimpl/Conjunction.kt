package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.SolverUtils
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.noResponseBy
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import it.unibo.tuprolog.solve.solver.SolverUtils.yesResponseBy

/**
 * Implementation of primitive handling `','/2` behaviour
 *
 * @author Enrico
 */
object Conjunction : PrimitiveWrapper(Signature(Tuple.FUNCTOR, 2)) {

    override val uncheckedImplementation: Primitive = { mainRequest ->
        sequence {
            val (leftSubGoal, rightSubGoal) = with(mainRequest) {
                SolverUtils.orderedWithStrategy(arguments.asSequence(), context, context.solverStrategies::predicationChoiceStrategy)
            }.toList()

            val leftSubSolveRequest = mainRequest.newSolveRequest(prepareForExecution(leftSubGoal))

            var cutExecuted = false
            SolverSLD().solve(leftSubSolveRequest).forEach { leftResponse ->
                if (leftResponse.context.clauseScopedParents.any { it in leftResponse.context.toCutContextsParent })
                    cutExecuted = true

                when (leftResponse.solution) {
                    is Solution.Yes -> {
                        val rightSubSolveRequest = leftSubSolveRequest.newSolveRequest(
                                prepareForExecution(rightSubGoal.apply(leftResponse.context.currentSubstitution)),
                                leftResponse.context.currentSubstitution,
                                baseContext = leftResponse.context
                        )

                        SolverSLD().solve(rightSubSolveRequest).forEach { rightResponse ->
                            if (rightResponse.context.clauseScopedParents.any { it in rightResponse.context.toCutContextsParent })
                                cutExecuted = true

                            when (rightResponse.solution) {
                                is Solution.Yes -> yield(mainRequest.yesResponseBy(rightResponse))
                                is Solution.No -> yield(mainRequest.noResponseBy(rightResponse))
                            }
                        }
                    }
                    is Solution.No -> yield(mainRequest.noResponseBy(leftResponse))
                }
                if (cutExecuted) return@sequence
            }
        }
    }
}
