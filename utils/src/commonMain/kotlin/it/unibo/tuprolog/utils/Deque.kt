@file:JvmName("Deque")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName

/**
 * Creates a new, mutable double-ended queue (deque) containing the given [items], in order.
 *
 * The result is exposed as a plain [MutableList] for API convenience (e.g. random access), but each
 * platform backs it with a data structure efficient at both ends (e.g. `java.util.LinkedList` on the JVM),
 * so that [addFirst] and [takeFirst] are cheap; use it wherever elements are pushed/popped from the front
 * as well as the back, such as the traversal fringe of [it.unibo.tuprolog.utils.graphs.AbstractSearchStrategy].
 */
expect fun <T> dequeOf(vararg items: T): MutableList<T>

/**
 * Creates a new, mutable [dequeOf] deque containing the elements of [items], in iteration order.
 */
expect fun <T> dequeOf(items: Iterable<T>): MutableList<T>

/**
 * Creates a new, mutable [dequeOf] deque containing the elements of [items], in iteration order.
 */
expect fun <T> dequeOf(items: Sequence<T>): MutableList<T>

/**
 * Inserts [item] at the head of this list, shifting all other elements one position towards the tail.
 * Named after (and, on platforms with one, backed by) the "add first" operation of a proper deque.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
expect fun <T> MutableList<T>.addFirst(item: T)

/**
 * Inserts all the elements of [items], in iteration order, at the head of this list.
 */
expect fun <T> MutableList<T>.addFirst(items: Iterable<T>)

/**
 * Inserts all the elements of [items], in iteration order, at the head of this list.
 */
expect fun <T> MutableList<T>.addFirst(items: Sequence<T>)

/**
 * Removes and returns the first element of this list, or `null` if the list is empty.
 */
expect fun <T> MutableList<T>.takeFirst(): T?
