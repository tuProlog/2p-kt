package it.unibo.tuprolog.primitive.function

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Interface defining Prolog functions that return [Term] instances or its subclasses
 *
 * @author Enrico
 */
interface PrologFunction<out R : Term> : Function<R> {

    companion object {

        /** Creates a prolog function from provided lambda */
        inline fun ofNullary(crossinline nullaryFunction: (ExecutionContext) -> Term): NullaryFunction<Term> = object : NullaryFunction<Term> {
            override fun invoke(context: ExecutionContext): Term = nullaryFunction(context)
        }

        /** Creates a prolog function from provided lambda */
        inline fun ofUnary(crossinline unaryFunction: (Term, ExecutionContext) -> Term): UnaryFunction<Term> = object : UnaryFunction<Term> {
            override fun invoke(input: Term, context: ExecutionContext): Term = unaryFunction(input, context)
        }

        /** Creates a prolog function from provided lambda */
        inline fun ofBinary(crossinline binaryFunction: (Term, Term, ExecutionContext) -> Term): BinaryFunction<Term> = object : BinaryFunction<Term> {
            override fun invoke(input1: Term, input2: Term, context: ExecutionContext): Term = binaryFunction(input1, input2, context)
        }
    }
}

/**
 * Interface defining *nullary* Prolog functions that return a [Term] in output
 *
 * @author Enrico
 */
interface NullaryFunction<out R : Term> : PrologFunction<R> {
    /** Invokes the function, passing the context if necessary. */
    operator fun invoke(context: ExecutionContext): R
}

/**
 * Interface defining *unary* Prolog functions that take a [Term] in input and return a [Term] in output
 *
 * @author Enrico
 */
interface UnaryFunction<out R : Term> : PrologFunction<R> {
    /** Invokes the function with the specified argument, and optionally a context if necessary. */
    operator fun invoke(input: Term, context: ExecutionContext): R
}

/**
 * Interface defining *binary* Prolog functions that take two [Term]s in input and return a [Term] in output
 *
 * @author Enrico
 */
interface BinaryFunction<out R : Term> : PrologFunction<R> {
    /** Invokes the function with the specified argument. */
    operator fun invoke(input1: Term, input2: Term, context: ExecutionContext): R
}
