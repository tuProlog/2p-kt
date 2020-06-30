package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.impl.SideEffectsBuilderImpl
import it.unibo.tuprolog.utils.dequeOf
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface SideEffectsBuilder : SideEffectFactory {

    @JsName("sideEffects")
    val sideEffects: MutableList<SideEffect>

    @JsName("buildArray")
    fun buildArray(): Array<SideEffect> = sideEffects.toTypedArray()

    @JsName("build")
    fun build(): List<SideEffect> = sideEffects.toList()

    companion object {
        @JvmStatic
        @JsName("empty")
        fun empty(): SideEffectsBuilder =
            SideEffectsBuilderImpl(dequeOf())

        @JvmStatic
        @JsName("ofIterable")
        fun of(sideEffects: Iterable<SideEffect>): SideEffectsBuilder =
            SideEffectsBuilderImpl(dequeOf(sideEffects))

        @JvmStatic
        @JsName("ofSequence")
        fun of(sideEffects: Sequence<SideEffect>): SideEffectsBuilder =
            SideEffectsBuilderImpl(dequeOf(sideEffects))

        @JvmStatic
        @JsName("of")
        fun of(vararg sideEffects: SideEffect): SideEffectsBuilder =
            SideEffectsBuilderImpl(dequeOf(*sideEffects))
    }
}