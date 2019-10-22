package it.unibo.tuprolog.function

import it.unibo.tuprolog.core.Term

/**
 * Interface defining Prolog functions that return [Term] instances or its subclasses
 *
 * @author Enrico
 */
interface PrologFunction<out R : Term> : Function<R>

/**
 * Interface defining *nullary* Prolog functions that return a [Term] in output
 *
 * @author Enrico
 */
interface NullaryFunction<out R : Term> : PrologFunction<R> {
    /** Invokes the function. */
    operator fun invoke(): R
}

/**
 * Interface defining *unary* Prolog functions that take a [Term] in input and return a [Term] in output
 *
 * @author Enrico
 */
interface UnaryFunction<out R : Term> : PrologFunction<R> {
    /** Invokes the function with the specified argument. */
    operator fun invoke(input: Term): R
}

/**
 * Interface defining *binary* Prolog functions that take two [Term]s in input and return a [Term] in output
 *
 * @author Enrico
 */
interface BinaryFunction<out R : Term> : PrologFunction<R> {
    /** Invokes the function with the specified argument. */
    operator fun invoke(input1: Term, input2: Term): R
}
