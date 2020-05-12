@file:JvmName("IndexedExtensions")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName

fun <T> Sequence<T>.indexed(): Sequence<Indexed<T>> =
    zip(LongRange(0, Long.MAX_VALUE).asSequence()) {
            it, i -> Indexed(i, it)
    }