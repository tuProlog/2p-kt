package it.unibo.tuprolog.solve.streams.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.ErrorUtils
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

/**
 * Implementation of primitive handling `throw/1` behaviour
 *
 * @author Enrico
 */
internal object Throw : PrimitiveWrapper<StreamsExecutionContext>("throw", 1) {

    override fun uncheckedImplementation(request: Solve.Request<StreamsExecutionContext>): Sequence<Solve.Response> =
        try {
            request.ensuringAllArgumentsAreInstantiated().arguments.single().freshCopy().let { throwArgument ->
                val ancestorCatch = request.context.sideEffectManager.retrieveAncestorCatchRequest(throwArgument)

                when (val catcherUnifyingSubstitution = ancestorCatch?.arguments?.get(1)?.mguWith(throwArgument)) {
                    // matching catch found, it will handle exception
                    is Substitution.Unifier -> sequenceOf(
                        with(request) {
                            val newSubstitution =
                                (context.substitution + catcherUnifyingSubstitution) as Substitution.Unifier

                            replySuccess(
                                newSubstitution,
                                sideEffectManager = context.sideEffectManager.throwCut(ancestorCatch.context)
                            )
                        }
                    )

                    // no catch found that can handle thrown exception
                    else -> {
                        val errorCause = throwArgument.extractErrorCauseChain(request.context)
                        when {
                            // if unhandled error is a LogicError, rethrow outside
                            errorCause != null -> throw errorCause

                            // if current unhandled exception is some other error, launch it as message
                            else -> throw SystemError.forUncaughtException(request.context, throwArgument)
                        }
                    }
                }
            }
        } catch (logicError: LogicError) {
            sequenceOf(request.replyException(logicError))
        }

    /** Utility function to extract error type from a term that should be `error(TYPE_STRUCT, ...)` */
    private fun Term.extractErrorTypeAndExtra() = with(this as? Struct) {
        when {
            this?.functor == ErrorUtils.errorWrapperFunctor && this.arity == 2 ->
                (this.args.firstOrNull() as? Struct)?.let { Pair(it, this.args[1]) }
            else -> null
        }
    }

    /** Utility function to extract error, with filled cause field, till the error root */
    private fun Term.extractErrorCauseChain(withContext: ExecutionContext): LogicError? =
        extractErrorTypeAndExtra()?.let { (type, extra) ->
            LogicError.of(
                context = withContext,
                type = type,
                extraData = extra,
                cause = extra.extractErrorCauseChain(withContext)
            )
        }
}
