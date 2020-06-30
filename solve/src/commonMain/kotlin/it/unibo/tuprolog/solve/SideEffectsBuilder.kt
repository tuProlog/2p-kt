package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.impl.SideEffectsBuilderImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface SideEffectsBuilder : SideEffectFactory {

    val sideEffects: MutableList<SideEffect>

    fun buildArray(): Array<SideEffect> = sideEffects.toTypedArray()

    fun build(): List<SideEffect> = sideEffects.toList()

    companion object {
        @JvmStatic
        @JsName("empty")
        fun empty(): SideEffectsBuilder =
            SideEffectsBuilderImpl(mutableListOf())

        @JvmStatic
        @JsName("ofIterable")
        fun of(sideEffects: Iterable<SideEffect>): SideEffectsBuilder =
            SideEffectsBuilderImpl(sideEffects.toMutableList())

        @JvmStatic
        @JsName("ofSequence")
        fun of(sideEffects: Sequence<SideEffect>): SideEffectsBuilder =
            SideEffectsBuilderImpl(sideEffects.toMutableList())

        @JvmStatic
        @JsName("of")
        fun of(vararg sideEffects: SideEffect): SideEffectsBuilder =
            SideEffectsBuilderImpl(mutableListOf(*sideEffects))
    }
}