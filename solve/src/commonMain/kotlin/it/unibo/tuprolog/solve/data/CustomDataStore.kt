package it.unibo.tuprolog.solve.data

import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * The [CustomData] attached to an [it.unibo.tuprolog.solve.ExecutionContext] (see
 * [it.unibo.tuprolog.solve.ExecutionContext.customData]), split into three tiers by how long an entry is expected
 * to survive, from longest- to shortest-lived: [persistent], [durable], and [ephemeral]. Resolution strategies
 * decide when/how often each tier gets pruned (see [discardEphemeral]/[preservePersistent]); e.g. ephemeral data is
 * typically discarded more eagerly (across backtracking or per solving step) than durable or persistent data.
 */
data class CustomDataStore(
    /** Data expected to survive for as long as the [it.unibo.tuprolog.solve.Solver] itself, e.g. across queries. */
    @JsName("persistent")
    val persistent: CustomData = emptyMap(),
    /** Data expected to survive longer than [ephemeral], but not necessarily as long as [persistent]. */
    @JsName("durable")
    val durable: CustomData = emptyMap(),
    /** Short-lived data, the first tier to be discarded (see [discardEphemeral]). */
    @JsName("ephemeral")
    val ephemeral: CustomData = emptyMap(),
) {
    companion object {
        /** The empty [CustomDataStore], with no entry in any tier. */
        @JsName("empty")
        @JvmStatic
        fun empty(): CustomDataStore = CustomDataStore()
    }

    /** Returns a copy of this store with [ephemeral] cleared, keeping [persistent] and [durable] as-is. */
    @JsName("discardEphemeral")
    fun discardEphemeral(): CustomDataStore = copy(ephemeral = emptyMap())

    /** Returns a copy of this store with both [durable] and [ephemeral] cleared, keeping only [persistent]. */
    @JsName("preservePersistent")
    fun preservePersistent(): CustomDataStore = copy(durable = emptyMap(), ephemeral = emptyMap())
}
