@file:JvmName("Variants")

package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmName

/**
 * Whether this term and [other] are *variants*, i.e. equal up to a consistent, one-to-one renaming of their
 * variables: `f(X, Y)` and `f(A, B)` are variants, `f(X, X)` and `f(A, B)` are not. Tags are ignored, and numbers
 * are compared as by [equals]. Terms are traversed iteratively, so arbitrarily deep terms (e.g. long lists) are fine.
 */
@JsName("isVariantOf")
fun Term.isVariantOf(other: Term): Boolean {
    val forward = HashMap<Var, Var>()
    val backward = HashMap<Var, Var>()
    val pending = ArrayDeque<Term>()
    pending.addLast(this)
    pending.addLast(other)
    while (pending.isNotEmpty()) {
        val left = pending.removeFirst()
        val right = pending.removeFirst()
        when {
            left.isVar || right.isVar -> {
                if (!left.isVar || !right.isVar) return false
                val l = left.castToVar()
                val r = right.castToVar()
                if (forward.getOrPut(l) { r } != r || backward.getOrPut(r) { l } != l) return false
            }
            left.isStruct && right.isStruct -> {
                val l = left.castToStruct()
                val r = right.castToStruct()
                if (l.arity != r.arity || l.functor != r.functor) return false
                for (i in 0 until l.arity) {
                    pending.addLast(l.getArgAt(i))
                    pending.addLast(r.getArgAt(i))
                }
            }
            left != right -> return false
        }
    }
    return true
}

/**
 * A hash code consistent with [isVariantOf]: variants have the same one. Variables contribute the position of their
 * first occurrence, so the hash does not depend on their identity or name.
 */
@JsName("variantHashCode")
fun Term.variantHashCode(): Int {
    val variables = HashMap<Var, Int>()
    val pending = ArrayDeque<Term>()
    pending.addLast(this)
    var hash = 1
    while (pending.isNotEmpty()) {
        val term = pending.removeLast()
        hash = 31 * hash +
            when {
                term.isVar -> variables.getOrPut(term.castToVar()) { variables.size }
                term.isStruct -> {
                    val struct = term.castToStruct()
                    for (i in struct.arity - 1 downTo 0) pending.addLast(struct.getArgAt(i))
                    31 * struct.functor.hashCode() + struct.arity
                }
                else -> term.hashCode()
            }
    }
    return hash
}
