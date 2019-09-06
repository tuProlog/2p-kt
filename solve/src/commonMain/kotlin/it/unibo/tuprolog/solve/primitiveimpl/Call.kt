package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
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
                ).map { mainRequest.responseBy(it) }
            else -> TODO("throw type_error(callable, G)")
        }
    }
}