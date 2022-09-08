@file:JvmName("ExceptionUtils")

package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.utils.io.exceptions.IOException
import it.unibo.tuprolog.utils.io.exceptions.InvalidUrlException
import kotlin.jvm.JvmName

fun IOException.toLogicError(context: ExecutionContext): LogicError {
    return SystemError.forUncaughtException(context, this)
}

fun InvalidUrlException.toLogicError(
    context: ExecutionContext,
    signature: Signature,
    culprit: Term,
    index: Int
): LogicError {
    return TypeError.forArgument(context, signature, TypeError.Expected.URL, culprit, index)
}
