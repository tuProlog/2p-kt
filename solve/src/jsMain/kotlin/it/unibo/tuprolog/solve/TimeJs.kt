package it.unibo.tuprolog.solve

import kotlin.js.Date

/** A function returning current Time instant */
@JsName("currentTimeInstant")
actual fun currentTimeInstant(): TimeInstant = Date.now().toLong()
