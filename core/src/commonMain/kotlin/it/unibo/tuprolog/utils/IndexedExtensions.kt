@file:JvmName("IndexedExtensions")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName

fun <T> Sequence<T>.longIndexed(): Sequence<LongIndexed<T>> =
    zip(LongRange(0, Long.MAX_VALUE).asSequence()) { it, i ->
        LongIndexed.of(i, it)
    }

fun <T> Sequence<T>.indexed(): Sequence<IntIndexed<T>> =
    zip(IntRange(0, Int.MAX_VALUE).asSequence()) { it, i ->
        IntIndexed.of(i, it)
    }