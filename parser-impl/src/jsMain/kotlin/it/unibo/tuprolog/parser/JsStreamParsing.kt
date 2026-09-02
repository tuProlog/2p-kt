package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTables
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.asDynamic

/**
 * Opens a UTF-8 suspending parse session over a browser `File` or `Blob`.
 *
 * The object must provide `stream()`; a file name is used as [sourceId] when available. Closing the
 * returned session cancels unfinished reads and releases the stream reader lock.
 *
 * @throws IllegalArgumentException if [maximumRetainedTokens] is not positive
 */
fun PrologParser.openFileSession(
    file: Any,
    initialOperators: OperatorTable = OperatorTables.empty(),
    sourceId: String? = file.asDynamic().name as? String,
    maximumRetainedTokens: Int? = null,
): SuspendingPrologParseSession =
    openSession(
        JsReadableStreamTextChunkSource(file.asDynamic().stream()),
        sourceId,
        initialOperators,
        maximumRetainedTokens,
    )

/**
 * Opens a UTF-8 suspending parse session over a JavaScript `ReadableStream<Uint8Array>`.
 *
 * Multi-byte sequences split across stream chunks are decoded incrementally. Closing the returned
 * session cancels unfinished reads and releases the stream reader lock.
 *
 * @throws IllegalArgumentException if [maximumRetainedTokens] is not positive
 */
fun PrologParser.openReadableStreamSession(
    stream: Any,
    initialOperators: OperatorTable = OperatorTables.empty(),
    sourceId: String? = null,
    maximumRetainedTokens: Int? = null,
): SuspendingPrologParseSession =
    openSession(JsReadableStreamTextChunkSource(stream), sourceId, initialOperators, maximumRetainedTokens)

internal suspend fun awaitPromise(promise: dynamic): Any? =
    suspendCoroutine { continuation ->
        promise.then(
            { value: Any? -> continuation.resume(value) },
            { reason: Any? -> continuation.resumeWithException(reason.asThrowable()) },
        )
    }

internal fun Any?.asThrowable(): Throwable {
    if (this is Throwable) {
        return this
    }
    val message = this?.asDynamic()?.message as? String ?: toString()
    return RuntimeException(message)
}
