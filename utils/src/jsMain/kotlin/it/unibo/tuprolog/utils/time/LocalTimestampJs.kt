package it.unibo.tuprolog.utils.time

import kotlin.js.Date

/** JavaScript local wall-clock timestamp. */
actual fun formatLocalTimestamp(epochMilliseconds: Long): String {
    val date = Date(epochMilliseconds.toDouble())

    fun pad(value: Int) = value.toString().padStart(2, '0')
    return "${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} " +
        "${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}"
}
