package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.Result

object InvokeMethod : AbstractInvoke("method") {
    override fun Result.Value.getInvocationResult(): Term = toTerm()
}
