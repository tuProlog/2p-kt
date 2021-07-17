package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.solve.AbstractWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature

/**
 * A class wrapping a [LogicFunction] implementation
 *
 * @author Enrico
 */
abstract class FunctionWrapper<C : ExecutionContext> : AbstractWrapper<LogicFunction> {

    constructor(signature: Signature) : super(signature)
    constructor(name: String, arity: Int, vararg: Boolean = false) : super(name, arity, vararg)

    /** The function expressing the implementation of the PrologFunction, without any check for application to correct signature */
    protected abstract fun uncheckedImplementation(request: Compute.Request<C>): Compute.Response

    /** Checked PrologFunction implementation */
    @Suppress("UNCHECKED_CAST")
    final override val wrappedImplementation: LogicFunction by lazy {
        functionOf(signature, ::uncheckedImplementation as LogicFunction)
    }
}
