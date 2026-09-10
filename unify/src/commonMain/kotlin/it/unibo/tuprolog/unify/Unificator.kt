package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Unifies pairs of [Term]s, computing the *most general unifier* (MGU) that makes them syntactically equal, if any
 * exists.
 *
 * A [Unificator] does not perform I/O or throw on failure: unification outcomes are represented as values, namely
 * [Substitution]s. A successful unification yields a [Substitution.Unifier] (possibly empty, i.e.
 * [Substitution.empty]); a failed one yields the [Substitution.Fail] singleton returned by [Substitution.failed].
 * [match] and [unify] are convenience operations built on top of [mgu].
 *
 * Two orthogonal choices distinguish concrete unification strategies, both obtainable from this interface's
 * companion object:
 * - *how* two non-variable terms are deemed equal while building unification equations (e.g. [strict] compares
 *   with plain [Term.equals], while [naive] additionally compares numeric terms by value); and
 * - whether *occurs-check* is enabled, controlled per-call via the `occurCheckEnabled` parameter of [mgu], [match]
 *   and [unify] (`true` by default). Occurs-check prevents a variable from being bound to a term that contains
 *   that same variable (which would otherwise produce an infinite/cyclic term); disabling it trades soundness for
 *   speed, which is safe only when the caller already knows the operands cannot give rise to such a cycle.
 *
 * When the operands have distinct semantic roles, the subject term (such as a goal, query, actual value, or sought
 * item) should be passed first, and the reference term (such as a pattern, rule head, expected value, or stored
 * candidate) second. Calls whose operands have no such roles may retain their natural or mathematical order.
 *
 * Although unifiability is symmetric, the substitutions and unified terms returned by [mgu] and [unify] can retain
 * operand orientation. Reordering their arguments is therefore a behavioral change and requires appropriate tests.
 *
 * ```kotlin
 * val unificator = Unificator.default
 * val x = Var.of("X")
 * val substitution = unificator.mgu(x, Atom.of("a")) // {X -> a}
 * unificator.match(x, Atom.of("a")) // true
 * unificator.unify(x, Atom.of("a")) // a
 * ```
 *
 * To customize how terms are compared, or to observe/alter the equation-solving process, extend [AbstractUnificator]
 * instead of implementing this interface directly.
 */
interface Unificator {
    /**
     * The bindings assumed as already holding before unification starts; every [mgu]/[merge] call is implicitly
     * performed against this context, as if it were merged into the result. If [context] is [Substitution.failed],
     * every operation on this [Unificator] fails as well.
     */
    @JsName("context")
    val context: Substitution

    /**
     * Calculates the Most General Unifier of [term1] and [term2], optionally enabling occurs-check.
     *
     * @return a [Substitution.Unifier] (possibly [Substitution.empty]) binding the variables of [term1] and [term2]
     * so that applying it to both terms yields syntactically equal results, or [Substitution.failed] if no such
     * substitution exists (including when [occurCheckEnabled] is `true` and unification would otherwise produce a
     * cyclic term, or when [context] itself is failed).
     */
    @JsName("mguWithOccurCheck")
    fun mgu(
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean = true,
    ): Substitution

    /** Calculates the Most General Unifier of [term1] and [term2], with occurs-check enabled. */
    @JsName("mgu")
    fun mgu(
        term1: Term,
        term2: Term,
    ): Substitution = mgu(term1, term2, true)

    /**
     * Tells whether [term1] and [term2] match each other, that is, whether [mgu] would succeed for them, optionally
     * enabling occurs-check.
     */
    @JsName("matchWithOccurCheck")
    fun match(
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean,
    ): Boolean = mgu(term1, term2, occurCheckEnabled) !== Substitution.failed()

    /**
     * Tells whether [term1] and [term2] match each other, that is, whether [mgu] would succeed for them.
     * Performs unification with occurs-check enabled.
     */
    @JsName("match")
    fun match(
        term1: Term,
        term2: Term,
    ): Boolean = match(term1, term2, true)

    /**
     * Unifies [term1] and [term2] if possible, optionally enabling occurs-check.
     *
     * @return the result of applying the computed [mgu] to [term1] (retaining [term1]'s orientation), or `null` if
     * [term1] and [term2] do not unify.
     */
    @JsName("unifyWithOccurCheck")
    fun unify(
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean,
    ): Term? {
        val substitution = mgu(term1, term2, occurCheckEnabled)
        return if (substitution.isFailed) null else term1[substitution]
    }

    /** Unifies [term1] and [term2] if possible, with occurs-check enabled. */
    @JsName("unify")
    fun unify(
        term1: Term,
        term2: Term,
    ): Term? = unify(term1, term2, true)

    /**
     * Merges [substitution1] and [substitution2] into a single [Substitution], as if their bindings had been
     * collected while unifying two terms piecewise (e.g. argument by argument): equal-variable bindings from both
     * sides are unified against each other and, optionally, checked for occurrence, rather than simply overwritten.
     *
     * @return the merged [Substitution], or [Substitution.failed] if [substitution1] and [substitution2] disagree
     * on some variable's binding (or either of them, or [context], is already failed).
     */
    @JsName("mergeWithOccurCheck")
    fun merge(
        substitution1: Substitution,
        substitution2: Substitution,
        occurCheckEnabled: Boolean,
    ): Substitution

    /** Merges [substitution1] and [substitution2], with occurs-check enabled. */
    @JsName("merge")
    fun merge(
        substitution1: Substitution,
        substitution2: Substitution,
    ): Substitution = merge(substitution1, substitution2, true)

    companion object {
        /**
         * The default unification strategy: an empty-context [strict] [Unificator], comparing [Term]s' identity
         * through plain [Term.equals].
         *
         * Note: unlike what the name might suggest, [default] performs **no caching** on its own — wrap it (or any
         * other [Unificator]) with [cached] if memoization is needed.
         */
        @JvmStatic
        @JsName("default")
        val default by lazy { strict() }

        /** Computes the Most General Unifier of [this] and [other], using the [default] unification strategy. */
        @JvmStatic
        @JsName("mguWith")
        infix fun Term.mguWith(other: Term): Substitution = default.mgu(this, other)

        /** Computes whether [this] and [other] match, using the [default] unification strategy. */
        @JvmStatic
        @JsName("matches")
        infix fun Term.matches(other: Term): Boolean = default.match(this, other)

        /** Computes the unified term resulting from unifying [this] with [other], using the [default] strategy. */
        @JvmStatic
        @JsName("unifyWith")
        infix fun Term.unifyWith(other: Term): Term? = default.unify(this, other)

        /** Merges [this] and [other], using the [default] unification strategy. */
        @JvmStatic
        @JsName("mergeWith")
        infix fun Substitution.mergeWith(other: Substitution): Substitution = default.merge(this, other)

        /**
         * Creates a naive unification strategy, with the given starting [context], that checks [Term]s' equality
         * through [Term.equals], except for numeric terms which are compared *by value* rather than by exact
         * representation — e.g. an [it.unibo.tuprolog.core.Integer] `1` and a [it.unibo.tuprolog.core.Real] `1.0`
         * are considered equal.
         */
        @JvmStatic
        @JsName("naiveWithContext")
        fun naive(context: Substitution): Unificator =
            object : AbstractUnificator(context) {
                override fun checkTermsEquality(
                    first: Term,
                    second: Term,
                ) = when {
                    first.isInteger && second.isInteger -> {
                        first.castToInteger().value.compareTo(second.castToInteger().value) == 0
                    }
                    first.isNumber && second.isNumber -> {
                        first.castToNumeric().decimalValue.compareTo(second.castToNumeric().decimalValue) == 0
                    }
                    else -> first == second
                }
            }

        /** Creates a naive unification strategy (see [naive]) with an empty starting context. */
        @JvmStatic
        @JsName("naive")
        fun naive(): Unificator = naive(Substitution.empty())

        /**
         * Creates a strict unification strategy, with the given starting [context], that checks [Term]s' equality
         * through plain [Term.equals] — including for numeric terms, so e.g. an integer `1` and a real `1.0` are
         * *not* considered equal (unlike with [naive]).
         */
        @JvmStatic
        @JsName("strictWithContext")
        fun strict(context: Substitution): Unificator =
            object : AbstractUnificator(context) {
                override fun checkTermsEquality(
                    first: Term,
                    second: Term,
                ) = first == second
            }

        /** Creates a strict unification strategy (see [strict]) with an empty starting context. */
        @JvmStatic
        @JsName("strict")
        fun strict(): Unificator = strict(Substitution.empty())

        /**
         * Decorates [other] so that its most recently computed [Unificator.mgu]/[Unificator.merge] results (and,
         * transitively, [Unificator.match]/[Unificator.unify], which are defined in terms of [Unificator.mgu]) are
         * memoized in an LRU cache, avoiding recomputation for repeated requests with the same arguments.
         *
         * If [other] is already a [CachedUnificator], its underlying (non-cached) [Unificator] is re-wrapped with
         * the new [capacity] instead of double-caching.
         *
         * @param other the [Unificator] to be made cached
         * @param capacity the maximum amount of entries the cache may store
         * @return a decorated, cached [Unificator]
         */
        @JvmStatic
        @JsName("cached")
        fun cached(
            other: Unificator,
            capacity: Int = DEFAULT_CACHE_CAPACITY,
        ): Unificator =
            if (other is CachedUnificator) {
                CachedUnificator(other.decorated, capacity)
            } else {
                CachedUnificator(other, capacity)
            }

        /** The default capacity of the LRU cache used by [cached] when no explicit capacity is provided. */
        const val DEFAULT_CACHE_CAPACITY = 32
    }
}
