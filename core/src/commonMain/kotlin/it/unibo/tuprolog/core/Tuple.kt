package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TupleImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

interface Tuple : Collection {

    override val isTuple: Boolean
        get() = true

    override val functor: String
        get() = FUNCTOR

    override val args: Array<Term>
        get() = arrayOf(left, right)

    override val arity: Int
        get() = 2

    @JsName("left")
    val left: Term

    @JsName("right")
    val right: Term

    override fun toArray(): Array<Term> = unfoldedArray

    override fun toList(): KtList<Term> = unfoldedList

    override fun toSequence(): Sequence<Term> = unfoldedSequence

    override fun freshCopy(): Tuple = super.freshCopy() as Tuple

    override fun freshCopy(scope: Scope): Tuple =
        when {
            isGround -> this
            else -> scope.tupleOf(argsSequence.map { it.freshCopy(scope) }.asIterable())
        }

    override fun tag(name: String, value: Any): Tuple

    companion object {

        const val FUNCTOR = ","

        @JvmStatic
        @JsName("wrapIfNeededTrueDefault")
        fun wrapIfNeeded(vararg terms: Term): Term =
            wrapIfNeeded(*terms, default = { Truth.TRUE })

        @JvmStatic
        @JsName("wrapIfNeeded")
        fun wrapIfNeeded(vararg terms: Term, default: () -> Term): Term =
            when {
                terms.isEmpty() -> default()
                terms.size == 1 -> terms.single()
                else -> of(terms.toList())
            }

        @JvmStatic
        @JsName("of")
        fun of(left: Term, right: Term): Tuple = TupleImpl(left, right)

        @JvmStatic
        @JsName("ofMany")
        fun of(first: Term, second: Term, vararg others: Term): Tuple = of(listOf(first, second, *others))

        @JvmStatic
        @JsName("ofIterable")
        fun of(terms: Iterable<Term>): Tuple = of(terms.toList())

        @JvmStatic
        @JsName("ofList")
        fun of(terms: KtList<Term>): Tuple {
            require(terms.size >= 2) {
                "Tuples require at least 2 terms"
            }

            return terms.slice(0 until terms.lastIndex)
                .foldRight(terms.last()) { l, r -> TupleImpl(l, r) } as Tuple
        }
    }
}
