package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.*
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

import it.unibo.tuprolog.core.toTerm as extToTerm

interface Prolog : Scope {

    fun Any.toTerm(): Term = when (this) {
        is Term -> this
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
        else -> throw IllegalArgumentException("Cannot convert ${this::class} into ${Term::class}")
    }

    infix fun Var.to(termObject: Any) = Substitution.of(this, termObject.toTerm())

    infix fun String.to(termObject: Any) = Substitution.of(varOf(this), termObject.toTerm())

    fun structOf(functor: String, vararg args: Any): Struct {
        return structOf(functor, *args.map { it.toTerm() }.toTypedArray())
    }

    operator fun String.invoke(term: Any, vararg terms: Any): Struct {
        return structOf(this, sequenceOf(term, *terms).map { it.toTerm() })
    }

    operator fun Any.plus(other: Any): Struct {
        return structOf("+", this.toTerm(), other.toTerm())
    }

    operator fun Any.minus(other: Any): Struct {
        return structOf("-", this.toTerm(), other.toTerm())
    }

    operator fun Any.times(other: Any): Struct {
        return structOf("*", this.toTerm(), other.toTerm())
    }

    operator fun Any.div(other: Any): Struct {
        return structOf("/", this.toTerm(), other.toTerm())
    }

    infix fun Any.greaterThan(other: Any): Struct {
        return structOf(">", this.toTerm(), other.toTerm())
    }

    infix fun Any.greaterThanOrEqualsTo(other: Any): Struct {
        return structOf(">=", this.toTerm(), other.toTerm())
    }

    infix fun Any.nonLowerThan(other: Any): Struct {
        return this greaterThanOrEqualsTo other
    }

    infix fun Any.lowerThan(other: Any): Struct {
        return structOf("<", this.toTerm(), other.toTerm())
    }

    infix fun Any.lowerThanOrEqualsTo(other: Any): Struct {
        return structOf("=<", this.toTerm(), other.toTerm())
    }

    infix fun Any.nonGreaterThan(other: Any): Struct {
        return this lowerThanOrEqualsTo other
    }

    infix fun Any.intDiv(other: Any): Struct {
        return structOf("//", this.toTerm(), other.toTerm())
    }

    operator fun Any.rem(other: Any): Struct {
        return structOf("rem", this.toTerm(), other.toTerm())
    }

    infix fun Any.and(other: Any): Struct {
        return tupleOf(this.toTerm(), other.toTerm())
    }

    infix fun Any.or(other: Any): Struct {
        return structOf(";", this.toTerm(), other.toTerm())
    }

    infix fun Any.pow(other: Any): Struct {
        return structOf("**", this.toTerm(), other.toTerm())
    }

    infix fun Any.sup(other: Any): Struct {
        return structOf("^", this.toTerm(), other.toTerm())
    }

    infix fun Any.impliedBy(other: Any): Rule {
        when (val t = this.toTerm()) {
            is Struct -> return ruleOf(t, other.toTerm())
            else -> throw IllegalArgumentException("Cannot convert ${this::class} into ${Struct::class}")
        }
    }

    infix fun Any.`is`(other: Any): Struct {
        return structOf("is", this.toTerm(), other.toTerm())
    }

    infix fun Any.`if`(other: Any): Rule {
        return this impliedBy other
    }

    fun Any.impliedBy(vararg other: Any): Rule =
            this impliedBy Tuple.wrapIfNeeded(*other.map { it.toTerm() }.toTypedArray())

    fun Any.`if`(vararg other: Any): Rule =
            this.impliedBy(*other)

    fun tupleOf(vararg terms: Any): Tuple {
        return tupleOf(*terms.map { it.toTerm() }.toTypedArray())
    }

    fun listOf(vararg terms: Any): it.unibo.tuprolog.core.List {
        return this.listOf(*terms.map { it.toTerm() }.toTypedArray())
    }

    fun setOf(vararg terms: Any): it.unibo.tuprolog.core.Set {
        return this.setOf(*terms.map { it.toTerm() }.toTypedArray())
    }

    fun consOf(head: Any, tail: Any): Cons {
        return consOf(head.toTerm(), tail.toTerm())
    }

    fun factOf(term: Any): Struct {
        return factOf(term.toTerm() as Struct)
    }

    fun directiveOf(term: Any, vararg terms: Any): Struct {
        return directiveOf(term.toTerm(), *terms.map { it.toTerm() }.toTypedArray())
    }

    fun <R> scope(function: Prolog.() -> R): R {
        return Prolog.empty().function()
    }

    fun rule(function: Prolog.() -> Any): Rule {
        return Prolog.empty().function().toTerm() as Rule
    }

    fun clause(function: Prolog.() -> Any): Clause {
        return Prolog.empty().function().let {
            when(val t = it.toTerm()) {
                is Clause -> t
                is Struct -> return factOf(t)
                else -> throw IllegalArgumentException("Cannot convert $it into a clause")
            }
        }
    }

    fun directive(function: Prolog.() -> Any): Directive {
        return Prolog.empty().function().let {
            when(val t = it.toTerm()) {
                is Directive -> t
                is Struct -> return directiveOf(t)
                else -> throw IllegalArgumentException("Cannot convert $it into a directive")
            }
        }
    }

    fun fact(function: Prolog.() -> Any): Fact {
        return Prolog.empty().function().let {
            when(val t = it.toTerm()) {
                is Fact -> t
                is Struct -> return factOf(t)
                else -> throw IllegalArgumentException("Cannot convert $it into a fact")
            }
        }
    }

    companion object {
        fun empty(): Prolog = PrologImpl()
    }
}


fun <R> prolog(function: Prolog.() -> R): R {
    return Prolog.empty().function()
}