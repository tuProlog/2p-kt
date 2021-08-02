package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Term

internal const val MARK: Int = -1368133982

internal expect inline fun isTerm(any: Any?): Boolean

internal expect inline fun asTerm(any: Any?): Term?
