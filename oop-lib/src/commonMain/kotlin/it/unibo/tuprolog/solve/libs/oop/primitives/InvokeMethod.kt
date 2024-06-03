package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.OOPContext
import it.unibo.tuprolog.solve.libs.oop.Result

class InvokeMethod(oopContext: OOPContext) : AbstractInvoke("method", oopContext) {
    override fun Result.Value.getInvocationResult(): Term = toTerm()

    init {
        require(signature.arity == ARITY && signature.name == FUNCTOR)
    }

    companion object {
        const val FUNCTOR = "invoke_method"
        const val ARITY = 3
    }
}
