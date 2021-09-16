@file:JvmName("CoroutinesUtils")

package it.unibo.tuprolog.solve.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmName

internal object PoisonPill

internal expect val backgroundScope: CoroutineScope

expect fun <T> ReceiveChannel<T>.toSequence(coroutineScope: CoroutineScope = backgroundScope): Sequence<T>

expect fun <T> Flow<T>.toSequence(coroutineScope: CoroutineScope = backgroundScope): Sequence<T>
