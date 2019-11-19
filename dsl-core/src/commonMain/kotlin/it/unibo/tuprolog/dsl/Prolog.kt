package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.*
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.reflect.KClass

import it.unibo.tuprolog.core.toTerm as extToTerm

interface Prolog : Scope {

    fun Any.toTerm(): Term = when (this) {
        is Term -> this
        is ToTermConvertible -> this.toTerm()
        is BigDecimal -> numOf(this)
        is Double -> numOf(this)
        is Float -> numOf(this)
        is BigInteger -> numOf(this)
        is Long -> numOf(this)
        is Int -> numOf(this)
        is Short -> numOf(this)
        is Byte -> numOf(this)
        is Boolean -> truthOf(this)
        is String -> when {
            this matches Var.VAR_REGEX_PATTERN -> varOf(this)
            else -> atomOf(this)
        }
        is Array<*> -> this.map { it!!.toTerm() }.extToTerm()
        is Sequence<*> -> this.map { it!!.toTerm() }.extToTerm()
        is Iterable<*> -> this.map { it!!.toTerm() }.extToTerm()
        else -> raiseErrorConvertingTo(Term::class)
    }

    fun structOf(functor: String, vararg args: Any): Struct =
        structOf(functor, *args.map { it.toTerm() }.toTypedArray())

    operator fun String.invoke(term: Any, vararg terms: Any): Struct =
        structOf(this, sequenceOf(term, *terms).map { it.toTerm() })

    operator fun Any.plus(other: Any): Struct = structOf("+", this.toTerm(), other.toTerm())

    operator fun Any.minus(other: Any): Struct = structOf("-", this.toTerm(), other.toTerm())

    operator fun Any.times(other: Any): Struct = structOf("*", this.toTerm(), other.toTerm())

    operator fun Any.div(other: Any): Struct = structOf("/", this.toTerm(), other.toTerm())

    infix fun Any.greaterThan(other: Any): Struct = structOf(">", this.toTerm(), other.toTerm())

    infix fun Any.greaterThanOrEqualsTo(other: Any): Struct =
        structOf(">=", this.toTerm(), other.toTerm())

    infix fun Any.nonLowerThan(other: Any): Struct = this greaterThanOrEqualsTo other

    infix fun Any.lowerThan(other: Any): Struct = structOf("<", this.toTerm(), other.toTerm())

    infix fun Any.lowerThanOrEqualsTo(other: Any): Struct =
        structOf("=<", this.toTerm(), other.toTerm())

    infix fun Any.nonGreaterThan(other: Any): Struct = this lowerThanOrEqualsTo other

    infix fun Any.intDiv(other: Any): Struct = structOf("//", this.toTerm(), other.toTerm())

    operator fun Any.rem(other: Any): Struct = structOf("rem", this.toTerm(), other.toTerm())

    infix fun Any.and(other: Any): Struct = tupleOf(this.toTerm(), other.toTerm())

    infix fun Any.or(other: Any): Struct = structOf(";", this.toTerm(), other.toTerm())

    infix fun Any.pow(other: Any): Struct = structOf("**", this.toTerm(), other.toTerm())

    infix fun Any.sup(other: Any): Struct = structOf("^", this.toTerm(), other.toTerm())

    infix fun Any.`is`(other: Any): Struct = structOf("is", this.toTerm(), other.toTerm())

    infix fun Any.impliedBy(other: Any): Rule {
        when (val t = this.toTerm()) {
            is Struct -> return ruleOf(t, other.toTerm())
            else -> raiseErrorConvertingTo(Struct::class)
        }
    }

    infix fun Any.`if`(other: Any): Rule = this impliedBy other

    fun Any.impliedBy(vararg other: Any): Rule =
        this impliedBy Tuple.wrapIfNeeded(*other.map { it.toTerm() }.toTypedArray())

    fun Any.`if`(vararg other: Any): Rule =
        this.impliedBy(*other)

    fun tupleOf(vararg terms: Any): Tuple = tupleOf(*terms.map { it.toTerm() }.toTypedArray())

    fun listOf(vararg terms: Any): it.unibo.tuprolog.core.List =
        this.listOf(*terms.map { it.toTerm() }.toTypedArray())

    fun setOf(vararg terms: Any): it.unibo.tuprolog.core.Set =
        this.setOf(*terms.map { it.toTerm() }.toTypedArray())

    fun consOf(head: Any, tail: Any): Cons = consOf(head.toTerm(), tail.toTerm())

    fun factOf(term: Any): Fact = factOf(term.toTerm() as Struct)

    fun directiveOf(term: Any, vararg terms: Any): Directive =
        directiveOf(term.toTerm(), *terms.map { it.toTerm() }.toTypedArray())

    fun <R> scope(function: Prolog.() -> R): R = Prolog.empty().function()

    fun rule(function: Prolog.() -> Any): Rule = Prolog.empty().function().toTerm() as Rule

    fun clause(function: Prolog.() -> Any): Clause = Prolog.empty().function().let {
        when (val t = it.toTerm()) {
            is Clause -> t
            is Struct -> return factOf(t)
            else -> it.raiseErrorConvertingTo(Clause::class)
        }
    }

    fun directive(function: Prolog.() -> Any): Directive = Prolog.empty().function().let {
        when (val t = it.toTerm()) {
            is Directive -> t
            is Struct -> return directiveOf(t)
            else -> it.raiseErrorConvertingTo(Directive::class)
        }
    }

    fun fact(function: Prolog.() -> Any): Fact = Prolog.empty().function().let {
        when (val t = it.toTerm()) {
            is Fact -> t
            is Struct -> return factOf(t)
            else -> it.raiseErrorConvertingTo(Fact::class)
        }
    }

    infix fun Var.to(termObject: Any) = Substitution.of(this, termObject.toTerm())

    infix fun String.to(termObject: Any) = Substitution.of(varOf(this), termObject.toTerm())

    operator fun Substitution.get(term: Any): Term? =
        when (val t = term.toTerm()) {
            is Var -> this[t]
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    fun Substitution.containsKey(term: Any): Boolean =
        when (val t = term.toTerm()) {
            is Var -> this.containsKey(t)
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    operator fun Substitution.contains(term: Any): Boolean = containsKey(term)

    fun Substitution.containsValue(term: Any): Boolean =
        this.containsValue(term.toTerm())

    companion object {
        fun empty(): Prolog = PrologImpl()

        /** Utility method to launch conversion failed errors */
        private fun Any.raiseErrorConvertingTo(`class`: KClass<*>): Nothing =
            throw IllegalArgumentException("Cannot convert ${this::class} into $`class`")
    }
}

fun <R> prolog(function: Prolog.() -> R): R = Prolog.empty().function()
