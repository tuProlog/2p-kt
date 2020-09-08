package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.extractSignature


abstract class AbstractEvaluator<T : Term>(private val context: ExecutionContext) : TermVisitor<T> {

    /** Shorthand to access context loaded functions */
    @Suppress("MemberVisibilityCanBePrivate")
    protected val loadedFunctions by lazy { context.libraries.functions }

    @Suppress("UNCHECKED_CAST")
    private inline fun casting(f: () -> Term): T = f() as T

    override fun defaultValue(term: Term): T = casting { term }

    override fun visit(term: Term): T =
        super.visit(term.apply { staticCheck(context) })

    override fun visitAtom(term: Atom): T = casting {
        loadedFunctions[term.extractSignature()]
            ?.let { it(Compute.Request(term.extractSignature(), term.argsList, context)).result }
            ?: term
    }

    override fun visitStruct(term: Struct): T = casting {
        loadedFunctions[term.extractSignature()]
            ?.let {
                it(
                    Compute.Request(
                        term.extractSignature(),
                        term.argsSequence.map { arg -> arg.accept(this).apply { dynamicCheck(term, context) } }
                            .toList(),
                        context
                    )
                ).result
            }
            ?: term
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
     * This is a stub implementation, that does nothing
     */
    protected open fun Term.dynamicCheck(enclosingTerm: Struct, context: ExecutionContext): Unit = Unit
}
