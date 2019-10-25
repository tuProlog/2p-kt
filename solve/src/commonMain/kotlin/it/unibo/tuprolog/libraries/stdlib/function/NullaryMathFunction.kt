package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.function.NullaryFunction
import it.unibo.tuprolog.primitive.function.PrologFunction
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class to implement nullary math functions (so called math constants)
 *
 * @param name the math constant name
 *
 * @author Enrico
 */
abstract class NullaryMathFunction(name: String) : MathFunction(name, 0) {

    override val function: NullaryFunction<Term> = PrologFunction.ofNullary { context -> mathConstant(context) }

    /** The actual math constant implementation */
    protected abstract fun mathConstant(context: ExecutionContext): Numeric
}
