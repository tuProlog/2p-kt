package it.unibo.tuprolog.solve.sideffects

import it.unibo.tuprolog.solve.sideffects.impl.SideEffectsBuilderImpl
import it.unibo.tuprolog.utils.dequeOf
import kotlin.jvm.JvmStatic

interface SideEffectsBuilder : SideEffectFactory {

    val sideEffects: MutableList<SideEffect>

    fun buildArray(): Array<SideEffect> = sideEffects.toTypedArray()

    fun build(): List<SideEffect> = sideEffects.toList()

    companion object {
        @JvmStatic
        fun empty(): SideEffectsBuilder =
            SideEffectsBuilderImpl(dequeOf())

        @JvmStatic
        fun of(sideEffects: Iterable<SideEffect>): SideEffectsBuilder =
            SideEffectsBuilderImpl(dequeOf(sideEffects))

        @JvmStatic
        fun of(sideEffects: Sequence<SideEffect>): SideEffectsBuilder =
            SideEffectsBuilderImpl(dequeOf(sideEffects))

        @JvmStatic
        fun of(vararg sideEffects: SideEffect): SideEffectsBuilder =
            SideEffectsBuilderImpl(dequeOf(*sideEffects))
    }
}
