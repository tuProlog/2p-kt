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
