package it.unibo.tuprolog.function

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
 * @author Enrico
 */
open class ExpressionEvaluator(context: ExecutionContext) : TermVisitor<Term> {

    protected val loadedFunctions = context.libraries.functions

    override fun defaultValue(term: Term): Term = term

    override fun visitAtom(term: Atom): Term = loadedFunctions[term.extractSignature()].let {
        when (it) {
            is NullaryFunction<*> -> it()
            else -> term
        }
    }

    override fun visitStruct(term: Struct): Term = loadedFunctions[term.extractSignature()].let {
        when (it) {
            is UnaryFunction<*> -> it(term.args.first().accept(this))
            is BinaryFunction<*> -> it(term.args.first().accept(this), term.args.last().accept(this))
            else -> term
        }
    }
}
