package it.unibo.tuprolog.solve.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.StreamsSolver
import it.unibo.tuprolog.solve.solver.*

/**
 * Implementation of primitive handling `catch/3` behaviour
 *
 * @author Enrico
 */
internal object Catch : PrimitiveWrapper<ExecutionContextImpl>("catch", 3) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> =
        sequence {
            val goalArgument = request.arguments.first()

            StreamsSolver.solve(request.newSolveRequest(call(goalArgument))).forEach { goalResponse ->
                when {
                    // if i'm the catch selected by throw/1 primitive
                    goalResponse.sideEffectManager.isSelectedThrowCatch(request.context) -> {

                        val recoverGoalArgument = request.arguments.last().apply(goalResponse.solution.substitution)

                        // attaching recover goal's solve request to catch/3 parent, to not re-execute this catch/3 if error thrown
                        val recoverGoalSolveRequest = request
                            .newSolveRequest(
                                call(recoverGoalArgument),
                                goalResponse.solution.substitution - request.context.substitution
                            )
                            .ensureNoMoreSelectableCatch(request.context)

                        yieldAll(StreamsSolver.solve(recoverGoalSolveRequest).map { request.replyWith(it) })
                    }
                    else -> yield(request.replyWith(goalResponse))
                }
            }
        }

    /** Utility function to create the struct: call([goal]) */
    private fun call(goal: Term) = Struct.of(Call.functor, goal)

    /** Calls [SideEffectManagerImpl.ensureNoMoreSelectableCatch] */
    private fun Solve.Request<ExecutionContextImpl>.ensureNoMoreSelectableCatch(notSelectableContext: ExecutionContextImpl) =
        copy(
            context = context.copy(
                sideEffectManager = context.sideEffectManager.ensureNoMoreSelectableCatch(notSelectableContext)
            )
        )
}
