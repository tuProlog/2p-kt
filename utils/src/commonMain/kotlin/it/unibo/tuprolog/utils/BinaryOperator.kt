package it.unibo.tuprolog.utils

/**
 * A function taking two values of type [T] and combining them into a single value of the same type,
 * e.g. an addition, a union, or a merge operation.
 *
 * This is just a readability alias for the common `(T, T) -> T` function shape; it carries no behaviour
 * of its own, but naming the shape makes signatures such as [TagsOperator] self-explanatory.
 */
typealias BinaryOperator<T> = (T, T) -> T
