package it.unibo.tuprolog.solve.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow

internal actual val backgroundScope: CoroutineScope = MainScope()

actual fun <T> ReceiveChannel<T>.toSequence(coroutineScope: CoroutineScope): Sequence<T> {
    TODO("Not yet implemented")
}

actual fun <T> Flow<T>.toSequence(coroutineScope: CoroutineScope): Sequence<T> {
    TODO("Not yet implemented")
}

actual fun createScope(): CoroutineScope {
    TODO("Not yet implemented")
}