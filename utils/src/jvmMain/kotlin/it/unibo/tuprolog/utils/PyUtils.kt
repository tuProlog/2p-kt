@file:JvmName("PyUtils")

package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.IterableWrapper
import it.unibo.tuprolog.utils.impl.IteratorWrapper
import it.unibo.tuprolog.utils.impl.SequenceWrapper
import kotlin.jvm.JvmName

fun <T> iterator(iterator: Iterator<T>): MutableIterator<T> = IteratorWrapper(iterator)

fun <T> iterable(iterable: Iterable<T>): MutableIterable<T> = IterableWrapper(iterable)

fun <T> iterable(sequence: Sequence<T>): MutableIterable<T> = SequenceWrapper(sequence)
