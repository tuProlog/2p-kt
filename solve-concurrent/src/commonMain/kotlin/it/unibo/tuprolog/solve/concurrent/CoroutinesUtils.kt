@file:JvmName("CoroutinesUtils")

package it.unibo.tuprolog.solve.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.jvm.JvmName

internal object PoisonPill

internal expect val backgroundScope: CoroutineScope

expect fun createScope(): CoroutineScope

expect fun closeExecution()

expect fun <T> ReceiveChannel<T>.toSequence(coroutineScope: CoroutineScope = backgroundScope): Sequence<T>
