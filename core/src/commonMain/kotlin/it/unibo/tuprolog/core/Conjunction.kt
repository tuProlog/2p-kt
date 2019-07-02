package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.ConjunctionImpl
import kotlin.collections.List as KtList

interface Conjunction : Struct {

    override val isConjunction: Boolean
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

    override fun freshCopy(): Conjunction = super.freshCopy() as Conjunction

    override fun freshCopy(scope: Scope): Conjunction =
            if (isGround) {
                this
            } else {
                scope.conjunctionOf(argsSequence.map { it.freshCopy(scope) }.asIterable())
            }

    companion object {

        const val FUNCTOR = ","

        fun of(left: Term, right: Term): Conjunction {
            return ConjunctionImpl(left, right)
        }

        fun of(left: Term, right: Term, others: Term): Conjunction {
            return of(listOf(left, right, others))
        }

        fun of(terms: Iterable<Term>): Conjunction {
            return of(terms.toList())
        }

        fun of(terms: KtList<Term>): Conjunction {
            require(terms.size >= 2) {
                "Conjunctions require at least 2 terms"
            }

            return terms.slice(0 until terms.lastIndex)
                    .foldRight(terms.last()) { l, r -> ConjunctionImpl(l, r) } as Conjunction
        }
    }
}