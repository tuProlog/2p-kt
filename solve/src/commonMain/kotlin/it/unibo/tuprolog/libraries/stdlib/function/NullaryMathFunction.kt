package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.function.NullaryFunction
import it.unibo.tuprolog.primitive.function.PrologFunction
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class to implement nullary math functions (with no arguments)
 *
 * @param name the no arguments function name
 *
 * @author Enrico
 */
abstract class NullaryMathFunction(name: String) : MathFunction(name, 0) {

    override val function: NullaryFunction<Term> = PrologFunction.ofNullary { context -> mathFunction(context) }

    /** The actual function implementation */
    protected abstract fun mathFunction(context: ExecutionContext): Numeric
}
