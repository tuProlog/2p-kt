package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.solver.Solver
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
        val toBeCalledGoal = mainRequest.arguments.single()
        when {
            toBeCalledGoal is Var -> TODO("throw instantiation_error")
            SolverUtils.isWellFormed(toBeCalledGoal) ->
                Solver.sld().solve(
                        mainRequest.newSolveRequest(toBeCalledGoal as Struct)
                ).map {
                    mainRequest.responseBy(it.copy(context = resetCutWorkChanges(it.context, mainRequest.context)))
                }
            else -> TODO("throw type_error(callable, G)")
        }
    }

    /**
     * Utility method to reset [Cut] changed data structures to initial value before exiting [Call] scope
     *
     * That implements expected ISO behaviour, for which *call/1 is said to be opaque (or not transparent) to cut.*
     */
    private fun resetCutWorkChanges(subResponseContext: ExecutionContext, toRecover: ExecutionContext) =
            // opaque behaviour of call/1 w.r.t cut/0, results in cancellation of sub-goal work onto "cut"'s data structures
            subResponseContext.copy(toCutContextsParent = toRecover.toCutContextsParent)
}