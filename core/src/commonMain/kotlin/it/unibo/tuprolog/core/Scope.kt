package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.ScopeImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.core.List as LogicList

/**
 * A small, stateful factory of [Term]s that remembers the [Var]iables it has already created, by [Var.name].
 *
 * [Var.of] always creates a brand-new variable, even when called twice with the same name (see [Var] for why).
 * That is correct in general, but gets in the way whenever the *same* variable must occur more than once
 * while building a term or a clause — e.g. `member(H, [_|T]) :- member(H, T).`, where `H` and `T` each occur
 * twice. [Scope] solves this: asking it for [varOf] with a name it has already seen returns the very same
 * [Var] instance, instead of minting a new, unrelated one. Every other factory method it exposes
 * ([structOf], [ruleOf], [logicListOf], [numOf], and so on) mirrors the corresponding static factory found on
 * the relevant [Term] subtype, so building terms inside a [Scope] reads exactly like building them outside of
 * one — variable reuse just becomes automatic. [anonymous] is the one deliberate exception: each call still
 * returns a fresh, unrelated variable, since anonymous variables are never meant to be shared.
 *
 * A [Scope] is intentionally mutable and meant to be used once, for one self-contained unit of construction
 * (typically: one clause). It is not meant to be reset or reused across unrelated terms. `Term.freshCopy()`
 * and `Term.freshCopy(Scope)` are implemented on top of [Scope] for the very same reason: a fresh, empty
 * scope guarantees that repeated occurrences of a variable are refreshed consistently.
 *
 * Usage example, building the `member/2` clause above and having `H`/`T` refer to the same variables in both
 * head and body:
 * ```
 * Scope.empty {
 *     ruleOf(
 *         structOf("member", varOf("H"), consOf(anonymous(), varOf("T"))),
 *         structOf("member", varOf("H"), varOf("T")),
 *     )
 * }
 * ```
 *
 * @see Var
 * @see VariablesProvider
 */
interface Scope {
    /** The [Var]iables created so far through this [Scope], indexed by their simple [Var.name]. */
    @JsName("variables")
    val variables: Map<String, Var>

    /** Shorthand for [Truth.FAIL]. */
    @JsName("fail")
    val fail: Truth

    /** Shorthand for the empty logic list, i.e. [EmptyList]. */
    @JsName("emptyLogicList")
    val emptyLogicList: EmptyList

    /** Shorthand for the empty logic block, i.e. [EmptyBlock]. */
    @JsName("emptyBlock")
    val emptyBlock: EmptyBlock

    /**
     * Shorthand for [anonymous], letting Kotlin code refer to a fresh anonymous variable as `_`,
     * mirroring Prolog's own syntax.
     */
    @Suppress("PropertyName")
    @JsName("_")
    val `_`: Var
        get() = anonymous()

    /** Checks whether [variable] was created through this [Scope]. */
    @JsName("containsVar")
    operator fun contains(variable: Var): Boolean

    /** Checks whether a variable named [variable] was created through this [Scope]. */
    @JsName("contains")
    operator fun contains(variable: String): Boolean

    /** Retrieves the [Var] named [variable] previously created through this [Scope], or `null` if none exists yet. */
    @JsName("get")
    operator fun get(variable: String): Var?

    /**
     * Runs [lambda] with this [Scope] as its receiver, for its side effects, then returns this same [Scope].
     * Useful to populate a [Scope] with variables/terms without capturing the result of the last expression.
     */
    @JsName("where")
    fun where(lambda: Scope.() -> Unit): Scope

    /**
     * Runs [lambda] with this [Scope] as its receiver, returning whatever [lambda] produces.
     * This is the idiomatic way of using a [Scope]: `Scope.empty().with { ruleOf(...) }`,
     * or more concisely `Scope.empty { ruleOf(...) }` via the companion overloads.
     */
    @JsName("with")
    fun <R> with(lambda: Scope.() -> R): R

    /** Retrieves the [Var] named [name], creating and caching a new one on first request. */
    @JsName("varOf")
    fun varOf(name: String): Var

    /** Retrieves the [Var] named [name], creating and caching a new one on first request. */
    @JsName("varOfChar")
    fun varOf(name: Char): Var

    /** Creates an [Atom] with the given [value]. See [Atom.of]. */
    @JsName("atomOf")
    fun atomOf(value: String): Atom

    /** Creates an [Atom] with the given [value]. See [Atom.of]. */
    @JsName("atomOfChar")
    fun atomOf(value: Char): Atom

    /** Creates a [Struct] with the given [functor] and [args]. See [Struct.of]. */
    @JsName("structOf")
    fun structOf(
        functor: String,
        vararg args: Term,
    ): Struct

    /** Creates a [Struct] with the given [functor] and [args]. See [Struct.of]. */
    @JsName("structOfSequence")
    fun structOf(
        functor: String,
        args: Sequence<Term>,
    ): Struct

    /** Creates a [Struct] with the given [functor] and [args]. See [Struct.of]. */
    @JsName("structOfIterable")
    fun structOf(
        functor: String,
        args: Iterable<Term>,
    ): Struct

    /** Creates a [Struct] with the given [functor] and [args]. See [Struct.of]. */
    @JsName("structOfList")
    fun structOf(
        functor: String,
        args: List<Term>,
    ): Struct

    /** Creates a [Tuple] out of [terms]. See [Tuple.of]. */
    @JsName("tupleOf")
    fun tupleOf(vararg terms: Term): Tuple

    /** Creates a [Tuple] out of [terms]. See [Tuple.of]. */
    @JsName("tupleOfIterable")
    fun tupleOf(terms: Iterable<Term>): Tuple

    /** Creates a [Tuple] out of [terms]. See [Tuple.of]. */
    @JsName("tupleOfSequence")
    fun tupleOf(terms: Sequence<Term>): Tuple

    /** Creates a logic [LogicList] out of [terms]. See [List.of]. */
    @JsName("logicListOf")
    fun logicListOf(vararg terms: Term): LogicList

    /** Creates a logic [LogicList] out of [terms]. See [List.of]. */
    @JsName("logicListOfIterable")
    fun logicListOf(terms: Iterable<Term>): LogicList

    /** Creates a logic [LogicList] out of [terms]. See [List.of]. */
    @JsName("logicListOfSequence")
    fun logicListOf(terms: Sequence<Term>): LogicList

    /** Creates a (possibly partial) logic [LogicList] out of [terms], terminated by [last]. See [List.from]. */
    @JsName("logicListFromFrom")
    fun logicListFrom(
        vararg terms: Term,
        last: Term? = null,
    ): LogicList

    /** Creates a (possibly partial) logic [LogicList] out of [terms], terminated by [last]. See [List.from]. */
    @JsName("logicListFromIterable")
    fun logicListFrom(
        terms: Iterable<Term>,
        last: Term? = null,
    ): LogicList

    /** Creates a (possibly partial) logic [LogicList] out of [terms], terminated by [last]. See [List.from]. */
    @JsName("logicListFromSequence")
    fun logicListFrom(
        terms: Sequence<Term>,
        last: Term? = null,
    ): LogicList

    /** Creates a [Block] out of [terms]. See [Block.of]. */
    @JsName("blockOf")
    fun blockOf(vararg terms: Term): Block

    /** Creates a [Block] out of [terms]. See [Block.of]. */
    @JsName("blockOfIterable")
    fun blockOf(terms: Iterable<Term>): Block

    /** Creates a [Block] out of [terms]. See [Block.of]. */
    @JsName("blockOfSequence")
    fun blockOf(terms: Sequence<Term>): Block

    /** Creates a [Fact] with the given [head]. See [Fact.of]. */
    @JsName("factOf")
    fun factOf(head: Struct): Fact

    /** Creates a [Rule] with the given [head] and body. See [Rule.of]. */
    @JsName("ruleOf")
    fun ruleOf(
        head: Struct,
        body1: Term,
        vararg body: Term,
    ): Rule

    /** Creates a [Directive] with the given body. See [Directive.of]. */
    @JsName("directiveOf")
    fun directiveOf(
        body1: Term,
        vararg body: Term,
    ): Directive

    /** Creates a [Clause] with the given (possibly `null`) [head] and body. See [Clause.of]. */
    @JsName("clauseOf")
    fun clauseOf(
        head: Struct?,
        vararg body: Term,
    ): Clause

    /** Creates a [Cons] with the given [head] and [tail]. See [Cons.of]. */
    @JsName("consOf")
    fun consOf(
        head: Term,
        tail: Term,
    ): Cons

    /** Creates an [Indicator] with the given [name] and [arity]. See [Indicator.of]. */
    @JsName("indicatorOf")
    fun indicatorOf(
        name: Term,
        arity: Term,
    ): Indicator

    /** Creates an [Indicator] with the given [name] and [arity]. See [Indicator.of]. */
    @JsName("indicatorOfStringInt")
    fun indicatorOf(
        name: String,
        arity: Int,
    ): Indicator

    /** Creates a fresh, unrelated anonymous [Var]. See [Var.anonymous]. */
    @JsName("anonymous")
    fun anonymous(): Var

    /** Alias for [anonymous]. */
    @JsName("whatever")
    fun whatever(): Var

    /** Creates a [Real] out of [value]. See [Numeric.of]. */
    @JsName("numOfBigDecimal")
    fun numOf(value: BigDecimal): Real

    /** Creates a [Real] out of [value]. See [Numeric.of]. */
    @JsName("numOfDouble")
    fun numOf(value: Double): Real

    /** Creates a [Real] out of [value]. See [Numeric.of]. */
    @JsName("numOfFloat")
    fun numOf(value: Float): Real

    /** Creates an [Integer] out of [value]. See [Numeric.of]. */
    @JsName("numOfBigInteger")
    fun numOf(value: BigInteger): Integer

    /** Creates an [Integer] out of [value]. See [Numeric.of]. */
    @JsName("numOfInt")
    fun numOf(value: Int): Integer

    /** Creates an [Integer] out of [value]. See [Numeric.of]. */
    @JsName("numOfLong")
    fun numOf(value: Long): Integer

    /** Creates an [Integer] out of [value]. See [Numeric.of]. */
    @JsName("numOfShort")
    fun numOf(value: Short): Integer

    /** Creates an [Integer] out of [value]. See [Numeric.of]. */
    @JsName("numOfByte")
    fun numOf(value: Byte): Integer

    /**
     * Parses [value] into a [Numeric], preferring [Integer] and falling back to [Real]. See [Numeric.of].
     * @throws NumberFormatException if [value] cannot be parsed as either
     */
    @JsName("parseNum")
    fun numOf(value: String): Numeric

    /** Creates a [Numeric] out of [value]. See [Numeric.of]. */
    @JsName("numOf")
    fun numOf(value: Number): Numeric

    /** Creates an [Integer] out of [value]. See [Integer.of]. */
    @JsName("intOfBigInteger")
    fun intOf(value: BigInteger): Integer

    /** Creates an [Integer] out of [value]. See [Integer.of]. */
    @JsName("intOf")
    fun intOf(value: Int): Integer

    /** Creates an [Integer] out of [value]. See [Integer.of]. */
    @JsName("intOfLong")
    fun intOf(value: Long): Integer

    /** Creates an [Integer] out of [value]. See [Integer.of]. */
    @JsName("intOfShort")
    fun intOf(value: Short): Integer

    /** Creates an [Integer] out of [value]. See [Integer.of]. */
    @JsName("intOfByte")
    fun intOf(value: Byte): Integer

    /**
     * Parses [value] into an [Integer]. See [Integer.of].
     * @throws NumberFormatException if [value] is not a valid integer literal
     */
    @JsName("parseInt")
    fun intOf(value: String): Integer

    /**
     * Parses [value], expressed in the given [radix], into an [Integer]. See [Integer.of].
     * @throws NumberFormatException if [value] is not a valid integer literal in [radix]
     */
    @JsName("parseIntRadix")
    fun intOf(
        value: String,
        radix: Int,
    ): Integer

    /** Creates a [Real] out of [value]. See [Real.of]. */
    @JsName("realOfBigDecimal")
    fun realOf(value: BigDecimal): Real

    /** Creates a [Real] out of [value]. See [Real.of]. */
    @JsName("realOf")
    fun realOf(value: Double): Real

    /** Creates a [Real] out of [value]. See [Real.of]. */
    @JsName("realOfFloat")
    fun realOf(value: Float): Real

    /**
     * Parses [value] into a [Real]. See [Real.of].
     * @throws NumberFormatException if [value] is not a valid real literal
     */
    @JsName("parseReal")
    fun realOf(value: String): Real

    /** Creates a [Truth] out of [value]. See [Truth.of]. */
    @JsName("truthOf")
    fun truthOf(value: Boolean): Truth

    /**
     * Creates a [Substitution.Unifier] out of the given [Var.name]-[Term] [assignments].
     * @throws it.unibo.tuprolog.core.exception.SubstitutionException if [assignments] are contradictory
     */
    @JsName("unifierOf")
    fun unifierOf(vararg assignments: Pair<String, Term>): Substitution.Unifier

    /** Creates a [Substitution] out of the given [Var.name]-[Term] [assignments]. */
    @JsName("substitutionOf")
    fun substitutionOf(vararg assignments: Pair<String, Term>): Substitution

    /**
     * Creates a [Substitution.Unifier] out of the given [Var]-[Term] [assignments].
     * @throws it.unibo.tuprolog.core.exception.SubstitutionException if [assignments] are contradictory
     */
    @JsName("unifierOfIterable")
    fun unifierOf(assignments: Iterable<Pair<Var, Term>>): Substitution.Unifier

    /**
     * Creates a [Substitution.Unifier] out of the given [Var]-[Term] [assignments].
     * @throws it.unibo.tuprolog.core.exception.SubstitutionException if [assignments] are contradictory
     */
    @JsName("unifierOfSequence")
    fun unifierOf(assignments: Sequence<Pair<Var, Term>>): Substitution.Unifier

    /** Creates a [Substitution] out of the given [Var]-[Term] [assignments]. */
    @JsName("substitutionOfIterable")
    fun substitutionOf(assignments: Iterable<Pair<Var, Term>>): Substitution

    /** Creates a [Substitution] out of the given [Var]-[Term] [assignments]. */
    @JsName("substitutionOfSequence")
    fun substitutionOf(assignments: Sequence<Pair<Var, Term>>): Substitution

    /** Factory functions for creating [Scope]s. */
    companion object {
        /** Creates a new, empty [Scope], with no [Var]iables cached yet. */
        @JvmStatic
        @JsName("empty")
        fun empty(): Scope = ScopeImpl(mutableMapOf())

        /** Creates a new [Scope], pre-populated with fresh variables named after [vars]. */
        @JvmStatic
        @JsName("of")
        fun of(vararg vars: String): Scope = of(vars.map { Var.of(it) })

        /** Creates a new [Scope], pre-populated with the given [Var]s, indexed by their [Var.name]. */
        @JvmStatic
        @JsName("ofVar")
        fun of(
            `var`: Var,
            vararg vars: Var,
        ): Scope = of(listOf(`var`, *vars))

        /** Creates a new [Scope], pre-populated with the given [vars], indexed by their [Var.name]. */
        @JvmStatic
        @JsName("ofVarIterable")
        fun of(vars: Iterable<Var>): Scope = ScopeImpl(vars.associateByTo(mutableMapOf()) { it.name })

        /** Creates a new [Scope], pre-populated with the given [vars], indexed by their [Var.name]. */
        @JvmStatic
        @JsName("ofVarSequence")
        fun of(vars: Sequence<Var>): Scope = of(vars.asIterable())

        /** Creates a new [Scope], pre-populated with the [variabled]'s [Variabled.variables]. */
        @JvmStatic
        @JsName("ofVariabled")
        fun of(variabled: Variabled): Scope = of(variabled.variables)

        /** Shorthand for `empty().with(lambda)`. See [with]. */
        @JvmStatic
        @JsName("emptyWithFunction")
        fun <R> empty(lambda: Scope.() -> R): R = empty().with(lambda)

        /** Shorthand for `of(*vars).with(lambda)`. See [with]. */
        @JvmStatic
        @JsName("ofWithFunction")
        fun <R> of(
            vararg vars: String,
            lambda: Scope.() -> R,
        ): R = of(*vars).with(lambda)

        /** Shorthand for `of(var, *vars).with(lambda)`. See [with]. */
        @JvmStatic
        @JsName("ofVarWithFunction")
        fun <R> of(
            `var`: Var,
            vararg vars: Var,
            lambda: Scope.() -> R,
        ): R = of(`var`, *vars).with(lambda)

        /** Shorthand for `of(vars).with(lambda)`. See [with]. */
        @JvmStatic
        @JsName("ofVarIterableWithFunction")
        fun <R> of(
            vars: Iterable<Var>,
            lambda: Scope.() -> R,
        ): R = of(vars).with(lambda)

        /** Shorthand for `of(vars).with(lambda)`. See [with]. */
        @JvmStatic
        @JsName("ofVarSequenceWithFunction")
        fun <R> of(
            vars: Sequence<Var>,
            lambda: Scope.() -> R,
        ): R = of(vars).with(lambda)

        /** Shorthand for `of(vars).with(lambda)`. See [with]. */
        @JvmStatic
        @JsName("ofVariabledWithFunction")
        fun <R> of(
            vars: Variabled,
            lambda: Scope.() -> R,
        ): R = of(vars).with(lambda)
    }
}
