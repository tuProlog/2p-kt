package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import kotlin.js.JsName
import kotlin.collections.plus as append

/**
 * Adds infix/operator builders mirroring Prolog's own arithmetic, relational and logical operators, so that
 * `Struct`s (and `Rule`s, for `if`/`impliedBy`) can be written as Kotlin expressions instead of nested
 * [BaseLogicProgrammingScope.toTerm]/`structOf` calls:
 * ```kotlin
 * logicProgramming {
 *     "X" `is` ("Y" + 1)                 // Struct: is(X, +(Y, 1))
 *     ("X" greaterThan 0) and ("Y" lowerThan 10) // Struct: ','(>(X, 0), <(Y, 10))
 *     "even"("X") `if` ("X" rem 2 eq 0)  // Rule: even(X) :- =(rem(X, 2), 0)
 * }
 * ```
 * Each Prolog operator is overloaded once per receiver type ([Term], [Any], [Number], [Boolean], [Char],
 * [String], ...) only where Kotlin's own operator-overload resolution requires it to disambiguate from
 * conflicting stdlib operators (e.g. `Number.plus`); the `Any`-receiver versions (`equalsTo`, `and`, `is`, ...)
 * cover every other case directly, [toTerm]-ing both operands.
 */
interface LogicProgrammingScopeWithOperators<S : LogicProgrammingScopeWithOperators<S>> : BaseLogicProgrammingScope<S> {
    /** Builds a `+`/2 [Struct], e.g. `1 + "X"`. */
    @JsName("termPlus")
    operator fun Term.plus(other: Any): Struct = structOf("+", this.toTerm(), other.toTerm())

    /** Same as the [Term] receiver overload of `plus` above, for a non-[Term] left-hand side. */
    @JsName("number")
    operator fun Number.plus(other: Any): Struct = toTerm() + other

    /** Same as the [Term] receiver overload of `plus` above, for a non-[Term] left-hand side. */
    @JsName("booleanPlus")
    operator fun Boolean.plus(other: Any): Struct = toTerm() + other

    /** Same as the [Term] receiver overload of `plus` above, for a non-[Term] left-hand side. */
    @JsName("charPlus")
    operator fun Char.plus(other: Any): Struct = toTerm() + other

    /** Builds a `-`/2 [Struct], e.g. `"X" - 1`. */
    @JsName("termMinus")
    operator fun Term.minus(other: Any): Struct = structOf("-", this.toTerm(), other.toTerm())

    /** Same as the [Term] receiver overload of `minus` above, for a non-[Term] left-hand side. */
    @JsName("numberMinus")
    operator fun Number.minus(other: Any): Struct = toTerm() - other

    /** Same as the [Term] receiver overload of `minus` above, for a non-[Term] left-hand side. */
    @JsName("booleanMinus")
    operator fun Boolean.minus(other: Any): Struct = toTerm() - other

    /** Same as the [Term] receiver overload of `minus` above, for a non-[Term] left-hand side. */
    @JsName("charMinus")
    operator fun Char.minus(other: Any): Struct = toTerm() - other

    /** Same as the [Term] receiver overload of `minus` above, for a non-[Term] left-hand side. */
    @JsName("stringMinus")
    operator fun String.minus(other: Any): Struct = toTerm() - other

    /** Builds a `*`/2 [Struct], e.g. `"X" * 2`. */
    @JsName("termTimes")
    operator fun Term.times(other: Any): Struct = structOf("*", this.toTerm(), other.toTerm())

    /** Same as the [Term] receiver overload of `times` above, for a non-[Term] left-hand side. */
    @JsName("numberTimes")
    operator fun Number.times(other: Any): Struct = toTerm() * other

    /** Same as the [Term] receiver overload of `times` above, for a non-[Term] left-hand side. */
    @JsName("booleanTimes")
    operator fun Boolean.times(other: Any): Struct = toTerm() * other

    /** Same as the [Term] receiver overload of `times` above, for a non-[Term] left-hand side. */
    @JsName("charTimes")
    operator fun Char.times(other: Any): Struct = toTerm() * other

    /** Same as the [Term] receiver overload of `times` above, for a non-[Term] left-hand side. */
    @JsName("stringTimes")
    operator fun String.times(other: Any): Struct = toTerm() * other

    /**
     * Builds an [Indicator] out of this [Term] (the name) and [other] (the arity), e.g. `"foo" / 2`. Note this
     * overload does *not* build a Prolog `/`/2 struct — use [structOf] directly for that.
     */
    @JsName("termDiv")
    operator fun Term.div(other: Any): Indicator = indicatorOf(this.toTerm(), other.toTerm())

    /** Builds a `/`/2 [Struct] (Prolog division), e.g. `"X" / 2`. Unlike the [Term] receiver overload of `div` above, this does not build an [Indicator]. */
    @JsName("numberDiv")
    operator fun Number.div(other: Any): Struct = toTerm() / other

    /** Same as the [Number] receiver overload of `div` above, for a non-[Number] left-hand side. */
    @JsName("booleanDiv")
    operator fun Boolean.div(other: Any): Struct = toTerm() / other

    /** Same as the [Number] receiver overload of `div` above, for a non-[Number] left-hand side. */
    @JsName("charDiv")
    operator fun Char.div(other: Any): Struct = toTerm() / other

    /** Same as the [Number] receiver overload of `div` above, for a non-[Number] left-hand side. */
    @JsName("stringDiv")
    operator fun String.div(other: Any): Struct = toTerm() / other

    /** Builds a [Struct] whose functor is `'='/2` (term unification operator). */
    @JsName("anyEqualsTo")
    infix fun Any.equalsTo(other: Any): Struct = structOf("=", this.toTerm(), other.toTerm())

    /** Builds a `\=`/2 [Struct] (term non-unifiability). */
    @JsName("anyNotEqualsTo")
    infix fun Any.notEqualsTo(other: Any): Struct = structOf("\\=", this.toTerm(), other.toTerm())

    /** Builds a `>`/2 [Struct] (arithmetic greater-than). */
    @JsName("anyGreaterThan")
    infix fun Any.greaterThan(other: Any): Struct = structOf(">", this.toTerm(), other.toTerm())

    /** Builds a `>=`/2 [Struct] (arithmetic greater-than-or-equal). */
    @JsName("anyGreaterThanOrEqualsTo")
    infix fun Any.greaterThanOrEqualsTo(other: Any): Struct = structOf(">=", this.toTerm(), other.toTerm())

    /** Alias of [greaterThanOrEqualsTo]. */
    @JsName("anyNonLowerThan")
    infix fun Any.nonLowerThan(other: Any): Struct = this greaterThanOrEqualsTo other

    /** Builds a `<`/2 [Struct] (arithmetic lower-than). */
    @JsName("anyLowerThan")
    infix fun Any.lowerThan(other: Any): Struct = structOf("<", this.toTerm(), other.toTerm())

    /** Builds an `=<`/2 [Struct] (arithmetic lower-than-or-equal). */
    @JsName("anyLowerThanOrEqualsTo")
    infix fun Any.lowerThanOrEqualsTo(other: Any): Struct = structOf("=<", this.toTerm(), other.toTerm())

    /** Alias of [lowerThanOrEqualsTo]. */
    @JsName("anyNonGreaterThan")
    infix fun Any.nonGreaterThan(other: Any): Struct = this lowerThanOrEqualsTo other

    /** Builds a `//`/2 [Struct] (integer division). */
    @JsName("anyIntDiv")
    infix fun Any.intDiv(other: Any): Struct = structOf("//", this.toTerm(), other.toTerm())

    /** Builds a `rem`/2 [Struct] (integer remainder). */
    @JsName("termRem")
    infix operator fun Term.rem(other: Any): Struct = structOf("rem", this.toTerm(), other.toTerm())

    /** Same as the [Term] receiver overload of `rem` above, for a non-[Term] left-hand side. */
    @JsName("numberRem")
    infix operator fun Number.rem(other: Any): Struct = toTerm() % other

    /** Same as the [Term] receiver overload of `rem` above, for a non-[Term] left-hand side. */
    @JsName("booleanRem")
    infix operator fun Boolean.rem(other: Any): Struct = toTerm() % other

    /** Same as the [Term] receiver overload of `rem` above, for a non-[Term] left-hand side. */
    @JsName("charRem")
    infix operator fun Char.rem(other: Any): Struct = toTerm() % other

    /** Same as the [Term] receiver overload of `rem` above, for a non-[Term] left-hand side. */
    @JsName("stringRem")
    infix operator fun String.rem(other: Any): Struct = toTerm() % other

    /**
     * Builds a conjunction (`, `/2, right-associative), i.e. a [Tuple]: if this value is already a [Tuple],
     * [other] is appended to it as one more item, rather than nesting a new 2-ary tuple around it — so
     * `a and b and c` flattens to a single 3-ary tuple `(a, b, c)` rather than `(a, (b, c))`.
     */
    @JsName("anyAnd")
    infix fun Any.and(other: Any): Struct =
        toTerm().let {
            val otherTerm = other.toTerm()
            if (it is Tuple) {
                tupleOf(it.items.append(otherTerm))
            } else {
                tupleOf(this.toTerm(), otherTerm)
            }
        }

    /** Builds a `;`/2 [Struct] (disjunction). */
    @JsName("anyOr")
    infix fun Any.or(other: Any): Struct = structOf(";", this.toTerm(), other.toTerm())

    /** Builds a `**`/2 [Struct] (arithmetic power). */
    @JsName("anyPow")
    infix fun Any.pow(other: Any): Struct = structOf("**", this.toTerm(), other.toTerm())

    /** Builds a `^`/2 [Struct] (existential quantification, e.g. for `bagof`/`setof`, or integer power). */
    @JsName("anySup")
    infix fun Any.sup(other: Any): Struct = structOf("^", this.toTerm(), other.toTerm())

    /** Builds an `is`/2 [Struct] (arithmetic evaluation). */
    @Suppress("ktlint:standard:function-naming")
    @JsName("anyIs")
    infix fun Any.`is`(other: Any): Struct = structOf("is", this.toTerm(), other.toTerm())

    /** Builds a `->`/2 [Struct] (if-then). */
    @JsName("anyThen")
    infix fun Any.then(other: Any): Struct = structOf("->", this.toTerm(), other.toTerm())

    /**
     * Builds the [Rule] `this :- other`, i.e. `other` implies (is the body of a rule whose head is) this value.
     *
     * @throws IllegalArgumentException if this value does not [toTerm] into a [Struct] (a legal rule head).
     */
    @JsName("anyImpliedBy")
    infix fun Any.impliedBy(other: Any): Rule =
        when (val t = this.toTerm()) {
            is Struct -> ruleOf(t, other.toTerm())
            else -> raiseErrorConvertingTo(Struct::class)
        }

    /**
     * Alias of [impliedBy] reading in head-first order: `head `if` body` builds the [Rule] `head :- body`.
     * @throws IllegalArgumentException if this value does not [toTerm] into a [Struct].
     */
    @Suppress("ktlint:standard:function-naming")
    @JsName("anyIf")
    infix fun Any.`if`(other: Any): Rule = this impliedBy other

    /**
     * Vararg overload of [impliedBy]: wraps [other] into a single conjunction (via [Tuple.wrapIfNeeded]) before
     * building the [Rule], so a multi-goal body can be listed as separate arguments instead of chained with [and].
     * @throws IllegalArgumentException if this value does not [toTerm] into a [Struct].
     */
    @JsName("anyImpliedByVararg")
    fun Any.impliedBy(vararg other: Any): Rule = this impliedBy Tuple.wrapIfNeeded(other.map { it.toTerm() })

    /**
     * Vararg overload of [if]: `head.if(body1, body2, ...)` builds the [Rule] `head :- (body1, body2, ...)`.
     * @throws IllegalArgumentException if this value does not [toTerm] into a [Struct].
     */
    @Suppress("ktlint:standard:function-naming")
    @JsName("anyIfVararg")
    fun Any.`if`(vararg other: Any): Rule = this.impliedBy(*other)
}
