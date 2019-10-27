package it.unibo.tuprolog.primitive.function

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * A class implementing a visitor that will evaluate expression terms according to context loaded functions
 *
 * No additional check is implemented at this level
 *
 * @param context the context in which the evaluation should happen
 *
 * @author Enrico
 */
open class ExpressionEvaluator(private val context: ExecutionContext) : TermVisitor<Term> {

    /** Shorthand to access context loaded functions */
    protected val loadedFunctions by lazy { context.libraries.functions }

    override fun defaultValue(term: Term): Term = term

    override fun visit(term: Term): Term =
            super.visit(term.apply { staticCheck(context) })

    override fun visitAtom(term: Atom): Term = loadedFunctions[term.extractSignature()].let {
        when (it) {
            is NullaryFunction<*> -> it(context)
            else -> term
        }
    }

    override fun visitStruct(term: Struct): Term = loadedFunctions[term.extractSignature()].let {
        when (it) {
            is UnaryFunction<*> -> it(
                    term.args.first().accept(this).apply { dynamicCheck(context) },
                    context
            )
            is BinaryFunction<*> -> it(
                    term.args.first().accept(this).apply { dynamicCheck(context) },
                    term.args.last().accept(this).apply { dynamicCheck(context) },
                    context
            )
            else -> term
        }
    }

    /**
     * Template method to implement static checks, i.e. those checks that can be made before evaluating sub-expressions
     *
     * This is a stub implementation, that does nothing
     */
    protected open fun Term.staticCheck(context: ExecutionContext): Unit = Unit

    /**
     * Template method to implement dynamic checks, i.e. those checks that must be made after sub-expression evaluation, on its result
     *
     * * This is a stub implementation, that does nothing
     */
    protected open fun Term.dynamicCheck(context: ExecutionContext): Unit = Unit
}
