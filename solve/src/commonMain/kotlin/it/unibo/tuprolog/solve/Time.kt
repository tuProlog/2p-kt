@file:JvmName("Time")

package it.unibo.tuprolog.solve

import kotlin.js.JsName
import kotlin.jvm.JvmName

/** This type represents how the solver will see time instants */
typealias TimeInstant = Long

/** This type represents how the solver will see time duration */
typealias TimeDuration = Long

/** A function returning current Time instant */
@JsName("currentTimeInstant")
expect fun currentTimeInstant(): TimeInstant

enum class TimeUnit(private val millis: Long) {
    MILLIS(1L),
    SECONDS(1000L),
    MINUTES(60L),
    HOURS(60L),
    DAYS(24),
    WEEKS(7),
    YEARS(365);

    @JvmName("toDuration")
    fun toDuration(): TimeDuration =
        (sequenceOf(MILLIS, SECONDS, MINUTES, HOURS, DAYS) + if (this == YEARS) YEARS else WEEKS)
            .takeWhile { it.ordinal <= this.ordinal }
            .map { it.millis }
            .reduce(TimeDuration::times)
}

operator fun TimeDuration.times(unit: TimeUnit): TimeDuration = this * unit.toDuration()

operator fun Int.times(unit: TimeUnit): TimeDuration = this.toLong() * unit
