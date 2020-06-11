package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.solve.ExecutionContext

/** A base class to implement predicates with one argument */
abstract class UnaryPredicate<E : ExecutionContext>(operator: String) : PrimitiveWrapper<E>(operator, 1)
