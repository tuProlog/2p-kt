package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve

object GetDurable : AbstractGetData("durable") {
    override val Solve.Request<ExecutionContext>.data: Map<String, Any>
        get() = context.durableData
}