package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.responseBy

/**
 * Implementation of primitive handling `catch/3` behaviour
 *
 * @author Enrico
 */
object Catch : PrimitiveWrapper(Signature("catch", 3)) {

    override val uncheckedImplementation: Primitive = { mainRequest ->
        sequence {
            val goalArgument = mainRequest.arguments.first()

            SolverSLD().solve(mainRequest.newSolveRequest(Struct.of(Call.functor, goalArgument))).forEach { goalResponse ->
                when (mainRequest.context) {
                    // i'm the catch selected by throw/1 primitive
                    goalResponse.context.throwRelatedToCutContextsParent -> {
                        val recoverGoalArgument = mainRequest.arguments.last().apply(goalResponse.context.currentSubstitution)

                        // attaching recover goal's solve request to catch parent, to not re-execute the catch if error thrown
                        val recoverGoalSolveRequest = mainRequest.newSolveRequest(
                                Struct.of(Call.functor, recoverGoalArgument),
                                goalResponse.context.currentSubstitution
                        ).run {
                            // ensure that this catch cannot be selected again, to catch some error
                            copy(context = context.copy(logicalParentRequests = context.logicalParentRequests.filterNot { it.context == mainRequest.context }))
                        }

                        yieldAll(SolverSLD().solve(recoverGoalSolveRequest).map { mainRequest.responseBy(it) })
                    }
                    else -> yield(mainRequest.responseBy(goalResponse))
                }
            }
        }
    }
}
