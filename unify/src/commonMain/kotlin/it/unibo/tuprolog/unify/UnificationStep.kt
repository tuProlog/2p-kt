package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName

/**
 * A pair of [Term]s that an [AbstractWorklistUnificator] is about to compare, handed to
 * [AbstractWorklistUnificator.handleComparison] so that subclasses can observe it or alter its outcome.
 *
 * Orientation is preserved: [lhs] always stems from the left operand of the [request] (the first term of an
 * [MguRequest], or the first substitution of a [MergeRequest]), and [rhs] from the right one. Both are *dereferenced*:
 * a bound variable is replaced by the term it is currently bound to, so a term may show up on the side of the variable
 * it was bound to (e.g. after `f(X, X) = f(a, b)` binds `X` to `a`, the second pair is reported as `a = b`).
 *
 * Unless one of [replace], [accept] or [reject] is called, the pair is unified as usual. If more than one of them is
 * called, the last call wins. A single instance is reused for all the pairs of one [Unificator] call: do not retain it.
 */
interface UnificationStep {
    /** The operation this comparison belongs to. */
    @JsName("request")
    val request: UnificationRequest

    /** The term stemming from the left operand. */
    @JsName("lhs")
    val lhs: Term

    /** The term stemming from the right operand. */
    @JsName("rhs")
    val rhs: Term

    /**
     * Unifies [lhs] and [rhs] in place of the original pair, e.g. to carry tags over to the terms that variables get
     * bound to. Variables in the replacements are dereferenced again.
     */
    @JsName("replace")
    fun replace(
        lhs: Term = this.lhs,
        rhs: Term = this.rhs,
    )

    /** Treats the pair as already unified: no variable gets bound, and compound terms are not decomposed. */
    @JsName("accept")
    fun accept()

    /** Treats the pair as a contradiction, making the whole operation fail. */
    @JsName("reject")
    fun reject()
}
