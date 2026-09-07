package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.LazyConsWithExplicitLast
import it.unibo.tuprolog.core.impl.LazyConsWithImplicitLast
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

/**
 * A logic list, i.e. either an [EmptyList] (the atom `[]`) or a [Cons] cell. Logic lists are, in Prolog
 * syntax, `[a, b, c]`, which desugars to `.(a, .(b, .(c, [])))` — a chain of [Cons] cells terminated by
 * [EmptyList]. When the final tail is something other than [EmptyList] (a variable, or another term), the
 * chain is a *partial* list, and [isWellFormed] is `false`.
 *
 * [List.of] builds well-formed lists; [List.from] builds lists terminated by an arbitrary [last] term
 * (`null` meaning "terminate with the last given item instead of appending [EmptyList]"), which is how
 * partial lists (e.g. `[H|T]` patterns) are constructed. Lists built from a [Sequence] or [Cursor] are lazily
 * unfolded, which matters for very long or effectively-infinite generated lists.
 */
interface List : Recursive {
    override val isList: Boolean
        get() = true

    /** Whether this list's final tail is [EmptyList], i.e. whether it is a proper (non-partial) list. */
    @JsName("isWellFormed")
    val isWellFormed: Boolean

    /** The last element reachable by following [Cons.tail] repeatedly (the final tail itself, for a partial list). */
    @JsName("last")
    val last: Term

    /**
     * Estimated length of this list.
     * This property is part of the [List] interface to enable efficiency tweaks.
     * It is NOT intended for external usage
     *
     * DO NOT assume this returns the correct length of the current list.
     */
    @JsName("estimatedLength")
    val estimatedLength: Int

    override val unfoldedSequence: Sequence<Term>

    override val unfoldedList: KtList<Term>

    override val unfoldedArray: Array<Term>

    override val size: Int
        get() =
            unfoldedSequence.count().let {
                if (isWellFormed) {
                    it - 1
                } else {
                    it
                }
            }

    override fun freshCopy(): List

    override fun freshCopy(scope: Scope): List

    override fun asList(): List = this

    companion object {
        /** The canonical list-cell functor: `.` (same as [Cons.FUNCTOR]). */
        const val CONS_FUNCTOR = Terms.CONS_FUNCTOR

        /** The canonical empty-list functor: `[]` (same as [EmptyList.FUNCTOR]). */
        const val EMPTY_LIST_FUNCTOR = Terms.EMPTY_LIST_FUNCTOR

        /** Returns the empty logic list, i.e. the atom `[]`. */
        @JvmStatic
        @JsName("empty")
        fun empty(): List = Empty.list()

        /** Creates a well-formed logic list containing [items], in order, terminated by [EmptyList]. */
        @JvmStatic
        @JsName("of")
        fun of(vararg items: Term): List = from(items.toList(), empty())

        /** @see of */
        @JvmStatic
        @JsName("ofIterable")
        fun of(items: Iterable<Term>): List =
            when (items) {
                is KtList<Term> -> from(items, empty())
                else -> from(items.cursor(), empty())
            }

        /** @see of */
        @JvmStatic
        @JsName("ofList")
        fun of(items: KtList<Term>): List = from(items, empty())

        /** @see of */
        @JvmStatic
        @JsName("ofSequence")
        fun of(items: Sequence<Term>): List = from(items.cursor(), empty())

        /**
         * Creates a logic list containing [items], in order, terminated by [last]. Passing `null` as [last]
         * makes the last of [items] itself the final tail (still yielding a well-formed list only if that
         * last item is [EmptyList]); passing any other [Term] (including a [Var]) yields a partial list.
         */
        @JvmStatic
        @JsName("from")
        fun from(
            vararg items: Term,
            last: Term?,
        ): List = from(items.toList(), last)

        /** Equivalent to [from] with `last = null`. */
        @JvmStatic
        @JsName("fromNullTerminated")
        fun from(vararg items: Term): List = from(items.cursor(), null)

        /** @see from */
        @JvmStatic
        @JsName("fromIterable")
        fun from(
            items: Iterable<Term>,
            last: Term?,
        ): List =
            when (items) {
                is KtList<Term> -> from(items, last)
                else -> from(items.cursor(), last)
            }

        /** Equivalent to [from] with `last = null`. */
        @JvmStatic
        @JsName("fromIterableNullTerminated")
        fun from(items: Iterable<Term>): List = from(items, null)

        /**
         * Creates a logic list containing [items], in order, terminated by [last]. The list is unfolded
         * lazily as it is consumed, which is useful for large or generated [items] sequences.
         * @see from
         */
        @JvmStatic
        @JsName("fromSequence")
        fun from(
            items: Sequence<Term>,
            last: Term?,
        ): List = from(items.cursor(), last)

        /** Equivalent to [from] with `last = null`. */
        @JvmStatic
        @JsName("fromSequenceNullTerminated")
        fun from(items: Sequence<Term>): List = from(items.cursor(), null)

        /**
         * Creates a logic list containing [items], in order, terminated by [last].
         * @throws IllegalArgumentException if [items] is empty and [last] is neither `null` nor a [List]
         * @see from
         */
        @JvmStatic
        @JsName("fromList")
        fun from(
            items: KtList<Term>,
            last: Term?,
        ): List {
            if (items.isEmpty()) {
                return (last ?: empty()).asList()
                    ?: throw IllegalArgumentException(
                        "Cannot create a list out of the provided arguments: $items, $last",
                    )
            }
            val i = items.asReversed().iterator()
            var right =
                if (last == null) {
                    i.next()
                    items.last()
                } else {
                    last
                }
            while (i.hasNext()) {
                right = Cons.of(i.next(), right)
            }
            return right.castToList()
        }

        /** Equivalent to [from] with `last = null`. */
        @JvmStatic
        @JsName("fromListNullTerminated")
        fun from(items: KtList<Term>): List = from(items, null)

        /**
         * Creates a logic list lazily unfolding the given [items] [Cursor], terminated by [last].
         * @throws IllegalArgumentException if [items] is already exhausted and [last] is neither `null` nor a [List]
         * @see from
         */
        @JvmStatic
        @JsName("fromCursor")
        fun from(
            items: Cursor<out Term>,
            last: Term?,
        ): List =
            when {
                items.isOver ->
                    (last ?: empty()).asList()
                        ?: throw IllegalArgumentException(
                            "Cannot create a list out of the provided arguments: $items, $last",
                        )
                last == null -> LazyConsWithImplicitLast(items)
                else -> LazyConsWithExplicitLast(items, last)
            }

        /** Equivalent to [from] with `last = null`. */
        @JvmStatic
        @JsName("fromCursorNullTerminated")
        fun from(items: Cursor<out Term>): List = from(items, null)
    }
}
