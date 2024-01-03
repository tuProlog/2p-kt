package it.unibo.tuprolog.solve.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

@Suppress("TooGenericExceptionCaught")
actual fun <T> ReceiveChannel<T>.toSequence(coroutineScope: CoroutineScope): Sequence<T> {
    val queue = LinkedBlockingQueue<Any?>()
    coroutineScope.launch {
        val iterator = this@toSequence.iterator()
        try {
            while (iterator.hasNext()) {
                queue.add(iterator.next())
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        } finally {
            queue.add(PoisonPill)
        }
    }
    return sequence {
        while (true) {
            val current = queue.take()
            if (current == PoisonPill) {
                break
            } else {
                @Suppress("UNCHECKED_CAST")
                yield(current as T)
            }
        }
    }
}

private val backgroundExecutionContext = Executors.newCachedThreadPool()
private val executionContext = Executors.newCachedThreadPool()

internal actual val backgroundScope: CoroutineScope =
    CoroutineScope(SupervisorJob() + backgroundExecutionContext.asCoroutineDispatcher())

actual fun createScope(): CoroutineScope = CoroutineScope(SupervisorJob() + executionContext.asCoroutineDispatcher())

@Suppress("MagicNumber")
actual fun closeExecution() {
    // TODO need checks, computation never ends with shorter timeout of executionContext.awaitTermination
    executionContext.awaitTermination(30, TimeUnit.SECONDS)
    executionContext.shutdown()
    backgroundExecutionContext.awaitTermination(2, TimeUnit.SECONDS)
    backgroundExecutionContext.shutdown()
}
