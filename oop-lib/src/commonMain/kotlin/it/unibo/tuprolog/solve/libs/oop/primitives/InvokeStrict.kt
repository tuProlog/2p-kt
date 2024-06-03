package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.OOPContext
import it.unibo.tuprolog.solve.libs.oop.Result

class InvokeStrict(oopContext: OOPContext) : AbstractInvoke("strict", oopContext) {
    override fun Result.Value.getInvocationResult(): Term = asObjectRef()
}
