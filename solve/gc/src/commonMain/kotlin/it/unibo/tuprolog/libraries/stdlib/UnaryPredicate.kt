package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.ExecutionContext

abstract class UnaryPredicate(operator: String) : PrimitiveWrapper<ExecutionContext>(operator, 1)
