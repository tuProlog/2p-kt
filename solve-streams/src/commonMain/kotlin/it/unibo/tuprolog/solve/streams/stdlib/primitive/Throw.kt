package it.unibo.tuprolog.solve.streams.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.ErrorUtils
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext

/**
 * Implementation of primitive handling `throw/1` behaviour
 *
 * @author Enrico
 */
internal object Throw : UnaryPredicate.NonBacktrackable<StreamsExecutionContext>("throw") {

    override fun Solve.Request<StreamsExecutionContext>.computeOne(first: Term): Solve.Response =
        try {
            ensuringAllArgumentsAreInstantiated().arguments.single().freshCopy().let { throwArgument ->
                val ancestorCatch = context.sideEffectManager.retrieveAncestorCatchRequest(throwArgument)

                when (val catcher = ancestorCatch?.arguments?.get(1)?.let { mgu(it, throwArgument) }) {
                    // matching catch found, it will handle exception
                    is Substitution.Unifier -> {
                        val newSubstitution = (context.substitution + catcher) as Substitution.Unifier
                        replySuccess(
                            newSubstitution,
                            sideEffectManager = context.sideEffectManager.throwCut(ancestorCatch.context)
                        )
                    }

                    // no catch found that can handle thrown exception
                    else -> {
                        val errorCause = throwArgument.extractErrorCauseChain(context)
                        when {
                            // if unhandled error is a LogicError, rethrow outside
                            errorCause != null -> throw errorCause

                            // if current unhandled exception is some other error, launch it as message
                            else -> throw SystemError.forUncaughtException(context, throwArgument)
                        }
                    }
                }
            }
        } catch (logicError: LogicError) {
            replyException(logicError)
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
