package it.unibo.tuprolog.solve.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

// todo clean, old (hopefully useless) implementation
// actual fun <T> ReceiveChannel<T>.toSequence(coroutineScope: CoroutineScope): Sequence<T> =
//    consumeAsFlow().toSequence(coroutineScope)

@OptIn(ExperimentalCoroutinesApi::class)
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

// todo clean, old (hopefully useless) implementation
actual fun <T> Flow<T>.toSequence(coroutineScope: CoroutineScope): Sequence<T> {
    val queue = LinkedBlockingQueue<Any?>()
    coroutineScope.launch {
        collect { queue.add(it) }
        queue.add(PoisonPill)
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

actual fun createScope(): CoroutineScope =
    CoroutineScope(SupervisorJob() + executionContext.asCoroutineDispatcher())

// todo need checks, computation never ends with shorter timeout of executionContext.awaitTermination
actual fun closeExecution() {
    executionContext.awaitTermination(30, TimeUnit.SECONDS)
    executionContext.shutdown()
    backgroundExecutionContext.awaitTermination(2, TimeUnit.SECONDS)
    backgroundExecutionContext.shutdown()
}
