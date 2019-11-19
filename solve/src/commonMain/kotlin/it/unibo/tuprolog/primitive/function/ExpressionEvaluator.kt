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

    override fun visitAtom(term: Atom): Term = loadedFunctions[term.extractSignature()]
        ?.let { it(Compute.Request(term.extractSignature(), term.argsList, context)).result }
        ?: term

    override fun visitStruct(term: Struct): Term = loadedFunctions[term.extractSignature()]
        ?.let {
            it(
                Compute.Request(
                    term.extractSignature(),
                    term.argsSequence.map { arg -> arg.accept(this).apply { dynamicCheck(term, context) } }.toList(),
                    context
                )
            ).result
        }
        ?: term


    /**
     * Template method to implement static checks, i.e. those checks that can be made before evaluating sub-expressions
     *
     * This is a stub implementation, that does nothing
     */
    protected open fun Term.staticCheck(context: ExecutionContext): Unit = Unit

    /**
     * Template method to implement dynamic checks, i.e. those checks that must be made after sub-expression evaluation, on its result
     *
     * This is a stub implementation, that does nothing
     */
    protected open fun Term.dynamicCheck(enclosingTerm: Struct, context: ExecutionContext): Unit = Unit
}
