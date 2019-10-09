package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.prologerror.ErrorUtils
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.unify.Unification.Companion.mguWith

/**
 * Implementation of primitive handling `throw/1` behaviour
 *
 * @author Enrico
 */
internal object Throw : PrimitiveWrapper<ExecutionContextImpl>(Signature("throw", 1)) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> =
            request.ensuringAllArgumentsAreInstantiated()
                    .arguments.single().freshCopy()
                    .let { throwArgument ->

                        val ancestorCatch = (request.context.sideEffectManager as? SideEffectManagerImpl)
                                ?.run { retrieveAncestorCatchRequest(throwArgument) }

                        when (val catcherUnifyingSubstitution = ancestorCatch?.arguments?.get(1)?.mguWith(throwArgument)) {

                            // no catch found that can handle thrown exception
                            null, is Substitution.Fail -> {
                                val errorCause = extractErrorCauseChain(throwArgument, request.context)
                                when {
                                    isSystemErrorStruct(throwArgument) -> throw HaltException(
                                            "Unhandled system_error, halting inferential machine",
                                            errorCause,
                                            request.context
                                    )
                                    // current unhandled exception is some other error
                                    else -> throw SystemError(
                                            "Exception thrown, but no compatible catch/3 found",
                                            errorCause,
                                            request.context,
                                            throwArgument
                                    )
                                }
                            }

                            // catch, to handle exception, found
                            is Substitution.Unifier -> sequenceOf(with(request) {
                                val newSubstitution = (context.substitution + catcherUnifyingSubstitution)
                                        as Substitution.Unifier

                                replySuccess(
                                        newSubstitution,
                                        sideEffectManager = (context.sideEffectManager as? SideEffectManagerImpl)
                                                ?.run { throwCut(ancestorCatch.context) }
                                                ?: context.sideEffectManager
                                )
                            })
                        }
                    }

    /** Utility function to check if throwArgument is an `error(system_error, ...)` */
    private fun isSystemErrorStruct(aTerm: Term) = with(aTerm as? Struct) {
        this?.functor == ErrorUtils.errorWrapperFunctor
                && this.arity == 2
                && (this.args.firstOrNull() as? Atom)?.value == SystemError.typeFunctor
    }

    /** Utility function to extract error type from a term that should be `error(TYPE_STRUCT, ...)` */
    private fun extractErrorTypeAndExtra(aTerm: Term) = with(aTerm as? Struct) {
        when {
            this?.functor == ErrorUtils.errorWrapperFunctor && this.arity == 2 ->
                (this.args.firstOrNull() as? Struct)?.let { Pair(it, this.args[1]) }
            else -> null
        }
    }

    /** Utility function to extract error, with filled cause field, till the error root */
    private fun extractErrorCauseChain(aTerm: Term, context: ExecutionContext): PrologError? =
            extractErrorTypeAndExtra(aTerm)?.let { (type, extra) ->
                PrologError.of(
                        context = context,
                        type = type,
                        extraData = extra,
                        cause = extractErrorCauseChain(extra, context)
                )
            }

}
