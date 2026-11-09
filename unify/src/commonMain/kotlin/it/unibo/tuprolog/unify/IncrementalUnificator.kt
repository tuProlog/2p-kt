package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName

/**
 * A [Unificator] able to unify two terms under the bindings of a [BindingStore], adding the new ones to it, without
 * building a [Substitution] (#1010). Solvers use it, when their [Unificator] implements it, to unify goals with clauses
 * in their own bindings; with any other [Unificator] they use [mgu] and copy its result.
 *
 * The built-in [Unificator.default], [Unificator.strict] and [Unificator.naive] implement it. A custom
 * [AbstractWorklistUnificator] can opt in by delegating to [AbstractWorklistUnificator.unifyIncrementally]: then its
 * [AbstractWorklistUnificator.checkTermsEquality] and [AbstractWorklistUnificator.handleComparison] are honoured on both
 * paths, but [AbstractWorklistUnificator.handleResult] is not called by [unifyInto], as there is no result to handle.
 * So a unificator overriding `handleResult` should not implement this interface.
 */
interface IncrementalUnificator : Unificator {
    /**
     * Unifies [term1] and [term2], with the bindings of [store] and of [context], adding the new bindings to [store].
     * Returns `false` if they do not unify (or occurs-check fails, when [occurCheckEnabled]), leaving the bindings
     * made so far in [store], for the caller to undo.
     */
    @JsName("unifyInto")
    fun unifyInto(
        store: BindingStore,
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean = false,
    ): Boolean
}
