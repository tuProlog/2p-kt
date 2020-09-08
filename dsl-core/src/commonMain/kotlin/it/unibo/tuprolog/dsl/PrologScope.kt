package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.*
import kotlin.js.JsName
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.core.Set as LogicSet

interface PrologScope : PrologStdLibScope {

    @JsName("stringInvoke")
    operator fun String.invoke(term: Any, vararg terms: Any): Struct =
        structOf(this, sequenceOf(term, *terms).map { it.toTerm() })

    @JsName("structOfAny")
    fun structOf(functor: String, vararg args: Any): Struct =
        structOf(functor, *args.map { it.toTerm() }.toTypedArray())

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
    infix fun Any.greaterThanOrEqualsTo(other: Any): Struct =
        structOf(">=", this.toTerm(), other.toTerm())

    @JsName("anyNonLowerThan")
    infix fun Any.nonLowerThan(other: Any): Struct = this greaterThanOrEqualsTo other

    @JsName("anyLowerThan")
    infix fun Any.lowerThan(other: Any): Struct = structOf("<", this.toTerm(), other.toTerm())

    @JsName("anyLowerThanOrEqualsTo")
    infix fun Any.lowerThanOrEqualsTo(other: Any): Struct =
        structOf("=<", this.toTerm(), other.toTerm())

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

    @JsName("anyIs")
    infix fun Any.`is`(other: Any): Struct = structOf("is", this.toTerm(), other.toTerm())

    @JsName("anyThen")
    infix fun Any.then(other: Any): Struct = structOf("->", this.toTerm(), other.toTerm())

    @JsName("anyImpliedBy")
    infix fun Any.impliedBy(other: Any): Rule {
        when (val t = this.toTerm()) {
            is Struct -> return ruleOf(t, other.toTerm())
            else -> raiseErrorConvertingTo(Struct::class)
        }
    }

    @JsName("anyIf")
    infix fun Any.`if`(other: Any): Rule = this impliedBy other

    @JsName("anyImpliedByVararg")
    fun Any.impliedBy(vararg other: Any): Rule =
        this impliedBy Tuple.wrapIfNeeded(*other.map { it.toTerm() }.toTypedArray())

    @JsName("anyIfVararg")
    fun Any.`if`(vararg other: Any): Rule =
        this.impliedBy(*other)

    @JsName("tupleOfAny")
    fun tupleOf(vararg terms: Any): Tuple = tupleOf(*terms.map { it.toTerm() }.toTypedArray())

    @JsName("listOfAny")
    fun listOf(vararg terms: Any): LogicList =
        this.listOf(*terms.map { it.toTerm() }.toTypedArray())

    @JsName("setOfAny")
    fun setOf(vararg terms: Any): LogicSet =
        this.setOf(*terms.map { it.toTerm() }.toTypedArray())

    @JsName("factOfAny")
    fun factOf(term: Any): Fact = factOf(term.toTerm() as Struct)

    @JsName("consOfAny")
    fun consOf(head: Any, tail: Any): Cons = consOf(head.toTerm(), tail.toTerm())

    @JsName("directiveOfAny")
    fun directiveOf(term: Any, vararg terms: Any): Directive =
        directiveOf(term.toTerm(), *terms.map { it.toTerm() }.toTypedArray())

    @JsName("scope")
    fun <R> scope(function: PrologScope.() -> R): R = PrologScope.empty().function()

    @JsName("list")
    fun list(vararg items: Any, tail: Any? = null): LogicList = kotlin.collections.listOf(*items).map { it.toTerm() }.let {
        if (tail != null) {
            listFrom(it, last = tail.toTerm())
        } else {
            listOf(it)
        }
    }

    @JsName("rule")
    fun rule(function: PrologScope.() -> Any): Rule = PrologScope.empty().function().toTerm() as Rule

    @JsName("clause")
    fun clause(function: PrologScope.() -> Any): Clause = PrologScope.empty().function().let {
        when (val t = it.toTerm()) {
            is Clause -> t
            is Struct -> return factOf(t)
            else -> it.raiseErrorConvertingTo(Clause::class)
        }
    }

    @JsName("directive")
    fun directive(function: PrologScope.() -> Any): Directive = PrologScope.empty().function().let {
        when (val t = it.toTerm()) {
            is Directive -> t
            is Struct -> return directiveOf(t)
            else -> it.raiseErrorConvertingTo(Directive::class)
        }
    }

    @JsName("fact")
    fun fact(function: PrologScope.() -> Any): Fact = PrologScope.empty().function().let {
        when (val t = it.toTerm()) {
            is Fact -> t
            is Struct -> return factOf(t)
            else -> it.raiseErrorConvertingTo(Fact::class)
        }
    }

    @JsName("varTo")
    infix fun Var.to(termObject: Any) = Substitution.of(this, termObject.toTerm())

    @JsName("stringTo")
    infix fun String.to(termObject: Any) = Substitution.of(varOf(this), termObject.toTerm())

    @JsName("substitutionGet")
    operator fun Substitution.get(term: Any): Term? =
        when (val t = term.toTerm()) {
            is Var -> this[t]
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    @JsName("substitutionContainsKey")
    fun Substitution.containsKey(term: Any): Boolean =
        when (val t = term.toTerm()) {
            is Var -> this.containsKey(t)
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    @JsName("substitutionContains")
    operator fun Substitution.contains(term: Any): Boolean = containsKey(term)

    @JsName("substitutionContainsValue")
    fun Substitution.containsValue(term: Any): Boolean =
        this.containsValue(term.toTerm())

    companion object {
        @JsName("empty")
        fun empty(): PrologScope = PrologScopeImpl()
    }
}