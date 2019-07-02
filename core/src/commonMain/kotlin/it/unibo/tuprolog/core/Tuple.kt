package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TupleImpl
import kotlin.collections.List as KtList

interface Tuple : Struct {

    override val isTuple: Boolean
        get() = true

    override val functor: String
        get() = Couple.FUNCTOR

    override val args: Array<Term>
        get() = arrayOf(left, right)

    override val arity: Int
        get() = 2

    val left: Term

    val right: Term

    val unfoldedSequence: Sequence<Term>

    val unfoldedList: KtList<Term>

    val unfoldedArray: Array<Term>

    fun toArray(): Array<Term> = unfoldedArray

    fun toList(): KtList<Term> = unfoldedList

    fun toSequence(): Sequence<Term> = unfoldedSequence

    override fun freshCopy(): Tuple = super.freshCopy() as Tuple

    override fun freshCopy(scope: Scope): Tuple =
            if (isGround) {
                this
            } else {
                scope.tupleOf(argsSequence.map { it.freshCopy(scope) }.asIterable())
            }

    companion object {

        const val FUNCTOR = ","

        fun wrapIfNeeded(vararg terms: Term, default: () -> Term = Truth.Companion::`true`): Term {
            return when {
                terms.isEmpty() -> default()
                terms.size == 1 -> terms[0]
                else -> of(terms.toList())
            }
        }

        fun of(left: Term, right: Term): Tuple {
            return TupleImpl(left, right)
        }

        fun of(left: Term, right: Term, others: Term): Tuple {
            return of(listOf(left, right, others))
        }

        fun of(terms: Iterable<Term>): Tuple {
            return of(terms.toList())
        }

        fun of(terms: KtList<Term>): Tuple {
            require(terms.size >= 2) {
                "Tuples require at least 2 terms"
            }

            return terms.slice(0 until terms.lastIndex)
                    .foldRight(terms.last()) { l, r -> TupleImpl(l, r) } as Tuple
        }
    }
}