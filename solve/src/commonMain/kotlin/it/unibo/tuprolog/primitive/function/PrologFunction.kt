package it.unibo.tuprolog.primitive.function

import it.unibo.tuprolog.core.Term

/**
 * Interface defining Prolog functions that return [Term] instances or its subclasses
 *
 * @author Enrico
 */
interface PrologFunction<out R : Term> : Function<R> {

    companion object {

        /** Creates a prolog function from provided lambda */
        inline fun ofNullary(crossinline nullaryFunction: () -> Term): NullaryFunction<Term> = object : NullaryFunction<Term> {
            override fun invoke(): Term = nullaryFunction()
        }

        /** Creates a prolog function from provided lambda */
        inline fun ofUnary(crossinline unaryFunction: (Term) -> Term): UnaryFunction<Term> = object : UnaryFunction<Term> {
            override fun invoke(input: Term): Term = unaryFunction(input)
        }

        /** Creates a prolog function from provided lambda */
        inline fun ofBinary(crossinline binaryFunction: (Term, Term) -> Term): BinaryFunction<Term> = object : BinaryFunction<Term> {
            override fun invoke(input1: Term, input2: Term): Term = binaryFunction(input1, input2)
        }
    }
}

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
