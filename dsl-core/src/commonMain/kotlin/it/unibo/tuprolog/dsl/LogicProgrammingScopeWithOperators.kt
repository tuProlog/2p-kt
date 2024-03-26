package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Tuple
import kotlin.js.JsName

interface LogicProgrammingScopeWithOperators<S : LogicProgrammingScopeWithOperators<S>> : BaseLogicProgrammingScope<S> {
    @JsName("anyPlus")
    operator fun Any.plus(other: Any): Struct = structOf("+", this.toTerm(), other.toTerm())

    @JsName("anyMinus")
    operator fun Any.minus(other: Any): Struct = structOf("-", this.toTerm(), other.toTerm())

    @JsName("anyTimes")
    operator fun Any.times(other: Any): Struct = structOf("*", this.toTerm(), other.toTerm())

    @JsName("anyDiv")
    operator fun Any.div(other: Any): Indicator = indicatorOf(this.toTerm(), other.toTerm())

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

    @JsName("anyRem")
    operator fun Any.rem(other: Any): Struct = structOf("rem", this.toTerm(), other.toTerm())

    @JsName("anyAnd")
    infix fun Any.and(other: Any): Struct = tupleOf(this.toTerm(), other.toTerm())

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
