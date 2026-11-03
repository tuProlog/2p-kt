package it.unibo.tuprolog.unify

import kotlin.js.JsName

/**
 * The top-level [Unificator] operation being solved, as seen by the callbacks of [AbstractWorklistUnificator]: either
 * an [MguRequest] (from [Unificator.mgu], and thus [Unificator.match]/[Unificator.unify]) or a [MergeRequest] (from
 * [Unificator.merge]), carrying the operation's original arguments.
 */
sealed interface UnificationRequest {
    /** Whether occurs-check is enabled for this request. */
    @JsName("occurCheckEnabled")
    val occurCheckEnabled: Boolean
}
