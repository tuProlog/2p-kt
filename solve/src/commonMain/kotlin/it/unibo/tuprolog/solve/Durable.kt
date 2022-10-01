package it.unibo.tuprolog.solve

import kotlin.js.JsName

interface Durable {
    @JsName("startTime")
    val startTime: TimeInstant

    @JsName("endTime")
    val endTime: TimeInstant
        get() = (startTime + maxDuration).let { if (it < 0L) TimeInstant.MAX_VALUE else it }

    @JsName("remainingTime")
    val remainingTime: TimeDuration
        get() = endTime - currentTimeInstant()

    @JsName("elapsedTime")
    val elapsedTime: TimeDuration
        get() = currentTimeInstant() - startTime

    @JsName("maxDuration")
    val maxDuration: TimeDuration
}
