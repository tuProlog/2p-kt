package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

abstract class AbstractCollectingPrimitive(
    name: String,
) : TernaryRelation.WithoutSideEffects<ExecutionContext>(name) {
    protected fun Solve.Request<ExecutionContext>.computeIntermediateSolutions(goal: Struct): List<Solution.Yes> =
        buildList {
            for (solution in solve(goal, context.remainingTime)) {
                when {
                    context.remainingTime <= 0 -> {
                        throw TimeOutException(
                            exceededDuration = context.maxDuration,
                            context = context,
                        )
                    }
                    solution.isHalt -> {
                        throw solution.exception!!.pushContext(context)
                    }
                    solution.isNo -> {
                        break
                    }
                    else -> {
                        add(solution.castToYes())
                    }
                }
            }
        }
}
