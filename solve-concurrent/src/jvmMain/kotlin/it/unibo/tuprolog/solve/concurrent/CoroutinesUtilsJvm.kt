package it.unibo.tuprolog.solve.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedBlockingQueue

// todo clean, old (hopefully useless) implementation
//actual fun <T> ReceiveChannel<T>.toSequence(coroutineScope: CoroutineScope): Sequence<T> =
//    consumeAsFlow().toSequence(coroutineScope)

@OptIn(ExperimentalCoroutinesApi::class)
actual fun <T> ReceiveChannel<T>.toSequence(coroutineScope: CoroutineScope): Sequence<T> {
    val queue = LinkedBlockingQueue<Any?>()
    coroutineScope.launch {
        while (! this@toSequence.isClosedForReceive)
            queue.add(receive())
        queue.add(PoisonPill)
    }
    return sequence {
        while (true) {
            val current = queue.take()
            if (current == PoisonPill) {
                break
            } else {
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
                yield(current as T)
            }
        }
    }
}

internal actual val backgroundScope: CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.Default)
