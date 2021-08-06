package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.exception.SubstitutionApplicationException
import it.unibo.tuprolog.utils.Castable
import it.unibo.tuprolog.utils.Taggable
import kotlin.js.JsName

/**
 * Base type for all logic terms.
 * [Term]s are immutable tree-like data structures.
 */
interface Term : Comparable<Term>, Taggable<Term>, Castable<Term> {

    /**
     * Empty companion aimed at letting extensions be injected through extension methods
     */
    companion object

    /**
     * Helper method aimed at down-casting [Term]s using a fluent style
     * @param T must be a subtype of [Term]
     * @return the current term, cast'd into type [T], or `null`, in case the current term is not an instance of [T]
     */
    @Suppress("RedundantOverride")
    override fun <T : Term> `as`(): T? = super.`as`<T>()

    /**
     * Helper method aimed at down-casting [Term]s using a fluent style
     * @param T must be a subtype of [Term]
     * @return the current term, cast'd into type [T]
     * @throws ClassCastException if the current term is not of type [T]
     */
    @Suppress("RedundantOverride")
    override fun <T : Term> castTo(): T = super.castTo<T>()

    /**
     * Compares this term to the provided one, returning a positive integer if this term _precedes_ [other],
     * a negative integer if [other] precedes this term, or `0` otherwise
     * @param other is the term to be compared against this term
     * @return an [Int] indicating whether this term precedes [other] or not
     */
    override fun compareTo(other: Term): Int =
        TermComparator.DefaultComparator.compare(this, other)

    /**
     * Checks whether an[other] term is _equals_ to the current one or not,
     * by explicitly letting the client decide whether to rely or not on [Var]riables
     * complete names for checking equality among two [Var]iables.
     * If [useVarCompleteName] is `true`, [Var]iables are compared through their
     * [Var.completeName] property. Otherwise, they are compared through their
     * [Var.name] property. Other sorts of terms are compared as `Term.equals(Any?)`.
     *
     * For example, if [useVarCompleteName] is `true` the following comparison should fail:
     * ```
     * Var.of("X") == Var.of("X")
     * ```
     * otherwise, it should succeed.
     *
     * @param other is the [Term] the current [Term] should be compared with
     * @param useVarCompleteName indicates whether [Var] should be compared through their
     * [Var.completeName] property or through their [Var.name] property
     *
     * @return `true` if the two terms are equal, or `false`, otherwise
     */
    @JsName("equalsUsingVarCompleteNames")
    fun equals(other: Term, useVarCompleteName: Boolean): Boolean

    /**
     * Checks whether an[other] term is _structurally equals_ to the current one or not.
     * Structural equivalence is a looser type of equivalence (w.r.t. term equivalence)
     * where:
     * - numbers are compared by value, e.g. `1` is structurally equal to `1.0`
     * - variables are always considered equal, e.g. `f(X)` is structurally equal to `f(_)`
     * @param other is the [Term] the current [Term] should be compared with
     * @return `true` if the two terms are structurally equal, or `false`, otherwise
     */
    @JsName("structurallyEquals")
    infix fun structurallyEquals(other: Term): Boolean

    /** The sequence of [Var]iables directly or indirectly contained in the current term.
     * Variables are lazily returned in a non-deterministic order.
     * Notice that no occurrence-check is performed.
     * Thus, if a [Term] contains the same [Var]iable twice or more times, then the [variables] sequence
     * may contain as many occurrences of that [Var]iable
     *
     * @return a [Sequence] of [Var]
     */
    @JsName("variables")
    val variables: Sequence<Var>

    /**
     * Checks whether the current term is a variable.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Var].
     * @return `true` if the current term is a variable, or `false`, otherwise
     */
    @JsName("isVar")
    val isVar: Boolean get() = false

    /**
     * Checks whether the current term is ground.
     * A term is ground is ground if and only if it does not contain any variable.
     * This method is guaranteed to return `true` if and only if the [variables] property
     * of the current term refers to an empty sequence.
     * @return `true` if the current term is ground, or `false`, otherwise
     */
    @JsName("isGround")
    val isGround: Boolean get() = variables.none()

    /**
     * Checks whether the current term is a structure, i.e., either a compound term or an atom.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Struct].
     * @return `true` if the current term is a structure, or `false`, otherwise
     */
    @JsName("isStruct")
    val isStruct: Boolean get() = false

    /**
     * Checks whether the current term is a truth value.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Truth].
     * @return `true` if the current term is a truth value, or `false`, otherwise
     */
    @JsName("isTruth")
    val isTruth: Boolean get() = false

    /**
     * Checks whether the current term is a recursive structure, i.e., a list, a tuple, or a block.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Recursive].
     * @return `true` if the current term is a recursive structure, or `false`, otherwise
     */
    @JsName("isRecursive")
    val isRecursive: Boolean get() = false

    /**
     * Checks whether the current term is an atom.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Atom].
     * @return `true` if the current term is an atom, or `false`, otherwise
     */
    @JsName("isAtom")
    val isAtom: Boolean get() = false

    /**
     * Checks whether the current term is a constant, i.e., either an atom or a number.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Constant].
     * @return `true` if the current term is a constant, or `false`, otherwise
     */
    @JsName("isConstant")
    val isConstant: Boolean get() = false

    /**
     * Checks whether the current term is a number, i.e., either an integer or a real number.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Numeric].
     * @return `true` if the current term is a number, or `false`, otherwise
     */
    @JsName("isNumber")
    val isNumber: Boolean get() = false

    /**
     * Checks whether the current term is an integer.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Integer].
     * @return `true` if the current term is an integer number, or `false`, otherwise
     */
    @JsName("isInteger")
    val isInteger: Boolean get() = false

    /**
     * Checks whether the current term is a real number.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Real].
     * @return `true` if the current term is a real number, or `false`, otherwise
     */
    @JsName("isReal")
    val isReal: Boolean get() = false

    /**
     * Checks whether the current term is a (logic) list, i.e., either an empty list or a [Cons].
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [List].
     * @return `true` if the current term is a logic list, or `false`, otherwise
     */
    @JsName("isList")
    val isList: Boolean get() = false

    /**
     * Checks whether the current term is a logic tuple, i.e., a right-recursive conjunction of 2 or more terms.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Tuple].
     * @return `true` if the current term is a logic tuple, or `false`, otherwise
     */
    @JsName("isTuple")
    val isTuple: Boolean get() = false

    /**
     * Checks whether the current term is a logic block.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Block].
     * @return `true` if the current term is a logic block, or `false`, otherwise
     */
    @JsName("isBlock")
    val isBlock: Boolean get() = false

    /**
     * Checks whether the current term is an empty logic block.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [EmptyBlock].
     * @return `true` if the current term is an empty logic block, or `false`, otherwise
     */
    @JsName("isEmptyBlock")
    val isEmptyBlock: Boolean get() = false

    /**
     * Checks whether the current term is a clause, i.e., either a rule or a directive.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Clause].
     * @return `true` if the current term is a clause, or `false`, otherwise
     */
    @JsName("isClause")
    val isClause: Boolean get() = false

    /**
     * Checks whether the current term is a rule, or a fact.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Rule].
     * @return `true` if the current term is a rule, or `false`, otherwise
     */
    @JsName("isRule")
    val isRule: Boolean get() = false

    /**
     * Checks whether the current term is a fact.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Fact].
     * @return `true` if the current term is a fact, or `false`, otherwise
     */
    @JsName("isFact")
    val isFact: Boolean get() = false

    /**
     * Checks whether the current term is a directive.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Directive].
     * @return `true` if the current term is a directive, or `false`, otherwise
     */
    @JsName("isDirective")
    val isDirective: Boolean get() = false

    /**
     * Checks whether the current term is a cons.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Cons].
     * @return `true` if the current term is a cons, or `false`, otherwise
     */
    @JsName("isCons")
    val isCons: Boolean get() = false

    /**
     * Checks whether the current term is an empty logic list.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [EmptyList].
     * @return `true` if the current term is an empty logic list, or `false`, otherwise
     */
    @JsName("isEmptyList")
    val isEmptyList: Boolean get() = false

    /**
     * Checks whether the current term is the `true` atom.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Truth] and its [Truth.value] is `"true"`.
     * @return `true` if the current term is `"true"`, or `false`, otherwise
     */
    @JsName("isTrue")
    val isTrue: Boolean get() = false

    /**
     * Checks whether the current term is the either the `fail` atom or the `false` atom.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Truth] and its [Truth.value] is `"fail"` or `"false"`.
     * @return `true` if the current term is either `"fail"` or `"false"`, or `false`, otherwise
     */
    @JsName("isFail")
    val isFail: Boolean get() = false

    /**
     * Checks whether the current term is an indicator.
     * This method is guaranteed to return `true` if and only if the current term
     * is an instance of [Indicator].
     * @return `true` if the current term is an indicator, or `false`, otherwise
     */
    @JsName("isIndicator")
    val isIndicator: Boolean get() = false

    /**
     * Returns a fresh copy of this Term, that is, an instance of Term which is equal to the current one in any aspect,
     * except variables directly or indirectly contained into this Term, which are refreshed.
     * This means that it could return itself, if no variable is present (ground term), or a new Term with freshly
     * generated variables.
     *
     * Variables are refreshed consistently, meaning that, if more variables exists within this Term having the same
     * name, all fresh copies of such variables will have the same complete name.
     *
     * Example: "f(X, g(X))".freshCopy() returns something like "f(X_1, g(X_1))" instead of "f(X_1, g(X_2))"
     *
     * Notice that, if the current term is ground, the same object may be returned as a result by this method.
     *
     * @return a fresh copy of the current term which is different because variables are consistently renamed
     */
    @JsName("freshCopy")
    fun freshCopy(): Term

    /**
     * Returns a fresh copy of this Term, similarly to `freshCopy`, possibly reusing variables from the provided scope,
     * if any
     *
     * @see freshCopy
     * @param scope the Scope containing variables to be used in copying
     * @return a fresh copy of the current term which is different because variables are consistently renamed
     */
    @JsName("freshCopyFromScope")
    fun freshCopy(scope: Scope): Term

    /**
     * Applies a [Substitution] to the current term, producing a new [Term] which differs from the current
     * one because variables are replaced by their values, according to the binding carried by [substitution].
     *
     * Notice that, if the current term is ground, or the provided substitution is empty,
     * the same object may be returned as a result by this method.
     *
     * @param substitution is the [Substitution] to be applied to the current term
     * @return a [Term] where variables in [substitution] are replaced by their values
     * @throws [SubstitutionApplicationException] if the provided substitution is of type [Substitution.Fail]
     */
    @JsName("applySubstitution")
    fun apply(substitution: Substitution): Term

    /**
     * Applies one or more [Substitution]s to the current term, producing a new [Term] which differs from the current
     * one because variables are replaced by their values, according to the binding carried by the provided substitutions.
     *
     * This method behaves like [apply], assuming that the provided substitutions have been merged
     * by means of [Substitution.of].
     *
     * @param substitution is the first [Substitution] to be applied to the current term
     * @param substitutions is the vararg argument representing the 2nd, 3rd, etc., [Substitution]s to be applied
     * @return a [Term] where variables in [substitution] are replaced by their values
     * @throws [SubstitutionApplicationException] if the composition of the provided substitutions is of type [Substitution.Fail]
     *
     * @see apply
     * @see Substitution.of
     */
    @JsName("apply")
    fun apply(substitution: Substitution, vararg substitutions: Substitution): Term =
        apply(Substitution.of(substitution, *substitutions))

    /**
     * This is an alias for [apply] aimed at supporting a square-brackets syntax for substitutions applications in
     * Kotlin programs.
     * It lets programmers write `term[substitution]` instead of `term.apply(substitution)`.
     * It applies one or more [Substitution]s to the current term, producing a new [Term] which differs from the current
     * one because variables are replaced by their values, according to the binding carried by the provided substitutions.
     *
     * This method behaves like [apply], assuming that the provided substitutions have been merged
     * by means of [Substitution.of].
     *
     * @param substitution is the first [Substitution] to be applied to the current term
     * @param substitutions is the vararg argument representing the 2nd, 3rd, etc., [Substitution]s to be applied
     * @return a [Term] where variables in [substitution] are replaced by their values
     * @throws [SubstitutionApplicationException] if the composition of the provided substitutions is of type [Substitution.Fail]
     *
     * @see apply
     * @see Substitution.of
     */
    @JsName("getSubstituted")
    operator fun get(substitution: Substitution, vararg substitutions: Substitution): Term =
        apply(substitution, *substitutions)

    /**
     * Lets the provided [TermVisitor] navigate the current term and build an object of type [T].
     * Such an object is then returned as a result by this method.
     *
     * See also: [https://en.wikipedia.org/wiki/Visitor_pattern](https://en.wikipedia.org/wiki/Visitor_pattern)
     * for more information concerning the GoF's Visitor pattern.
     *
     * @param visitor is a [TermVisitor], i.e., an object aimed at navigating the current term
     * @param T is the type of the object built by [visitor] through its visit
     * @return an object of type [T], produced by [visitor] through its visit
     */
    @JsName("accept")
    fun <T> accept(visitor: TermVisitor<T>): T

    /**
     * Casts the current [Term] to [Atom], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Atom]
     * @return the current [Term], casted to [Atom]
     */
    @JsName("castToAtom")
    fun castToAtom(): Atom =
        asAtom() ?: throw ClassCastException("Cannot cast $this to ${Atom::class.simpleName}")

    /**
     * Casts the current [Term] to [Clause], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Clause]
     * @return the current [Term], casted to [Clause]
     */
    @JsName("castToClause")
    fun castToClause(): Clause =
        asClause() ?: throw ClassCastException("Cannot cast $this to ${Clause::class.simpleName}")

    /**
     * Casts the current [Term] to [Cons], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Cons]
     * @return the current [Term], casted to [Cons]
     */
    @JsName("castToCons")
    fun castToCons(): Cons =
        asCons() ?: throw ClassCastException("Cannot cast $this to ${Cons::class.simpleName}")

    /**
     * Casts the current [Term] to [Constant], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Constant]
     * @return the current [Term], casted to [Constant]
     */
    @JsName("castToConstant")
    fun castToConstant(): Constant =
        asConstant() ?: throw ClassCastException("Cannot cast $this to ${Constant::class.simpleName}")

    /**
     * Casts the current [Term] to [Directive], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Directive]
     * @return the current [Term], casted to [Directive]
     */
    @JsName("castToDirective")
    fun castToDirective(): Directive =
        asDirective() ?: throw ClassCastException("Cannot cast $this to ${Directive::class.simpleName}")

    /**
     * Casts the current [Term] to [EmptyList], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [EmptyList]
     * @return the current [Term], casted to [EmptyList]
     */
    @JsName("castToEmptyList")
    fun castToEmptyList(): EmptyList =
        asEmptyList() ?: throw ClassCastException("Cannot cast $this to ${EmptyList::class.simpleName}")

    /**
     * Casts the current [Term] to [EmptyBlock], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [EmptyBlock]
     * @return the current [Term], casted to [EmptyBlock]
     */
    @JsName("castToEmptyBlock")
    fun castToEmptyBlock(): EmptyBlock =
        asEmptyBlock() ?: throw ClassCastException("Cannot cast $this to ${EmptyBlock::class.simpleName}")

    /**
     * Casts the current [Term] to [Fact], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Fact]
     * @return the current [Term], casted to [Fact]
     */
    @JsName("castToFact")
    fun castToFact(): Fact =
        asFact() ?: throw ClassCastException("Cannot cast $this to ${Fact::class.simpleName}")

    /**
     * Casts the current [Term] to [Indicator], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Indicator]
     * @return the current [Term], casted to [Indicator]
     */
    @JsName("castToIndicator")
    fun castToIndicator(): Indicator =
        asIndicator() ?: throw ClassCastException("Cannot cast $this to ${Indicator::class.simpleName}")

    /**
     * Casts the current [Term] to [Integer], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Integer]
     * @return the current [Term], casted to [Integer]
     */
    @JsName("castToInteger")
    fun castToInteger(): Integer =
        asInteger() ?: throw ClassCastException("Cannot cast $this to ${Integer::class.simpleName}")

    /**
     * Casts the current [Term] to [List], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [List]
     * @return the current [Term], casted to [List]
     */
    @JsName("castToList")
    fun castToList(): List =
        asList() ?: throw ClassCastException("Cannot cast $this to ${List::class.simpleName}")

    /**
     * Casts the current [Term] to [Numeric], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Numeric]
     * @return the current [Term], casted to [Numeric]
     */
    @JsName("castToNumeric")
    fun castToNumeric(): Numeric =
        asNumeric() ?: throw ClassCastException("Cannot cast $this to ${Numeric::class.simpleName}")

    /**
     * Casts the current [Term] to [Real], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Real]
     * @return the current [Term], casted to [Real]
     */
    @JsName("castToReal")
    fun castToReal(): Real =
        asReal() ?: throw ClassCastException("Cannot cast $this to ${Real::class.simpleName}")

    /**
     * Casts the current [Term] to [Rule], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Rule]
     * @return the current [Term], casted to [Rule]
     */
    @JsName("castToRule")
    fun castToRule(): Rule =
        asRule() ?: throw ClassCastException("Cannot cast $this to ${Rule::class.simpleName}")

    /**
     * Casts the current [Term] to [Block], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Block]
     * @return the current [Term], casted to [Block]
     */
    @JsName("castToBlock")
    fun castToBlock(): Block =
        asBlock() ?: throw ClassCastException("Cannot cast $this to ${Block::class.simpleName}")

    /**
     * Casts the current [Term] to [Struct], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Struct]
     * @return the current [Term], casted to [Struct]
     */
    @JsName("castToStruct")
    fun castToStruct(): Struct =
        asStruct() ?: throw ClassCastException("Cannot cast $this to ${Struct::class.simpleName}")

    /**
     * Casts the current [Term] to [Recursive], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Recursive]
     * @return the current [Term], casted to [Recursive]
     */
    @JsName("castToRecursive")
    fun castToRecursive(): Recursive =
        asRecursive() ?: throw ClassCastException("Cannot cast $this to ${Recursive::class.simpleName}")

    /**
     * Casts the current [Term] to [Term]
     * @return the current [Term]
     */
    @JsName("castToTerm")
    fun castToTerm(): Term = this

    /**
     * Casts the current [Term] to [Truth], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Truth]
     * @return the current [Term], casted to [Truth]
     */
    @JsName("castToTruth")
    fun castToTruth(): Truth =
        asTruth() ?: throw ClassCastException("Cannot cast $this to ${Truth::class.simpleName}")

    /**
     * Casts the current [Term] to [Tuple], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Tuple]
     * @return the current [Term], casted to [Tuple]
     */
    @JsName("castToTuple")
    fun castToTuple(): Tuple =
        asTuple() ?: throw ClassCastException("Cannot cast $this to ${Tuple::class.simpleName}")

    /**
     * Casts the current [Term] to [Var], if possible
     * @throws ClassCastException if the current [Term] is not an instance of [Var]
     * @return the current [Term], casted to [Var]
     */
    @JsName("castToVar")
    fun castToVar(): Var =
        asVar() ?: throw ClassCastException("Cannot cast $this to ${Var::class.simpleName}")

    /**
     * Casts the current [Term] to [Atom], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Atom], or `null`, if the current term is not an instance of [Atom]
     */
    @JsName("asAtom")
    fun asAtom(): Atom? = null

    /**
     * Casts the current [Term] to [Clause], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Clause], or `null`, if the current term is not an instance of [Clause]
     */
    @JsName("asClause")
    fun asClause(): Clause? = null

    /**
     * Casts the current [Term] to [Cons], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Cons], or `null`, if the current term is not an instance of [Cons]
     */
    @JsName("asCons")
    fun asCons(): Cons? = null

    /**
     * Casts the current [Term] to [Constant], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Constant], or `null`, if the current term is not an instance of [Constant]
     */
    @JsName("asConstant")
    fun asConstant(): Constant? = null

    /**
     * Casts the current [Term] to [Directive], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Directive], or `null`, if the current term is not an instance of [Directive]
     */
    @JsName("asDirective")
    fun asDirective(): Directive? = null

    /**
     * Casts the current [Term] to [EmptyList], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [EmptyList], or `null`, if the current term is not an instance of [EmptyList]
     */
    @JsName("asEmptyList")
    fun asEmptyList(): EmptyList? = null

    /**
     * Casts the current [Term] to [EmptyBlock], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [EmptyBlock], or `null`, if the current term is not an instance of [EmptyBlock]
     */
    @JsName("asEmptyBlock")
    fun asEmptyBlock(): EmptyBlock? = null

    /**
     * Casts the current [Term] to [Fact], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Fact], or `null`, if the current term is not an instance of [Fact]
     */
    @JsName("asFact")
    fun asFact(): Fact? = null

    /**
     * Casts the current [Term] to [Indicator], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Indicator], or `null`, if the current term is not an instance of [Indicator]
     */
    @JsName("asIndicator")
    fun asIndicator(): Indicator? = null

    /**
     * Casts the current [Term] to [Integer], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Integer], or `null`, if the current term is not an instance of [Integer]
     */
    @JsName("asInteger")
    fun asInteger(): Integer? = null

    /**
     * Casts the current [Term] to [List], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [List], or `null`, if the current term is not an instance of [List]
     */
    @JsName("asList")
    fun asList(): List? = null

    /**
     * Casts the current [Term] to [Numeric], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Numeric], or `null`, if the current term is not an instance of [Numeric]
     */
    @JsName("asNumeric")
    fun asNumeric(): Numeric? = null

    /**
     * Casts the current [Term] to [Real], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Real], or `null`, if the current term is not an instance of [Real]
     */
    @JsName("asReal")
    fun asReal(): Real? = null

    /**
     * Casts the current [Term] to [Rule], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Rule], or `null`, if the current term is not an instance of [Rule]
     */
    @JsName("asRule")
    fun asRule(): Rule? = null

    /**
     * Casts the current [Term] to [Block], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Block], or `null`, if the current term is not an instance of [Block]
     */
    @JsName("asBlock")
    fun asBlock(): Block? = null

    /**
     * Casts the current [Term] to [Struct], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Struct], or `null`, if the current term is not an instance of [Struct]
     */
    @JsName("asStruct")
    fun asStruct(): Struct? = null

    /**
     * Casts the current [Term] to [Recursive], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Recursive], or `null`, if the current term is not an instance of [Recursive]
     */
    @JsName("asRecursive")
    fun asRecursive(): Recursive? = null

    /**
     * Casts the current [Term] to [Term]
     * @return the current [Term]
     */
    @JsName("asTerm")
    fun asTerm(): Term = this

    /**
     * Casts the current [Term] to [Truth], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Truth], or `null`, if the current term is not an instance of [Truth]
     */
    @JsName("asTruth")
    fun asTruth(): Truth? = null

    /**
     * Casts the current [Term] to [Tuple], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Tuple], or `null`, if the current term is not an instance of [Tuple]
     */
    @JsName("asTuple")
    fun asTuple(): Tuple? = null

    /**
     * Casts the current [Term] to [Var], if possible, or returns `null` otherwise
     * @return the current [Term], casted to [Var], or `null`, if the current term is not an instance of [Var]
     */
    @JsName("asVar")
    fun asVar(): Var? = null

    override fun equals(other: Any?): Boolean // Leave this here to allow delegation in `: ... by`

    override fun hashCode(): Int // Leave this here to allow delegation in `: ... by`

    override fun toString(): String // Leave this here to allow delegation in `: ... by`
}
