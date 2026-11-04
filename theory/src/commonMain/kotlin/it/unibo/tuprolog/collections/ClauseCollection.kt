package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.isVariantOf
import it.unibo.tuprolog.unify.UnificationAware
import kotlin.js.JsName

/**
 * A read-only, unification-aware collection of [Clause]s (#1019), the root of the clause-collection family:
 * [ClauseMultiSet] and [ClauseSet] (unordered), [ClauseQueue], [ClauseDeque] and [ClauseList] (ordered, as Prolog
 * orders clauses), each with a mutable counterpart, plus [ClauseMap]. `Theory` is a [ClauseDeque].
 *
 * Membership comes in two flavours, both served by the same clause indexes:
 * - *matching*: [get], [contains], [firstMatching], [countMatching] consider the clauses unifying with a query, according to
 *   the collection's [unificator];
 * - *identity*: [containsVariant] and [countVariants] consider the clauses that are variants of a given one (equal up
 *   to variable renaming, tags ignored), among those matching it.
 *
 * For this reason, clause collections are not `kotlin.collections.Collection`s, whose `contains` and `equals` would
 * have to commit to one of the two. Clause collections are not taggable either, but they preserve the tags of the
 * clauses they store, as they always return the very instances they were given.
 */
interface ClauseCollection :
    Iterable<Clause>,
    UnificationAware {
    /** The number of clauses in this collection. */
    @JsName("size")
    val size: Long

    /** Whether this collection contains no clause. */
    @JsName("isEmpty")
    val isEmpty: Boolean
        get() = !iterator().hasNext()

    /** Whether this collection contains at least one clause. */
    @JsName("isNonEmpty")
    val isNonEmpty: Boolean
        get() = !isEmpty

    /** All the clauses in this collection, in iteration order. */
    @JsName("clauses")
    val clauses: Iterable<Clause>
        get() = this

    /** All the [Rule]s in this collection, in iteration order. */
    @JsName("rules")
    val rules: Iterable<Rule>
        get() = clauses.asSequence().mapNotNull { it.asRule() }.asIterable()

    /** All the [Directive]s in this collection, in iteration order. */
    @JsName("directives")
    val directives: Iterable<Directive>
        get() = clauses.asSequence().mapNotNull { it.asDirective() }.asIterable()

    /** The clauses unifying with [clause], lazily, in iteration order. */
    @JsName("get")
    operator fun get(clause: Clause): Sequence<Clause>

    /** Whether some clause unifies with [clause]. */
    @JsName("contains")
    operator fun contains(clause: Clause): Boolean = get(clause).any()

    /** The first clause unifying with [query], or `null` if none does. */
    @JsName("firstMatching")
    fun firstMatching(query: Clause): Clause? = get(query).firstOrNull()

    /** How many clauses unify with [query]. */
    @JsName("countMatching")
    fun countMatching(query: Clause): Long = get(query).fold(0L) { count, _ -> count + 1 }

    /** Whether some clause is a variant of [clause] (see [isVariantOf]), among those unifying with it. */
    @JsName("containsVariant")
    fun containsVariant(clause: Clause): Boolean = get(clause).any { it.isVariantOf(clause) }

    /** How many clauses are variants of [clause] (see [isVariantOf]), among those unifying with it. */
    @JsName("countVariants")
    fun countVariants(clause: Clause): Long = get(clause).fold(0L) { n, it -> if (it.isVariantOf(clause)) n + 1 else n }

    /** A new mutable collection with the same clauses (in the same order, if ordered) and [unificator]. */
    @JsName("toMutable")
    fun toMutable(): MutableClauseCollection

    /** An immutable collection with the same clauses: this very collection, if it is immutable. */
    @JsName("toImmutable")
    fun toImmutable(): ClauseCollection
}
