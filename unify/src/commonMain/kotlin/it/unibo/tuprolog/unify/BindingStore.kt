package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.js.JsName

/**
 * Mutable variable bindings that an [IncrementalUnificator] reads and extends, instead of building a [Substitution]
 * (#1010). Bindings are *triangular*: a bound term may contain bound variables, and a variable may be bound to another
 * one. Typically implemented by a solver over its own bindings, e.g. recording each binding on a trail to undo it on
 * backtracking.
 */
interface BindingStore {
    /** The term [variable] is bound to, or `null` if it is unbound. */
    @JsName("get")
    operator fun get(variable: Var): Term?

    /** Binds the unbound [variable] to [term]. Never called on a bound variable, nor to rebind one. */
    @JsName("bind")
    fun bind(
        variable: Var,
        term: Term,
    )
}
