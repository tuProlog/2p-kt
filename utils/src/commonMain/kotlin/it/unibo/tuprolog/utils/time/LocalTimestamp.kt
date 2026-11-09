package it.unibo.tuprolog.utils.time

/** Formats Unix epoch milliseconds in the user's local timezone as `yyyy-MM-dd HH:mm:ss`. */
expect fun formatLocalTimestamp(epochMilliseconds: Long): String
