package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.BLOCK_FUNCTOR
import it.unibo.tuprolog.core.Terms.CLAUSE_FUNCTOR
import it.unibo.tuprolog.core.Terms.CONS_FUNCTOR
import it.unibo.tuprolog.core.Terms.EMPTY_BLOCK_FUNCTOR
import it.unibo.tuprolog.core.Terms.EMPTY_LIST_FUNCTOR
import it.unibo.tuprolog.core.Terms.FAIL_FUNCTOR
import it.unibo.tuprolog.core.Terms.INDICATOR_FUNCTOR
import it.unibo.tuprolog.core.Terms.TRUE_FUNCTOR
import it.unibo.tuprolog.core.Terms.TUPLE_FUNCTOR
import it.unibo.tuprolog.core.impl.StructImpl
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

/**
 * Base type for compound [Term]s, a.k.a. structures.
 * A [Struct] is characterised by a [functor] and a given (non-negative) amount of [args], namely [arity].
 * Each argument can be a [Term] of any sort.
 */
interface Struct : Term {
    override val isStruct: Boolean
        get() = true

    override val isClause: Boolean
        get() =
            CLAUSE_FUNCTOR == functor &&
                when (arity) {
                    2 -> getArgAt(0).isStruct
                    1 -> true
                    else -> false
                }

    override val isRule: Boolean
        get() = isClause && arity == 2

    override val isDirective: Boolean
        get() = isClause && arity == 1

    override val isFact: Boolean
        get() = isRule && getArgAt(1).isTrue

    override val isTuple: Boolean
        get() = functor == TUPLE_FUNCTOR && arity == 2

    override val isAtom: Boolean
        get() = arity == 0

    override val isList: Boolean
        get() = isCons || isEmptyList

    override val isCons: Boolean
        get() = CONS_FUNCTOR == functor && arity == 2

    override val isBlock: Boolean
        get() = (BLOCK_FUNCTOR == functor && arity == 1) || isEmptyBlock

    override val isEmptyBlock: Boolean
        get() = EMPTY_BLOCK_FUNCTOR == functor && arity == 0

    override val isEmptyList: Boolean
        get() = EMPTY_LIST_FUNCTOR == functor && arity == 0

    override val isTrue: Boolean
        get() = isAtom && TRUE_FUNCTOR == functor

    override val isFail: Boolean
        get() = isAtom && FAIL_FUNCTOR == functor

    override val isIndicator: Boolean
        get() = Indicator.FUNCTOR == functor && arity == 2

    override val variables: Sequence<Var>
        get() = argsSequence.flatMap { it.variables }

    override fun freshCopy(): Struct

    override fun freshCopy(scope: Scope): Struct

    override fun asStruct(): Struct = this

    /**
     * An alias for [addLast].
     */
    @JsName("append")
    fun append(argument: Term): Struct = addLast(argument)

    /**
     * Creates a novel [Struct] which is a copy of the current one, expect that is has one more argument.
     * The novel [argument] is appended at the end of the new [Struct]'s arguments list.
     * @param argument is a [Term] of any sort
     * @return a new [Struct], whose [functor] is equals to the current one,
     * whose [arity] is greater than the current one, and whose last argument is [argument]
     */
    @JsName("addLast")
    fun addLast(argument: Term): Struct

    /**
     * Creates a novel [Struct] which is a copy of the current one, expect that is has one more argument.
     * The novel [argument] is appended at the beginning of the new [Struct]'s arguments list.
     * @param argument is a [Term] of any sort
     * @return a new [Struct], whose [functor] is equals to the current one,
     * whose [arity] is greater than the current one, and whose first argument is [argument]
     */
    @JsName("addFirst")
    fun addFirst(argument: Term): Struct

    /**
     * Creates a novel [Struct] which is a copy of the current one, expect that is has one more argument.
     * The novel [argument] is inserted into the new [Struct]'s arguments list, at index [index], wheres subsequent
     * arguments indexes are shifted by 1.
     * @param index is the index the new [argument] should be inserted into
     * @param argument is a [Term] of any sort
     * @throws IndexOutOfBoundsException if [index] is lower than 0 or greater or equal to [arity]
     * @return a new [Struct], whose [functor] is equals to the current one,
     * whose [arity] is greater than the current one, and whose [index]-th argument is [argument]
     */
    @JsName("insertAt")
    fun insertAt(
        index: Int,
        argument: Term,
    ): Struct

    /**
     * Creates a novel [Struct] which is a copy of the current one, expect that is has a different functor.
     * @param functor is a [String] representing the new functor
     * @return a new [Struct], whose functor is [functor], and whose [arity] and arguments list are equal
     * to the current one
     */
    @JsName("setFunctor")
    fun setFunctor(functor: String): Struct

    /**
     * The functor of this [Struct].
     */
    @JsName("functor")
    val functor: String

    /**
     * Returns `true` if and only if [functor] matches [Struct.WELL_FORMED_FUNCTOR_PATTERN].
     */
    @JsName("isFunctorWellFormed")
    val isFunctorWellFormed: Boolean

    /**
     * The total amount of arguments of this [Struct].
     * This is equal to the length of [args].
     */
    @JsName("arity")
    val arity: Int
        get() = args.size

    /**
     * The indicator corresponding to this [Struct], i.e. [functor]/[arity].
     */
    @JsName("indicator")
    val indicator: Indicator
        get() = Indicator.of(functor, arity)

    /**
     * List of arguments of this [Struct].
     */
    @JsName("argsList")
    val args: KtList<Term>

    /**
     * Sequence of arguments of this [Struct].
     */
    @JsName("argsSequence")
    val argsSequence: Sequence<Term>
        get() = args.asSequence()

    /**
     * Gets the [index]-th argument if this [Struct].
     * @param index is the index the argument which should be retrieved
     * @throws IndexOutOfBoundsException if [index] is lower than 0 or greater or equal to [arity]
     * @return the [Term] having position [index] in [args]
     */
    @JsName("getArgAt")
    fun getArgAt(index: Int): Term = args[index]

    @JsName("setArgs")
    fun setArgs(vararg args: Term): Struct

    @JsName("setArgsIterable")
    fun setArgs(args: Iterable<Term>): Struct

    @JsName("setArgsSequence")
    fun setArgs(args: Sequence<Term>): Struct

    /**
     * Alias for [getArgAt].
     * In Kotlin, this method enables the syntax `struct[index]`.
     */
    @JsName("get")
    operator fun get(index: Int): Term = getArgAt(index)

    companion object {
        /**
         * The pattern of a well-formed functor for a [Struct].
         * A functor is well-formed if and only if:
         * - it starts with a lower-case letter
         * - it only contains letters, digits, or underscores
         */
        @JvmField
        val WELL_FORMED_FUNCTOR_PATTERN = Terms.WELL_FORMED_FUNCTOR_PATTERN

        /**
         * The pattern of an ill-formed functor for a [Struct] containing
         * - non-printable characters (e.g. `\n`, `\r`, `\t`)
         * - other characters which must be escaped (e.g. `"`, `'`, `\`)
         */
        @JvmField
        val NON_PRINTABLE_CHARACTER_PATTERN = Terms.NON_PRINTABLE_CHARACTER_PATTERN

        /**
         * Checks if a [string] matches [WELL_FORMED_FUNCTOR_PATTERN].
         * @param string the [String] to be checked
         * @return `true` if [string] matches [WELL_FORMED_FUNCTOR_PATTERN], `false` otherwise
         */
        @JvmStatic
        @JsName("isWellFormedFunctor")
        fun isWellFormedFunctor(string: String): Boolean = Terms.WELL_FORMED_FUNCTOR_PATTERN.matches(string)

        /**
         * Wraps the provided [string] within single quotes, unconditionally.
         * @param string the [String] to be enquoted
         * @return a new [String], beginning and ending with `'`
         */
        @JvmStatic
        @JsName("enquoteFunctor")
        fun enquoteFunctor(string: String): String = "'$string'"

        /**
         * Wraps the provided [string] within single quotes, but only if it is not well-formed.
         * Well-formed check is performed via the [isWellFormedFunctor] method.
         * @param string the [String] to be enquoted
         * @return either a new [String], beginning and ending with `'`, or [string]
         */
        @JvmStatic
        @JsName("enquoteFunctorIfNecessary")
        fun enquoteFunctorIfNecessary(string: String): String =
            if (isWellFormedFunctor(string)) {
                string
            } else {
                enquoteFunctor(string)
            }

        /**
         * Checks if a [string] matches [NON_PRINTABLE_CHARACTER_PATTERN].
         * @param string the [String] to be checked
         * @return `true` if [string] matches [NON_PRINTABLE_CHARACTER_PATTERN], `false` otherwise
         */
        @JvmStatic
        @JsName("functorNeedsEscape")
        fun functorNeedsEscape(string: String): Boolean = NON_PRINTABLE_CHARACTER_PATTERN.containsMatchIn(string)

        /**
         * Unconditionally, escapes all occurrences of the characters `\n`, `\r`, `\t`, and `\` in [string].
         * Depending on the value of parameters [escapeDoubleQuotes] and [escapeSingleQuotes], occurrences of characters
         * `"` and `'` may be escaped as well.
         * @param string the [String] whose characters must should be escaped
         * @param escapeDoubleQuotes decides whether double-quote characters (i.e., `"`) should be escaped
         * (defaults to `true`)
         * @param escapeSingleQuotes decides whether single-quote characters (i.e., `'`) should be escaped
         * (defaults to `!escapeDoubleQuotes`)
         * @return either a new [String], beginning and ending with `'`, or [string]
         */
        @JvmStatic
        @JsName("escapeFunctor")
        fun escapeFunctor(
            string: String,
            escapeSingleQuotes: Boolean = true,
            escapeDoubleQuotes: Boolean = !escapeSingleQuotes,
        ): String =
            string
                .toCharArray()
                .asSequence()
                .map { Terms.escapeChar(it, escapeSingleQuotes, escapeDoubleQuotes) }
                .reduceOrNull(String::plus)
                ?: ""

        /**
         * Escapes all occurrences of the characters `\n`, `\r`, `\t`, and `\` in [string], but only if a preliminary
         * check reveals that it contains some character which require escaping.
         * Depending on the value of parameters [escapeDoubleQuotes] and [escapeSingleQuotes], occurrences of characters
         * `"` and `'` may be escaped as well.
         * @param string the [String] whose characters must should be escaped
         * @param escapeDoubleQuotes decides whether double-quote characters (i.e., `"`) should be escaped
         * (defaults to `true`)
         * @param escapeSingleQuotes decides whether single-quote characters (i.e., `'`) should be escaped
         * (defaults to `!escapeDoubleQuotes`)
         * @return either a new [String], beginning and ending with `'`, or [string]
         */
        @JvmStatic
        @JsName("escapeFunctorIfNeeded")
        fun escapeFunctorIfNecessary(
            string: String,
            escapeSingleQuotes: Boolean = true,
            escapeDoubleQuotes: Boolean = !escapeSingleQuotes,
        ): String =
            if (functorNeedsEscape(string)) {
                escapeFunctor(string, escapeSingleQuotes, escapeDoubleQuotes)
            } else {
                string
            }

        /**
         * Creates a new [Struct] with [functor] as functor and a given amount of anonymous [Var]iables, namely [arity].
         * Instances of [Struct] are always created of the most adequate sub-type of [Struct].
         * @see [Var.anonymous]
         * @see [of]
         * @throws IllegalArgumentException is [arity] is a negative [Int]
         * @return a new instance of [Struct]
         */
        @JvmStatic
        @JsName("template")
        fun template(
            functor: String,
            arity: Int,
        ): Struct {
            require(arity >= 0) { "Arity must be a non-negative integer" }
            return of(functor, (0 until arity).map { Var.anonymous() })
        }

        /**
         * Creates a new [Struct] from the given [KtList] of [Term]s.
         * Instances of [Struct] are always created of the most adequate sub-type of [Struct].
         * This implies that, whenever possible, the creation of the new [Struct] may be delegated to:
         * - [Cons.of]
         * - [Rule.of]
         * - [Directive.of]
         * - [Tuple.of]
         * - [Indicator.of]
         * - [Block.of]
         *
         * depending on the value of [functor] and on the amount and sorts of items in [args].
         * @param functor is the [String] to be used as functor of the new [Struct]
         * @param args is the [KtList] of [Term]s to be used as argument list of the new [Struct]
         * @return a new instance of [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("ofList")
        fun of(
            functor: String,
            args: KtList<Term>,
        ): Struct =
            when {
                args.size == 2 && CONS_FUNCTOR == functor -> Cons.of(args.first(), args.last())
                args.size == 2 && CLAUSE_FUNCTOR == functor && args.first().isStruct ->
                    Rule.of(args.first().castToStruct(), args.last())
                args.size == 2 && TUPLE_FUNCTOR == functor -> Tuple.of(args)
                args.size == 2 && INDICATOR_FUNCTOR == functor -> Indicator.of(args.first(), args.last())
                args.size == 1 && BLOCK_FUNCTOR == functor -> Block.of(args)
                args.size == 1 && CLAUSE_FUNCTOR == functor -> Directive.of(args.first())
                args.isEmpty() -> Atom.of(functor)
                else -> StructImpl(functor, args, emptyMap())
            }

        /**
         * Creates a new [Struct] from the given [Term]s.
         * Instances of [Struct] are always created of the most adequate sub-type of [Struct].
         * This implies that, whenever possible, the creation of the new [Struct] may be delegated to:
         * - [Cons.of]
         * - [Rule.of]
         * - [Directive.of]
         * - [Tuple.of]
         * - [Indicator.of]
         * - [Block.of]
         *
         * depending on the value of [functor] and on the amount and sorts provided [Term]s.
         * @param functor is the [String] to be used as functor of the new [Struct]
         * @param args is the `vararg` array of [Term]s to be used as argument list of the new [Struct]
         * @return a new instance of [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("of")
        fun of(
            functor: String,
            vararg args: Term,
        ): Struct = of(functor, args.toList())

        /**
         * Creates a new [Struct] from the given [Sequence] of [Term]s.
         * Instances of [Struct] are always created of the most adequate sub-type of [Struct].
         * This implies that, whenever possible, the creation of the new [Struct] may be delegated to:
         * - [Cons.of]
         * - [Rule.of]
         * - [Directive.of]
         * - [Tuple.of]
         * - [Indicator.of]
         * - [Block.of]
         *
         * depending on the value of [functor] and on the amount and sorts of items in [args].
         * @param functor is the [String] to be used as functor of the new [Struct]
         * @param args is the [Sequence] of [Term]s to be used as argument list of the new [Struct]
         * @return a new instance of [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            functor: String,
            args: Sequence<Term>,
        ): Struct = of(functor, args.toList())

        /**
         * Creates a new [Struct] from the given [Iterable] of [Term]s.
         * Instances of [Struct] are always created of the most adequate sub-type of [Struct].
         * This implies that, whenever possible, the creation of the new [Struct] may be delegated to:
         * - [Cons.of]
         * - [Rule.of]
         * - [Directive.of]
         * - [Tuple.of]
         * - [Indicator.of]
         * - [Block.of]
         *
         * depending on the value of [functor] and on the amount and sorts of items in [args].
         * @param functor is the [String] to be used as functor of the new [Struct]
         * @param args is the [Iterable] of [Term]s to be used as argument list of the new [Struct]
         * @return a new instance of [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            functor: String,
            args: Iterable<Term>,
        ): Struct = of(functor, args.toList())

        /**
         * Folds the [Term]s in [terms] from left to right, creating binary structures having [operator] as functor.
         * Let `f` be the value of [operator], and let `t_i` be the `i`-th term in [terms].
         * Then, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n-1, t_n) ...))
         * ```
         * Of course, this method will return an instance of either [Tuple] in case the argument [operator] is adequate.
         * @param operator the functor of the [Struct]ures used to fold the [terms]
         * @param terms the [KtList] of [Terms] to be folded
         * @throws IllegalArgumentException if [terms] has less than 2 items
         * @return a new [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("foldListNullTerminated")
        fun fold(
            operator: String,
            terms: KtList<Term>,
        ): Struct = fold(operator, terms, null)

        /**
         * Folds the [Term]s in [terms] from left to right, creating binary structures having [operator] as functor.
         * Let `f` be the value of [operator], and let `t_i` be the `i`-th term in [terms].
         * Then, if [terminal] is non-null, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n, T) ...))
         * ```
         * where `T` is the value of [terminal], and `t_n` is the last item in [terms].
         * Conversely, if [terminal] is null, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n-1, t_n) ...))
         * ```
         * Of course, this method will return an instance of either [List], or [Tuple] in case the arguments [operator]
         * and [terminal] are adequate.
         * @param operator the functor of the [Struct]ures used to fold the [terms]
         * @param terms the [KtList] of [Terms] to be folded
         * @param terminal the termination [Term] used as the deepest one in the returned [Struct], or `null`
         * if `t_n` should be used instead
         * @throws IllegalArgumentException if [terms] has less than 2 items
         * @return a new [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("foldList")
        fun fold(
            operator: String,
            terms: KtList<Term>,
            terminal: Term?,
        ): Struct =
            when {
                operator == CONS_FUNCTOR && terminal == EmptyList() -> List.of(terms)
                operator == CONS_FUNCTOR && terminal == null -> List.from(terms)
                operator == TUPLE_FUNCTOR -> Tuple.of(terms + listOfNotNull(terminal))
                terminal == null -> {
                    require(terms.size >= 2) { "Struct requires at least two terms to fold" }
                    terms
                        .slice(0 until terms.lastIndex - 1)
                        .foldRight(of(operator, terms[terms.lastIndex - 1], terms[terms.lastIndex])) { a, b ->
                            of(operator, a, b)
                        }
                }
                else -> {
                    require(terms.isNotEmpty()) { "Struct requires at least two terms to fold" }
                    terms
                        .slice(0 until terms.lastIndex)
                        .foldRight(of(operator, terms[terms.lastIndex], terminal)) { a, b ->
                            of(operator, a, b)
                        }
                }
            }

        /**
         * Folds the [Term]s in [terms] from left to right, creating binary structures having [operator] as functor.
         * Let `f` be the value of [operator], and let `t_i` be the `i`-th term in [terms].
         * Then, if [terminal] is non-null, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n, T) ...))
         * ```
         * where `T` is the value of [terminal], and `t_n` is the last item in [terms].
         * Conversely, if [terminal] is null, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n-1, t_n) ...))
         * ```
         * Of course, this method will return an instance of either [List], or [Tuple] in case the arguments [operator]
         * and [terminal] are adequate.
         * @param operator the functor of the [Struct]ures used to fold the [terms]
         * @param terms the [Sequence] of [Terms] to be folded
         * @param terminal the termination [Term] used as the deepest one in the returned [Struct], or `null`
         * if `t_n` should be used instead
         * @throws IllegalArgumentException if [terms] has less than 2 items
         * @return a new [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("foldSequence")
        fun fold(
            operator: String,
            terms: Sequence<Term>,
            terminal: Term?,
        ): Struct = fold(operator, terms.toList(), terminal)

        /**
         * Folds the [Term]s in [terms] from left to right, creating binary structures having [operator] as functor.
         * Let `f` be the value of [operator], and let `t_i` be the `i`-th term in [terms].
         * Then, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n-1, t_n) ...))
         * ```
         * Of course, this method will return an instance of either [Tuple] in case the argument [operator] is adequate.
         * @param operator the functor of the [Struct]ures used to fold the [terms]
         * @param terms the [Sequence] of [Terms] to be folded
         * @throws IllegalArgumentException if [terms] has less than 2 items
         * @return a new [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("foldSequenceNullTerminated")
        fun fold(
            operator: String,
            terms: Sequence<Term>,
        ): Struct = fold(operator, terms, null)

        /**
         * Folds the [Term]s in [terms] from left to right, creating binary structures having [operator] as functor.
         * Let `f` be the value of [operator], and let `t_i` be the `i`-th term in [terms].
         * Then, if [terminal] is non-null, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n, T) ...))
         * ```
         * where `T` is the value of [terminal], and `t_n` is the last item in [terms].
         * Conversely, if [terminal] is null, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n-1, t_n) ...))
         * ```
         * Of course, this method will return an instance of either [List], or [Tuple] in case the arguments [operator]
         * and [terminal] are adequate.
         * @param operator the functor of the [Struct]ures used to fold the [terms]
         * @param terms the [Iterable] of [Terms] to be folded
         * @param terminal the termination [Term] used as the deepest one in the returned [Struct], or `null`
         * if `t_n` should be used instead
         * @throws IllegalArgumentException if [terms] has less than 2 items
         * @return a new [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("foldIterable")
        fun fold(
            operator: String,
            terms: Iterable<Term>,
            terminal: Term?,
        ): Struct = fold(operator, terms.toList(), terminal)

        /**
         * Folds the [Term]s in [terms] from left to right, creating binary structures having [operator] as functor.
         * Let `f` be the value of [operator], and let `t_i` be the `i`-th term in [terms].
         * Then, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n-1, t_n) ...))
         * ```
         * Of course, this method will return an instance of either [Tuple] in case the argument [operator] is adequate.
         * @param operator the functor of the [Struct]ures used to fold the [terms]
         * @param terms the [Iterable] of [Terms] to be folded
         * @throws IllegalArgumentException if [terms] has less than 2 items
         * @return a new [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("foldIterableNullTerminated")
        fun fold(
            operator: String,
            terms: Iterable<Term>,
        ): Struct = fold(operator, terms, null)

        /**
         * Folds the [Term]s in [terms] from left to right, creating binary structures having [operator] as functor.
         * Let `f` be the value of [operator], and let `t_i` be the `i`-th term in [terms].
         * Then, if [terminal] is non-null, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n, T) ...))
         * ```
         * where `T` is the value of [terminal], and `t_n` is the last item in [terms].
         * Conversely, if [terminal] is null, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n-1, t_n) ...))
         * ```
         * Of course, this method will return an instance of either [List], or [Tuple] in case the arguments [operator]
         * and [terminal] are adequate.
         * @param operator the functor of the [Struct]ures used to fold the [terms]
         * @param terms the `vararg` array of [Terms] to be folded
         * @param terminal the termination [Term] used as the deepest one in the returned [Struct], or `null`
         * if `t_n` should be used instead
         * @throws IllegalArgumentException if [terms] has less than 2 items
         * @return a new [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("fold")
        fun fold(
            operator: String,
            vararg terms: Term,
            terminal: Term?,
        ): Struct = fold(operator, terms.toList(), terminal)

        /**
         * Folds the [Term]s in [terms] from left to right, creating binary structures having [operator] as functor.
         * Let `f` be the value of [operator], and let `t_i` be the `i`-th term in [terms].
         * Then, this method constructs the [Struct]ure
         * ```
         * f(t_1, f(t_2, ... f(t_n-1, t_n) ...))
         * ```
         * Of course, this method will return an instance of either [Tuple] in case the argument [operator] is adequate.
         * @param operator the functor of the [Struct]ures used to fold the [terms]
         * @param terms the `vararg` array of [Terms] to be folded
         * @throws IllegalArgumentException if [terms] has less than 2 items
         * @return a new [Struct] (or of some particular sub-type of [Struct])
         */
        @JvmStatic
        @JsName("foldNullTerminated")
        fun fold(
            operator: String,
            vararg terms: Term,
        ): Struct = fold(operator, listOf(*terms))
    }
}
