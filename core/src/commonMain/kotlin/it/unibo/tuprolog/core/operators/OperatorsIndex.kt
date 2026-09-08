@file:JvmName("OperatorsIndex")

package it.unibo.tuprolog.core.operators

import kotlin.jvm.JvmName

/** A lookup structure mapping an [Operator.functor] to its [Operator.priority], indexed by [Operator.specifier]. */
typealias OperatorsIndex = Map<String, Map<Specifier, Int>>

/** Builds an [OperatorsIndex] out of this collection of [Operator]s, for fast functor/specifier lookups. */
fun Iterable<Operator>.toOperatorsIndex(): OperatorsIndex {
    val temp: MutableMap<String, MutableMap<Specifier, Int>> = mutableMapOf()
    for (op in this) {
        if (op.functor in temp) {
            val opsWithFunctor = temp[op.functor]!!
            opsWithFunctor[op.specifier] = op.priority
        } else {
            temp[op.functor] = mutableMapOf(op.specifier to op.priority)
        }
    }
    return temp
}

/** @see Iterable.toOperatorsIndex */
fun Sequence<Operator>.toOperatorsIndex(): OperatorsIndex = this.asIterable().toOperatorsIndex()
