package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.exception.SubstitutionException
import it.unibo.tuprolog.utils.Castable
import it.unibo.tuprolog.utils.Taggable
import it.unibo.tuprolog.utils.TagsOperator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.Collection as KtCollection

/**
 * General type for logic substitutions (i.e. variables assignments).
 * There are two sorts of substitutions:
 * - [Substitution.Unifier], which represent one possible assignment for a possibly empty set of [Var]s
 * - [Substitution.Fail], which represent the lack of possible assignments for any set of [Var]s
 */
sealed interface Substitution :
    Map<Var, Term>,
    Taggable<Substitution>,
    Castable<Substitution> {
    /** Whether this [Substitution] is a successful one (i.e., a [Unifier]) */
    @JsName("isSuccess")
    val isSuccess: Boolean

    /** Whether this [Substitution] is a failed one */
    @JsName("isFailed")
    val isFailed: Boolean

    /**
     * Casts the current [Substitution] to [Unifier], if possible, or returns `null` otherwise
     * @return the current [Substitution], casted to [Unifier], or `null`, if the current term is not an instance of [Unifier]
     */
    @JsName("asUnifier")
    fun asUnifier(): Unifier? = null

    /**
     * Casts the current [Substitution] to [Unifier], if possible
     * @throws ClassCastException if the current [Substitution] is not an instance of [Unifier]
     * @return the current [Substitution], casted to [Unifier]
     */
    @JsName("castToUnifier")
    fun castToUnifier(): Unifier =
        asUnifier() ?: throw ClassCastException("Cannot cast $this to ${Unifier::class.simpleName}")

    /**
     * Casts the current [Substitution] to [Fail], if possible, or returns `null` otherwise
     * @return the current [Substitution], casted to [Fail], or `null`, if the current term is not an instance of [Fail]
     */
    @JsName("asFail")
    fun asFail(): Fail? = null

    /**
     * Casts the current [Substitution] to [Fail], if possible
     * @throws ClassCastException if the current [Substitution] is not an instance of [Fail]
     * @return the current [Substitution], casted to [Fail]
     */
    @JsName("castToFail")
    fun castToFail(): Fail = asFail() ?: throw ClassCastException("Cannot cast $this to ${Fail::class.simpleName}")

    /** Applies this [Substitution] to the given [Term], returning `null` if it is [Substitution.Fail] */
    @JsName("applyTo")
    fun applyTo(term: Term): Term?

    @JsName("whenIs")
    fun <T> whenIs(
        unifier: ((Unifier) -> T)? = null,
        fail: ((Fail) -> T)? = null,
        otherwise: ((Substitution) -> T) = { throw IllegalStateException("Cannot handle solution $it") },
    ): T {
        if (isSuccess && unifier != null) {
            return unifier(castToUnifier())
        }
        if (isFailed && fail != null) {
            return fail(castToFail())
        }
        return otherwise(this)
    }

    /** Retrieves the original variable name of the provided [variable], if any, or `null` otherwise
     *
     * Consider for instance the substitution `{ X -> Y, Y -> Z }`,
     * then the invocation of `getOriginal(Z)` should retrieve `X`
     */
    @JsName("getOriginal")
    fun getOriginal(variable: Var): Var? // TODO test this method

    @JsName("getByName")
    fun getByName(name: String): Term? = keys.find { it.name == name }?.let { get(it) }

    /**
     * Creates a new [Substitution] that is the *composition* (a.k.a. union) of `this` and [other].
     * The composition is not guaranteed to be a [Substitution.Unifier], even if both arguments are.
     * In fact, the composition algorithm performs the following checks:
     * - If one of arguments is of type [Substitution.Fail], the result is of type [Substitution.Fail]
     * - If the set of assignments attained by composing the two substitutions is contradictory -- i.e.,
     * if the same [Var] is assigned to different [Term]s --, the result is of type [Substitution.Fail]
     * - Otherwise, the result is an instance of [Substitution.Unifier]
     *
     * Regardless of its type, the resulting [Substitution] will contain the tags of both input [Substitution]s.
     */
    @JsName("plus")
    operator fun plus(other: Substitution): Substitution

    /**
     * Creates a new [Substitution] that is the *composition* (a.k.a. union) of `this` and [other].
     * The composition is not guaranteed to be a [Substitution.Unifier], even if both arguments are.
     * In fact, the composition algorithm performs the following checks:
     * - If one of arguments is of type [Substitution.Fail], the result is of type [Substitution.Fail]
     * - If the set of assignments attained by composing the two substitutions is contradictory -- i.e.,
     * if the same [Var] is assigned to different [Term]s --, the result is of type [Substitution.Fail]
     * - Otherwise, the result is an instance of [Substitution.Unifier]
     *
     * Regardless of its type, the resulting [Substitution] will contain the tags attained by merging the input
     * [Substitution]s' tags via [tagsMerger].
     */
    @JsName("plusMergingTags")
    fun plus(
        other: Substitution,
        tagsMerger: TagsOperator,
    ): Substitution

    /**
     * Returns a new substitution containing all entries of the original substitution except those
     * entries which variable keys are contained in the given [keys] iterable.
     *
     * Regardless of its type, the resulting [Substitution] will contain the same tags of the original one
     */
    @JsName("minusIterable")
    operator fun minus(keys: Iterable<Var>): Substitution

    @JsName("minusVar")
    operator fun minus(variable: Var): Substitution

    @JsName("minusVars")
    fun minus(
        variable: Var,
        vararg otherVariables: Var,
    ): Substitution

    /**
     * Returns a new substitution containing all entries of the original substitution except those
     * entries which variable keys are contained in the given [other] substitution.
     *
     * Regardless of its type, the resulting [Substitution] will contain the same tags of the original one
     */
    @JsName("minus")
    operator fun minus(other: Substitution): Substitution

    /**
     * Returns a new substitution containing all key-value pairs matching the given [predicate].
     *
     * The returned map preserves the entry iteration order of the original map.
     *
     * Regardless of its type, the resulting [Substitution] will contain the same tags of the original one
     */
    @JsName("filterEntry")
    fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): Substitution

    /**
     * Returns a new substitution containing all key-value pairs whose key is in [variables].
     *
     * The returned map preserves the entry iteration order of the original map.
     *
     * Regardless of its type, the resulting [Substitution] will contain the same tags of the original one
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

    /** A type for successful [Substitution]s (a.k.a. unifiers) actually assigning [Var]s to [Term]s */
    sealed interface Unifier : Substitution {
        override fun minus(keys: Iterable<Var>): Unifier

        override fun minus(other: Substitution): Unifier

        override fun minus(variable: Var): Unifier

        override fun minus(
            variable: Var,
            vararg otherVariables: Var,
        ): Unifier

        override fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): Unifier

        override fun filter(predicate: (key: Var, value: Term) -> Boolean): Unifier

        override fun filter(variables: KtCollection<Var>): Unifier

        override fun applyTo(term: Term): Term

        override fun replaceTags(tags: Map<String, Any>): Unifier

        override fun asUnifier(): Unifier = this
    }

    /** A type for __failed__ [Substitution]s, assigning no [Var] */
    sealed interface Fail : Substitution {
        override fun getOriginal(variable: Var): Nothing? = null

        override fun minus(other: Substitution): Fail

        override fun minus(keys: Iterable<Var>): Fail

        override fun minus(variable: Var): Fail

        override fun minus(
            variable: Var,
            vararg otherVariables: Var,
        ): Fail

        override fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): Fail

        override fun filter(predicate: (key: Var, value: Term) -> Boolean): Fail

        override fun filter(variables: KtCollection<Var>): Fail

        override fun applyTo(term: Term): Nothing?

        override fun replaceTags(tags: Map<String, Any>): Fail

        override fun asFail(): Fail = this
    }

    /** Substitution companion with factory functionality */
    companion object {
        private inline fun <T> castToUnifierOrThrowException(
            arg: T,
            ctor: (T) -> Substitution,
        ): Unifier = ctor(arg).let { it.asUnifier() ?: throw SubstitutionException(it) }

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
        @JsName("unifierMap")
        fun unifier(map: Map<Var, Term>): Unifier = SubstitutionImpl.UnifierImpl.of(map)

        /** Creates a singleton [Unifier] containing a single [Var]-[Term] assignment */
        @JvmStatic
        @JsName("of")
        fun of(
            variable: Var,
            term: Term,
        ): Unifier = of(mapOf(variable to term))

        /** Creates a [Unifier] of given a map assigning [Var]s to [Term]s */
        @JvmStatic
        @JsName("unifier")
        fun unifier(
            variable: Var,
            term: Term,
        ): Unifier = of(mapOf(variable to term))

        /**
         * Creates a singleton [Unifier] containing a single [Var]-[Term] assignment.
         * The variable is created on the fly by name, via [Var.of]
         */
        @JvmStatic
        @JsName("ofString")
        fun of(
            variable: String,
            term: Term,
        ): Unifier = of(mapOf(Var.of(variable) to term))

        /**
         * Creates a singleton [Unifier] containing a single [Var]-[Term] assignment.
         * The variable is created on the fly by name, via [Var.of]
         */
        @JvmStatic
        @JsName("unifierString")
        fun unifier(
            variable: String,
            term: Term,
        ): Unifier = of(mapOf(Var.of(variable) to term))

        /**
         * Crates a [Substitution] from the given [Var]-[Term] [Pair]s.
         * If any contradiction is found, an instance of [Substitution.Fail] is returned
         */
        @JvmStatic
        @JsName("ofPair")
        fun of(
            substitutionPair: Pair<Var, Term>,
            vararg substitutionPairs: Pair<Var, Term>,
        ): Substitution = SubstitutionImpl.of(sequenceOf(substitutionPair, *substitutionPairs))

        /**
         * Crates a [Substitution] from the given [Var]-[Term] [Pair]s.
         * If any contradiction is found, a [SubstitutionException] is thrown
         */
        @JvmStatic
        @JsName("unifierPairs")
        fun unifier(
            substitutionPair: Pair<Var, Term>,
            vararg substitutionPairs: Pair<Var, Term>,
        ): Unifier = castToUnifierOrThrowException(sequenceOf(substitutionPair, *substitutionPairs), ::of)

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
        fun of(
            substitution: Substitution,
            vararg substitutions: Substitution,
        ): Substitution = substitutions.fold(substitution, Substitution::plus)
    }
}
