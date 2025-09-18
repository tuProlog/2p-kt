package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve

abstract class AbstractEvaluator<E : ExecutionContext, T : Term>(
    protected val request: Solve.Request<E>,
    protected val index: Int?,
) : TermVisitor<T> {
    /** Shorthand to access context loaded functions */
    @Suppress("MemberVisibilityCanBePrivate")
    protected val loadedFunctions by lazy { request.context.libraries.functions }

    @Suppress("UNCHECKED_CAST")
    private inline fun casting(f: () -> Term): T = f() as T

    override fun defaultValue(term: Term): T = casting { term }

    override fun visitTerm(term: Term): T = defaultValue(term.apply { staticCheck() })

    override fun visitAtom(term: Atom): T =
        casting {
            visitStruct(term)
        }

    override fun visitIndicator(term: Indicator): T =
        casting {
            visitStruct(term)
        }

    override fun visitStruct(term: Struct): T =
        casting {
            val functionSignature = term.extractSignature()
            loadedFunctions[functionSignature]?.let { function ->
                function
                    .compute(
                        Compute.Request(
                            functionSignature,
                            term.argsSequence.map { it.accept(this).apply { dynamicCheck(term) } }.toList(),
                            request.context,
                        ),
                    ).result
            } ?: unevaluable(term)
        }

    open fun unevaluable(struct: Struct): Term =
        throw TypeError.forArgument(request.context, request.signature, TypeError.Expected.EVALUABLE, struct, index)

    /**
     * Template method to implement static checks, i.e. those checks that can be made before evaluating sub-expressions
     *
     * This is a stub implementation, that does nothing
     */
    protected open fun Term.staticCheck(): Unit = Unit

    /**
     * Template method to implement dynamic checks, i.e. those checks that must be made after sub-expression evaluation, on its result
     *
     * This is a stub implementation, that does nothing
     */
    protected open fun Term.dynamicCheck(enclosingTerm: Struct): Unit = Unit
}
