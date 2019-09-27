package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.SolverUtils
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.responseBy

/**
 * Implementation of primitive handling `call/1` behaviour
 *
 * @author Enrico
 */
object Call : PrimitiveWrapper(Signature("call", 1)) {

    override val uncheckedImplementation: Primitive = { mainRequest ->
        // TODO: 25/09/2019 remove that
        mainRequest as Solve.Request<ExecutionContextImpl>

        val toBeCalledGoal = mainRequest.arguments.single()
        when {
            toBeCalledGoal is Var -> throw InstantiationError(
                    "call/1 argument should not be a Variable",
                    context = mainRequest.context,
                    extraData = toBeCalledGoal
            )

            SolverUtils.isWellFormed(toBeCalledGoal) ->
                SolverSLD().solve(
                        mainRequest.newSolveRequest(toBeCalledGoal as Struct)
                ).map {
                    mainRequest.responseBy(
                            it.copy(sideEffectManager =
                            (it.sideEffectManager as? SideEffectManagerImpl)
                                    ?.resetCutWorkChanges(mainRequest.context.sideEffectManager)
                                    ?: it.sideEffectManager
                            )
                    )
                }

            else -> throw TypeError(
                    message = "call/1 argument is neither a Variable nor a well-formed goal",
                    context = mainRequest.context,
                    expectedType = TypeError.Expected.CALLABLE,
                    actualValue = toBeCalledGoal
            )
        }
    }
}