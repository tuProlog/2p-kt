package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.exception.SubstitutionException
import it.unibo.tuprolog.core.impl.SubstitutionImpl
import it.unibo.tuprolog.utils.Taggable
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.Collection as KtCollection

/**
 * An interface representing a mapping between Variables and their Term substitutions
 *
 * @author Enrico
 * @author Giovanni
 */
interface Substitution : Map<Var, Term>, Taggable<Substitution> {

    /** Whether this [Substitution] is a successful one (i.e., a [Unifier]) */
    @JsName("isSuccess")
    val isSuccess: Boolean

    /** Whether this [Substitution] is a failed one */
    @JsName("isFailed")
    val isFailed: Boolean

    /** Applies this [Substitution] to the given [Term], returning `null` if it is [Substitution.Fail] */
    @JsName("applyTo")
    fun applyTo(term: Term): Term?

    /** Retrieves the original variable name of the provided [variable], if any, or `null` otherwise
     *
     * Consider for instance the substitution { X -> Y, Y -> Z },
     * then the invocation of `getOriginal(Z)` should retrieve `X`
     * */
    // TODO test this method
    @JsName("getOriginal")
    fun getOriginal(variable: Var): Var?

    /**
     * Creates a new substitution that's the *composition* of this and [other]
     *
     * However additional checks are performed:
     * - If one of operands is [Substitution.Fail], the result is [Substitution.Fail]
     * - If the set of substitutions resulting from this union is contradicting, the result is [Substitution.Fail]
     */
    @JsName("plus")
    operator fun plus(other: Substitution): Substitution

    /**
     * Returns a new substitution containing all entries of the original substitution except those
     * entries which variable keys are contained in the given [keys] iterable.
     */
    @JsName("minusIterable")
    operator fun minus(keys: Iterable<Var>): Substitution

    /**
     * Returns a new substitution containing all entries of the original substitution except those
     * entries which variable keys are contained in the given [other] substitution.
     */
    @JsName("minus")
    operator fun minus(other: Substitution): Substitution

    /**
     * Returns a new substitution containing all key-value pairs matching the given [predicate].
     *
     * The returned map preserves the entry iteration order of the original map.
     */
    @JsName("filterEntry")
    fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): Substitution

    /**
     * Returns a new substitution containing all key-value pairs whose key is in [variables].
     *
     * The returned map preserves the entry iteration order of the original map.
     */
    @JsName("filterCollection")
    fun filter(variables: KtCollection<Var>): Substitution // TODO: 16/01/2020 add tests for this specific method

    /**
     * Returns a new substitution containing all key-value pairs matching the given [predicate].
     *
     * The returned map preserves the entry iteration order of the original map.
     */
    @JsName("filter")
    fun filter(predicate: (key: Var, value: Term) -> Boolean): Substitution

    /** Creates a new Successful Substitution (aka Unifier) with given mappings (after some checks) */
    interface Unifier : Substitution {

        override fun minus(keys: Iterable<Var>): Unifier

        override fun minus(other: Substitution): Unifier

        override fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): Unifier

        override fun filter(predicate: (key: Var, value: Term) -> Boolean): Unifier

        override fun filter(variables: KtCollection<Var>): Unifier

        override fun applyTo(term: Term): Term

        override fun replaceTags(tags: Map<String, Any>): Unifier
    }

    /** The Failed Substitution instance */
    interface Fail : Substitution {
        override fun getOriginal(variable: Var): Nothing? = null

        override fun minus(other: Substitution): Fail

        override fun minus(keys: Iterable<Var>): Fail

        override fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): Fail

        override fun filter(predicate: (key: Var, value: Term) -> Boolean): Fail

        override fun filter(variables: KtCollection<Var>): Fail

        override fun applyTo(term: Term): Nothing?

        override fun replaceTags(tags: Map<String, Any>): Fail
    }

    /** Substitution companion with factory functionality */
    companion object {

        private inline fun <T> castToUnifierOrThrowException(arg: T, ctor: (T) -> Substitution): Unifier =
            ctor(arg).let { it as? Unifier ?: throw SubstitutionException(it) }

        private val FAILED: Fail = SubstitutionImpl.FailImpl()

        private val EMPTY: Unifier = SubstitutionImpl.UnifierImpl.of(emptyMap())

        /** Returns a failed substitution, i.e. an instance of type [Substitution.Fail] */
        @JvmStatic
        @JsName("failed")
        fun failed(): Fail = FAILED

        /** Returns an empty unifier, i.e. an instance of type [Substitution.Fail] */
        @JvmStatic
        @JsName("empty")
        fun empty(): Unifier = EMPTY

        /** Creates a [Unifier] of given a map assigning [Var]s to [Term]s */
        @JvmStatic
        @JsName("ofMap")
        fun of(map: Map<Var, Term>): Unifier = unifier(map)

        /** Creates a [Unifier] of given a map assigning [Var]s to [Term]s */
        @JvmStatic
        @JsName("unifier")
        fun unifier(map: Map<Var, Term>): Unifier = SubstitutionImpl.UnifierImpl.of(map)

        /** Creates a singleton [Unifier] containing a single [Var]-[Term] assignment */
        @JvmStatic
        @JsName("ofVar")
        fun of(variable: Var, withTerm: Term): Unifier = of(variable to withTerm) as Unifier

        /**
         * Creates a singleton [Unifier] containing a single [Var]-[Term] assignment.
         * The variable is created on the fly by name, via [Var.of]
         */
        @JvmStatic
        @JsName("of")
        fun of(variable: String, withTerm: Term): Unifier = of(Var.of(variable) to withTerm) as Unifier

        /**
         * Crates a [Substitution] from the given [Var]-[Term] [Pair]s.
         * If any contradiction is found, an instance of [Substitution.Fail] is returned
         */
        @JvmStatic
        @JsName("ofPair")
        fun of(substitutionPair: Pair<Var, Term>, vararg substitutionPairs: Pair<Var, Term>): Substitution =
            SubstitutionImpl.of(sequenceOf(substitutionPair, *substitutionPairs))

        /**
         * Crates a [Substitution] from the given [Var]-[Term] [Pair]s.
         * If any contradiction is found, a [SubstitutionException] is thrown
         */
        @JvmStatic
        @JsName("unifierPairs")
        fun unifier(substitutionPair: Pair<Var, Term>, vararg substitutionPairs: Pair<Var, Term>): Unifier =
            castToUnifierOrThrowException(sequenceOf(substitutionPair, *substitutionPairs), ::of)

        /**
         * Crates a [Substitution] from the given [Iterable] of [Var]-[Term] [Pair]s.
         * If any contradiction is found, an instance of [Substitution.Fail] is returned
         */
        @JvmStatic
        @JsName("ofIterable")
        fun of(substitutionPairs: Iterable<Pair<Var, Term>>): Substitution =
            SubstitutionImpl.of(substitutionPairs.asSequence())

        /**
         * Crates a [Unifier] from the given [Iterable] of [Var]-[Term] [Pair]s.
         * If any contradiction is found, a [SubstitutionException] is thrown
         */
        @JvmStatic
        @JsName("unifierIterable")
        fun unifier(map: Iterable<Pair<Var, Term>>): Unifier = castToUnifierOrThrowException(map, ::of)

        /**
         * Crates a [Substitution] from the given [Sequence] of [Var]-[Term] [Pair]s.
         * If any contradiction is found, an instance of [Substitution.Fail] is returned
         */
        @JvmStatic
        @JsName("ofSequence")
        fun of(substitutionPairs: Sequence<Pair<Var, Term>>): Substitution =
            SubstitutionImpl.of(substitutionPairs.asSequence())

        /**
         * Crates a [Unifier] from the given [Sequence] of [Var]-[Term] [Pair]s.
         * If any contradiction is found, a [SubstitutionException] is thrown
         */
        @JvmStatic
        @JsName("unifierSequence")
        fun unifier(map: Sequence<Pair<Var, Term>>): Unifier = castToUnifierOrThrowException(map, ::of)

        /**
         * Composes the provided [Substitution]s by merging them.
         * If any failure or contradiction is found, the result will be [Substitution.Fail]
         */
        @JvmStatic
        @JsName("ofSubstitution")
        fun of(substitution: Substitution, vararg substitutions: Substitution): Substitution =
            substitutions.fold(substitution, Substitution::plus)
    }
}
