package it.unibo.tuprolog.unify

import kotlin.js.JsName

/**
 * Marks a type that carries its own [Unificator], to be used whenever unification is needed in that context
 * (e.g. an execution/solving context choosing which unification strategy governs its resolution).
 */
interface UnificationAware {
    /** The [Unificator] associated with this object. */
    @JsName("unificator")
    val unificator: Unificator
}
