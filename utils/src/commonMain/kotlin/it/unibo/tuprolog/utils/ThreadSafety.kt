@file:JvmName("ThreadSafety")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName

expect fun <T> synchronizedOn(
    obj: Any,
    action: () -> T,
): T

fun <T : Any, R> T.synchronizedOnSelf(action: () -> R): R = synchronizedOn(this, action)
