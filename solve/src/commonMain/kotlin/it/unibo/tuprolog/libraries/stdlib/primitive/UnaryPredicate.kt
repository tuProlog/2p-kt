package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.ExecutionContext

/** A base class to implement predicates with one argument */
abstract class UnaryPredicate<E : ExecutionContext>(operator: String) : PrimitiveWrapper<E>(operator, 1)
