package it.unibo.tuprolog.solve.sideffects

import it.unibo.tuprolog.solve.sideffects.impl.SideEffectsBuilderImpl
import it.unibo.tuprolog.utils.dequeOf
import kotlin.jvm.JvmStatic

/**
 * A [SideEffectFactory] that also accumulates every [SideEffect] it creates into [sideEffects], so a primitive can
 * build up a list of side effects imperatively (calling factory methods for their effect on this builder) and then
 * retrieve the whole list via [build]. This is exactly what
 * [it.unibo.tuprolog.solve.primitive.Solve.Request.replyWith]'s `buildSideEffects: SideEffectsBuilder.() -> Unit`
 * overloads run against.
 */
interface SideEffectsBuilder : SideEffectFactory {
    /** The [SideEffect]s accumulated by this builder so far, in creation order. */
    val sideEffects: MutableList<SideEffect>

    /** Same as [build], but as an [Array]. */
    fun buildArray(): Array<SideEffect> = sideEffects.toTypedArray()

    /** Returns a snapshot [List] of the [SideEffect]s accumulated so far. */
    fun build(): List<SideEffect> = sideEffects.toList()

    companion object {
        /** Creates a new, empty [SideEffectsBuilder]. */
        @JvmStatic
        fun empty(): SideEffectsBuilder = SideEffectsBuilderImpl(dequeOf())

        /** Creates a [SideEffectsBuilder] pre-seeded with [sideEffects]. */
        @JvmStatic
        fun of(sideEffects: Iterable<SideEffect>): SideEffectsBuilder = SideEffectsBuilderImpl(dequeOf(sideEffects))

        /** Creates a [SideEffectsBuilder] pre-seeded with [sideEffects]. */
        @JvmStatic
        fun of(sideEffects: Sequence<SideEffect>): SideEffectsBuilder = SideEffectsBuilderImpl(dequeOf(sideEffects))

        /** Creates a [SideEffectsBuilder] pre-seeded with [sideEffects]. */
        @JvmStatic
        fun of(vararg sideEffects: SideEffect): SideEffectsBuilder = SideEffectsBuilderImpl(dequeOf(*sideEffects))
    }
}
