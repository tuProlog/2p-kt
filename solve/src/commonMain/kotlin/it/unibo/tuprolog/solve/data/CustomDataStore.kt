package it.unibo.tuprolog.solve.data

import kotlin.js.JsName
import kotlin.jvm.JvmStatic

data class CustomDataStore(
    @JsName("persistent")
    val persistent: CustomData = emptyMap(),
    @JsName("durable")
    val durable: CustomData = emptyMap(),
    @JsName("ephemeral")
    val ephemeral: CustomData = emptyMap(),
) {
    companion object {
        @JsName("empty")
        @JvmStatic
        fun empty(): CustomDataStore = CustomDataStore()
    }

    @JsName("discardEphemeral")
    fun discardEphemeral(): CustomDataStore = copy(ephemeral = emptyMap())

    @JsName("preservePersistent")
    fun preservePersistent(): CustomDataStore = copy(durable = emptyMap(), ephemeral = emptyMap())
}
