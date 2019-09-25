package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
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
                    mainRequest.responseBy(it.copy(context = resetCutWorkChanges(it.context!!, mainRequest.context)))
                }

            else -> throw TypeError(
                    message = "call/1 argument is neither a Variable nor a well-formed goal",
                    context = mainRequest.context,
                    expectedType = TypeError.Expected.CALLABLE,
                    actualValue = toBeCalledGoal
            )
        }
    }

    /**
     * Utility method to reset [Cut] changed data structures to initial value before exiting [Call] scope
     *
     * That implements expected ISO behaviour, for which *call/1 is said to be opaque (or not transparent) to cut.*
     */
    private fun resetCutWorkChanges(subResponseContext: ExecutionContextImpl, toRecover: ExecutionContextImpl) =
            // opaque behaviour of call/1 w.r.t cut/0, results in cancellation of sub-goal work onto "cut"'s data structures
            subResponseContext.copy(toCutContextsParent = toRecover.toCutContextsParent)
}