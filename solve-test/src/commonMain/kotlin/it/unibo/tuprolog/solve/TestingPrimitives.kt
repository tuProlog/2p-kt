package it.unibo.tuprolog.solve

import it.unibo.tuprolog.primitive.PrimitiveWrapper

object TestingPrimitives {
    val sleep by lazy {
        object : PrimitiveWrapper<ExecutionContext>("sleep", 1) {
            override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> {
                request.ensuringAllArgumentsAreInstantiated()

            }
        }
    }
}