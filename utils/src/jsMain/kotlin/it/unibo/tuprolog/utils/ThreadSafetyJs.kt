package it.unibo.tuprolog.utils

actual fun <T> synchronizedOn(
    obj: Any,
    action: () -> T,
): T = action()
