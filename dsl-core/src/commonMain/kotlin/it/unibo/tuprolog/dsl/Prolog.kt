package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.*
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.reflect.KClass

import it.unibo.tuprolog.core.toTerm as extToTerm

interface Prolog : Scope {

    @JsName("toTerm")
    fun Any.toTerm(): Term

    @JsName("PrologStructOf")
    fun structOf(functor: String, vararg args: Any): Struct =
        structOf(functor, *args.map { it.toTerm() }.toTypedArray())

    @JsName("invoke")
    operator fun String.invoke(term: Any, vararg terms: Any): Struct =
        structOf(this, sequenceOf(term, *terms).map { it.toTerm() })

    @JsName("plus")
    operator fun Any.plus(other: Any): Struct = structOf("+", this.toTerm(), other.toTerm())

    @JsName("minus")
    operator fun Any.minus(other: Any): Struct = structOf("-", this.toTerm(), other.toTerm())

    @JsName("times")
    operator fun Any.times(other: Any): Struct = structOf("*", this.toTerm(), other.toTerm())

    @JsName("div")
    operator fun Any.div(other: Any): Struct = structOf("/", this.toTerm(), other.toTerm())

    /** Creates a structure whose functor is `'='/2` (term unification operator) */
    @JsName("equalsTo")
    infix fun Any.equalsTo(other: Any): Struct = structOf("=", this.toTerm(), other.toTerm())

    @JsName("greaterThan")
    infix fun Any.greaterThan(other: Any): Struct = structOf(">", this.toTerm(), other.toTerm())

    @JsName("greaterThanOrEqualsTo")
    infix fun Any.greaterThanOrEqualsTo(other: Any): Struct =
        structOf(">=", this.toTerm(), other.toTerm())

    @JsName("nonLowerThan")
    infix fun Any.nonLowerThan(other: Any): Struct = this greaterThanOrEqualsTo other

    @JsName("lowerThan")
    infix fun Any.lowerThan(other: Any): Struct = structOf("<", this.toTerm(), other.toTerm())

    @JsName("lowerThanOrEqualsTo")
    infix fun Any.lowerThanOrEqualsTo(other: Any): Struct =
        structOf("=<", this.toTerm(), other.toTerm())

    @JsName("nonGreaterThan")
    infix fun Any.nonGreaterThan(other: Any): Struct = this lowerThanOrEqualsTo other

    @JsName("intDiv")
    infix fun Any.intDiv(other: Any): Struct = structOf("//", this.toTerm(), other.toTerm())

    @JsName("rem")
    operator fun Any.rem(other: Any): Struct = structOf("rem", this.toTerm(), other.toTerm())

    @JsName("and")
    infix fun Any.and(other: Any): Struct = tupleOf(this.toTerm(), other.toTerm())

    @JsName("or")
    infix fun Any.or(other: Any): Struct = structOf(";", this.toTerm(), other.toTerm())

    @JsName("pow")
    infix fun Any.pow(other: Any): Struct = structOf("**", this.toTerm(), other.toTerm())

    @JsName("sup")
    infix fun Any.sup(other: Any): Struct = structOf("^", this.toTerm(), other.toTerm())

    @JsName("is")
    infix fun Any.`is`(other: Any): Struct = structOf("is", this.toTerm(), other.toTerm())

    @JsName("then")
    infix fun Any.then(other: Any): Struct = structOf("->", this.toTerm(), other.toTerm())

    @JsName("impliedBy")
    infix fun Any.impliedBy(other: Any): Rule {
        when (val t = this.toTerm()) {
            is Struct -> return ruleOf(t, other.toTerm())
            else -> raiseErrorConvertingTo(Struct::class)
        }
    }

    @JsName("if")
    infix fun Any.`if`(other: Any): Rule = this impliedBy other

    @JsName("impliedByVararg")
    fun Any.impliedBy(vararg other: Any): Rule =
        this impliedBy Tuple.wrapIfNeeded(*other.map { it.toTerm() }.toTypedArray())

    @JsName("ifVararg")
    fun Any.`if`(vararg other: Any): Rule =
        this.impliedBy(*other)

    @JsName("PrologtupleOf")
    fun tupleOf(vararg terms: Any): Tuple = tupleOf(*terms.map { it.toTerm() }.toTypedArray())

    @JsName("ProloglistOf")
    fun listOf(vararg terms: Any): it.unibo.tuprolog.core.List =
        this.listOf(*terms.map { it.toTerm() }.toTypedArray())

    @JsName("PrologsetOf")
    fun setOf(vararg terms: Any): it.unibo.tuprolog.core.Set =
        this.setOf(*terms.map { it.toTerm() }.toTypedArray())

    @JsName("PrologconsOf")
    fun consOf(head: Any, tail: Any): Cons = consOf(head.toTerm(), tail.toTerm())

    @JsName("PrologfactOf")
    fun factOf(term: Any): Fact = factOf(term.toTerm() as Struct)

    @JsName("PrologdirectiveOf")
    fun directiveOf(term: Any, vararg terms: Any): Directive =
        directiveOf(term.toTerm(), *terms.map { it.toTerm() }.toTypedArray())

    @JsName("scope")
    fun <R> scope(function: Prolog.() -> R): R = Prolog.empty().function()

    @JsName("rule")
    fun rule(function: Prolog.() -> Any): Rule = Prolog.empty().function().toTerm() as Rule

    @JsName("clause")
    fun clause(function: Prolog.() -> Any): Clause = Prolog.empty().function().let {
        when (val t = it.toTerm()) {
            is Clause -> t
            is Struct -> return factOf(t)
            else -> it.raiseErrorConvertingTo(Clause::class)
        }
    }

    @JsName("directive")
    fun directive(function: Prolog.() -> Any): Directive = Prolog.empty().function().let {
        when (val t = it.toTerm()) {
            is Directive -> t
            is Struct -> return directiveOf(t)
            else -> it.raiseErrorConvertingTo(Directive::class)
        }
    }

    @JsName("fact")
    fun fact(function: Prolog.() -> Any): Fact = Prolog.empty().function().let {
        when (val t = it.toTerm()) {
            is Fact -> t
            is Struct -> return factOf(t)
            else -> it.raiseErrorConvertingTo(Fact::class)
        }
    }

    @JsName("to")
    infix fun Var.to(termObject: Any) = Substitution.of(this, termObject.toTerm())

    @JsName("StringTo")
    infix fun String.to(termObject: Any) = Substitution.of(varOf(this), termObject.toTerm())

    @JsName("SubstitutionGet")
    operator fun Substitution.get(term: Any): Term? =
        when (val t = term.toTerm()) {
            is Var -> this[t]
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    @JsName("containsKey")
    fun Substitution.containsKey(term: Any): Boolean =
        when (val t = term.toTerm()) {
            is Var -> this.containsKey(t)
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    @JsName("SubstitutionContains")
    operator fun Substitution.contains(term: Any): Boolean = containsKey(term)

    @JsName("containsValue")
    fun Substitution.containsValue(term: Any): Boolean =
        this.containsValue(term.toTerm())

    companion object {
        fun empty(): Prolog = PrologImpl()
    }
}

@JsName("prolog")
fun <R> prolog(function: Prolog.() -> R): R = Prolog.empty().function()

/** Utility method to launch conversion failed errors */
internal fun Any.raiseErrorConvertingTo(`class`: KClass<*>): Nothing =
    throw IllegalArgumentException("Cannot convert ${this::class} into $`class`")