package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.Result

object InvokeStrict : AbstractInvoke("strict") {
    override fun Result.Value.getInvocationResult(): Term = asObjectRef()
}
