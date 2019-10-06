package it.unibo.tuprolog.solve.fsm

import kotlin.js.Date

actual fun currentTimeMillis(): Long {
    return Date.now().toLong()
}