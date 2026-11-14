package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

abstract class AbstractCollectingPrimitive(
    name: String,
) : TernaryRelation.WithoutSideEffects<ExecutionContext>(name) {
    /**
     * Collects every solution of [goal], solved in a nested resolution bounded by this request's deadline (see
     * [Solve.Request.solve]). If that times out, so does this resolution: the [TimeOutException] is reported against
     * this request's [context], as if expiration had been detected here.
     */
    protected fun Solve.Request<ExecutionContext>.computeIntermediateSolutions(goal: Compound): List<Solution.Yes> =
        buildList {
            for (solution in solve(goal)) {
                when {
                    solution.exception is TimeOutException || context.chronometer.isExpired -> {
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
