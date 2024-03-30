package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import kotlin.js.JsName
import kotlin.collections.plus as append

interface LogicProgrammingScopeWithOperators<S : LogicProgrammingScopeWithOperators<S>> : BaseLogicProgrammingScope<S> {
    @JsName("termPlus")
    operator fun Term.plus(other: Any): Struct = structOf("+", this.toTerm(), other.toTerm())

    @JsName("number")
    operator fun Number.plus(other: Any): Struct = toTerm() + other

    @JsName("booleanPlus")
    operator fun Boolean.plus(other: Any): Struct = toTerm() + other

    @JsName("charPlus")
    operator fun Char.plus(other: Any): Struct = toTerm() + other

    @JsName("termMinus")
    operator fun Term.minus(other: Any): Struct = structOf("-", this.toTerm(), other.toTerm())

    @JsName("numberMinus")
    operator fun Number.minus(other: Any): Struct = toTerm() - other

    @JsName("booleanMinus")
    operator fun Boolean.minus(other: Any): Struct = toTerm() - other

    @JsName("charMinus")
    operator fun Char.minus(other: Any): Struct = toTerm() - other

    @JsName("stringMinus")
    operator fun String.minus(other: Any): Struct = toTerm() - other

    @JsName("termTimes")
    operator fun Term.times(other: Any): Struct = structOf("*", this.toTerm(), other.toTerm())

    @JsName("numberTimes")
    operator fun Number.times(other: Any): Struct = toTerm() * other

    @JsName("booleanTimes")
    operator fun Boolean.times(other: Any): Struct = toTerm() * other

    @JsName("charTimes")
    operator fun Char.times(other: Any): Struct = toTerm() * other

    @JsName("stringTimes")
    operator fun String.times(other: Any): Struct = toTerm() * other

    @JsName("termDiv")
    operator fun Term.div(other: Any): Indicator = indicatorOf(this.toTerm(), other.toTerm())

    @JsName("numberDiv")
    operator fun Number.div(other: Any): Struct = toTerm() / other

    @JsName("booleanDiv")
    operator fun Boolean.div(other: Any): Struct = toTerm() / other

    @JsName("charDiv")
    operator fun Char.div(other: Any): Struct = toTerm() / other

    @JsName("stringDiv")
    operator fun String.div(other: Any): Struct = toTerm() / other

    /** Creates a structure whose functor is `'='/2` (term unification operator) */
    @JsName("anyEqualsTo")
    infix fun Any.equalsTo(other: Any): Struct = structOf("=", this.toTerm(), other.toTerm())

    @JsName("anyNotEqualsTo")
    infix fun Any.notEqualsTo(other: Any): Struct = structOf("\\=", this.toTerm(), other.toTerm())

    @JsName("anyGreaterThan")
    infix fun Any.greaterThan(other: Any): Struct = structOf(">", this.toTerm(), other.toTerm())

    @JsName("anyGreaterThanOrEqualsTo")
    infix fun Any.greaterThanOrEqualsTo(other: Any): Struct = structOf(">=", this.toTerm(), other.toTerm())

    @JsName("anyNonLowerThan")
    infix fun Any.nonLowerThan(other: Any): Struct = this greaterThanOrEqualsTo other

    @JsName("anyLowerThan")
    infix fun Any.lowerThan(other: Any): Struct = structOf("<", this.toTerm(), other.toTerm())

    @JsName("anyLowerThanOrEqualsTo")
    infix fun Any.lowerThanOrEqualsTo(other: Any): Struct = structOf("=<", this.toTerm(), other.toTerm())

    @JsName("anyNonGreaterThan")
    infix fun Any.nonGreaterThan(other: Any): Struct = this lowerThanOrEqualsTo other

    @JsName("anyIntDiv")
    infix fun Any.intDiv(other: Any): Struct = structOf("//", this.toTerm(), other.toTerm())

    @JsName("termRem")
    infix operator fun Term.rem(other: Any): Struct = structOf("rem", this.toTerm(), other.toTerm())

    @JsName("numberRem")
    infix operator fun Number.rem(other: Any): Struct = toTerm() % other

    @JsName("booleanRem")
    infix operator fun Boolean.rem(other: Any): Struct = toTerm() % other

    @JsName("charRem")
    infix operator fun Char.rem(other: Any): Struct = toTerm() % other

    @JsName("stringRem")
    infix operator fun String.rem(other: Any): Struct = toTerm() % other

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

    @JsName("anyOr")
    infix fun Any.or(other: Any): Struct = structOf(";", this.toTerm(), other.toTerm())

    @JsName("anyPow")
    infix fun Any.pow(other: Any): Struct = structOf("**", this.toTerm(), other.toTerm())

    @JsName("anySup")
    infix fun Any.sup(other: Any): Struct = structOf("^", this.toTerm(), other.toTerm())

    @Suppress("ktlint:standard:function-naming")
    @JsName("anyIs")
    infix fun Any.`is`(other: Any): Struct = structOf("is", this.toTerm(), other.toTerm())

    @JsName("anyThen")
    infix fun Any.then(other: Any): Struct = structOf("->", this.toTerm(), other.toTerm())

    @JsName("anyImpliedBy")
    infix fun Any.impliedBy(other: Any): Rule =
        when (val t = this.toTerm()) {
            is Struct -> ruleOf(t, other.toTerm())
            else -> raiseErrorConvertingTo(Struct::class)
        }

    @Suppress("ktlint:standard:function-naming")
    @JsName("anyIf")
    infix fun Any.`if`(other: Any): Rule = this impliedBy other

    @JsName("anyImpliedByVararg")
    fun Any.impliedBy(vararg other: Any): Rule = this impliedBy Tuple.wrapIfNeeded(other.map { it.toTerm() })

    @Suppress("ktlint:standard:function-naming")
    @JsName("anyIfVararg")
    fun Any.`if`(vararg other: Any): Rule = this.impliedBy(*other)
}
