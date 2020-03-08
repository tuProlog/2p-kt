@file:JvmName("OperatorsIndex")

package it.unibo.tuprolog.core.operators

import kotlin.jvm.JvmName

typealias OperatorsIndex = Map<String, Map<Specifier, Int>>

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

fun Sequence<Operator>.toOperatorsIndex(): OperatorsIndex =
    this.asIterable().toOperatorsIndex()