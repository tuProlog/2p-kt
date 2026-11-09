package it.unibo.tuprolog.utils.time

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** JVM local wall-clock timestamp. */
actual fun formatLocalTimestamp(epochMilliseconds: Long): String =
    DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(epochMilliseconds))
