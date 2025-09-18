package it.unibo.tuprolog.solve.streams.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.StreamsSolver
import it.unibo.tuprolog.solve.streams.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.isSelectedThrowCatch
import it.unibo.tuprolog.solve.streams.solver.newSolveRequest
import it.unibo.tuprolog.solve.streams.solver.replyWith

/**
 * Implementation of primitive handling `catch/3` behaviour
 *
 * @author Enrico
 */
internal object Catch : PrimitiveWrapper<StreamsExecutionContext>("catch", 3) {
    override fun uncheckedImplementation(request: Solve.Request<StreamsExecutionContext>): Sequence<Solve.Response> =
        sequence {
            val goalArgument = request.arguments.first()

            StreamsSolver.solveToResponses(request.newSolveRequest(call(goalArgument))).forEach { goalResponse ->
                when {
                    // if i'm the catch selected by throw/1 primitive
                    goalResponse.sideEffectManager.isSelectedThrowCatch(request.context) -> {
                        val recoverGoalArgument = request.arguments.last().apply(goalResponse.solution.substitution)

                        // attaching recover goal's solve request to catch/3 parent, to not re-execute this catch/3 if error thrown
                        val recoverGoalSolveRequest =
                            request
                                .newSolveRequest(
                                    call(recoverGoalArgument),
                                    goalResponse.solution.substitution - request.context.substitution,
                                    requestIssuingInstant = currentTimeInstant(),
                                ).ensureNoMoreSelectableCatch(request.context)

                        yieldAll(StreamsSolver.solveToResponses(recoverGoalSolveRequest).map { request.replyWith(it) })
                    }
                    else -> yield(request.replyWith(goalResponse))
                }
            }
        }

    /** Utility function to create the struct: call([goal]) */
    private fun call(goal: Term) = Struct.of(Call.functor, goal)

    /** Calls [SideEffectManagerImpl.ensureNoMoreSelectableCatch] */
    private fun Solve.Request<StreamsExecutionContext>.ensureNoMoreSelectableCatch(
        notSelectableContext: StreamsExecutionContext,
    ) = copy(
        context =
            context.copy(
                sideEffectManager = context.sideEffectManager.ensureNoMoreSelectableCatch(notSelectableContext),
            ),
    )
}
