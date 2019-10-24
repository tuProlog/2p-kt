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
open class ExpressionEvaluator(protected val context: ExecutionContext) : TermVisitor<Term> {

    /** Shorthand to access context loaded functions */
    protected val loadedFunctions by lazy { context.libraries.functions }

    override fun defaultValue(term: Term): Term = term

    override fun visitAtom(term: Atom): Term = loadedFunctions[term.extractSignature()].let {
        when (it) {
            is NullaryFunction<*> -> it(context)
            else -> term
        }
    }

    override fun visitStruct(term: Struct): Term = loadedFunctions[term.extractSignature()].let {
        when (it) {
            is UnaryFunction<*> -> it(term.args.first().accept(this), context)
            is BinaryFunction<*> -> it(term.args.first().accept(this), term.args.last().accept(this), context)
            else -> term
        }
    }
}
