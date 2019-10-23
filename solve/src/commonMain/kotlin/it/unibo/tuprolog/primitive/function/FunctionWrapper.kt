package it.unibo.tuprolog.primitive.function

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.AbstractWrapper
import it.unibo.tuprolog.primitive.Signature

/**
 * A class wrapping a [PrologFunction] implementation
 *
 * @author Enrico
 */
abstract class FunctionWrapper : AbstractWrapper<PrologFunction<Term>> {

    constructor(signature: Signature) : super(signature)
    constructor(name: String, arity: Int, vararg: Boolean = false) : super(name, arity, vararg)

    /** Wrapped function implementation */
    abstract val function: PrologFunction<Term>

    override val descriptionPair: Pair<Signature, PrologFunction<Term>> by lazy { signature to function }
}
