@file:Suppress("NOTHING_TO_INLINE")

package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.forceCast

internal val property: String = AbstractTerm::termMark.name

internal actual inline fun isTerm(any: Any?): Boolean =
    any !== null && (any.asDynamic()[property] == MARK || any is Term)

internal actual inline fun asTerm(any: Any?): Term? =
    if (isTerm(any)) {
        any.forceCast<Term>()
    } else {
        null
    }
