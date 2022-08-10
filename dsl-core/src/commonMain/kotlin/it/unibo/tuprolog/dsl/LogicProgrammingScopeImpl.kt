package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.VariablesProvider

internal class LogicProgrammingScopeImpl(scope: Scope) :
    LogicProgrammingScope,
    VariablesProvider by VariablesProvider.of(scope) {

    private val anyToTermConverter = AnyToTermConverter.of(this)

    override fun Any.toTerm(): Term =
        anyToTermConverter.toTerm(this)

    override fun String.invoke(term: Any, vararg terms: Any): Struct =
        structOf(this, sequenceOf(term, *terms).map { it.toTerm() })

    override fun structOf(functor: String, vararg args: Any): Struct =
        structOf(functor, *args.map { it.toTerm() }.toTypedArray())

    override fun Any.plus(other: Any): Struct = structOf("+", this.toTerm(), other.toTerm())

    override fun Any.minus(other: Any): Struct = structOf("-", this.toTerm(), other.toTerm())

    override fun Any.times(other: Any): Struct = structOf("*", this.toTerm(), other.toTerm())

    override fun Any.div(other: Any): Indicator = indicatorOf(this.toTerm(), other.toTerm())

    /** Creates a structure whose functor is `'='/2` (term unification operator) */
    override fun Any.equalsTo(other: Any): Struct = structOf("=", this.toTerm(), other.toTerm())

    override fun Any.notEqualsTo(other: Any): Struct = structOf("\\=", this.toTerm(), other.toTerm())

    override fun Any.greaterThan(other: Any): Struct = structOf(">", this.toTerm(), other.toTerm())

    override fun Any.greaterThanOrEqualsTo(other: Any): Struct =
        structOf(">=", this.toTerm(), other.toTerm())

    override fun Any.nonLowerThan(other: Any): Struct = this greaterThanOrEqualsTo other

    override fun Any.lowerThan(other: Any): Struct = structOf("<", this.toTerm(), other.toTerm())

    override fun Any.lowerThanOrEqualsTo(other: Any): Struct =
        structOf("=<", this.toTerm(), other.toTerm())

    override fun Any.nonGreaterThan(other: Any): Struct = this lowerThanOrEqualsTo other

    override fun Any.intDiv(other: Any): Struct = structOf("//", this.toTerm(), other.toTerm())

    override fun Any.rem(other: Any): Struct = structOf("rem", this.toTerm(), other.toTerm())

    override fun Any.and(other: Any): Struct = tupleOf(this.toTerm(), other.toTerm())

    override fun Any.or(other: Any): Struct = structOf(";", this.toTerm(), other.toTerm())

    override fun Any.pow(other: Any): Struct = structOf("**", this.toTerm(), other.toTerm())

    override fun Any.sup(other: Any): Struct = structOf("^", this.toTerm(), other.toTerm())

    override fun Any.`is`(other: Any): Struct = structOf("is", this.toTerm(), other.toTerm())

    override fun Any.then(other: Any): Struct = structOf("->", this.toTerm(), other.toTerm())

    override fun Any.impliedBy(other: Any): Rule {
        when (val t = this.toTerm()) {
            is Struct -> return ruleOf(t, other.toTerm())
            else -> raiseErrorConvertingTo(Struct::class)
        }
    }

    override fun Any.`if`(other: Any): Rule = this impliedBy other

    override fun Any.impliedBy(vararg other: Any): Rule =
        this impliedBy Tuple.wrapIfNeeded(*other.map { it.toTerm() }.toTypedArray())

    override fun Any.`if`(vararg other: Any): Rule =
        this.impliedBy(*other)

    override fun tupleOf(vararg terms: Any): Tuple = tupleOf(*terms.map { it.toTerm() }.toTypedArray())

    override fun listOf(vararg terms: Any): List =
        this.listOf(*terms.map { it.toTerm() }.toTypedArray())

    override fun blockOf(vararg terms: Any): Block =
        this.blockOf(*terms.map { it.toTerm() }.toTypedArray())

    override fun factOf(term: Any): Fact = factOf(term.toTerm() as Struct)

    override fun consOf(head: Any, tail: Any): Cons = consOf(head.toTerm(), tail.toTerm())

    override fun directiveOf(term: Any, vararg terms: Any): Directive =
        directiveOf(term.toTerm(), *terms.map { it.toTerm() }.toTypedArray())

    override fun <R> scope(function: LogicProgrammingScope.() -> R): R = LogicProgrammingScope.empty().function()

    override fun list(vararg items: Any, tail: Any?): List = ktListOf(*items).map { it.toTerm() }.let {
        if (tail != null) {
            listFrom(it, last = tail.toTerm())
        } else {
            listOf(it)
        }
    }

    override fun rule(function: LogicProgrammingScope.() -> Any): Rule = LogicProgrammingScope.empty().function().toTerm() as Rule

    override fun clause(function: LogicProgrammingScope.() -> Any): Clause = LogicProgrammingScope.empty().function().let {
        when (val t = it.toTerm()) {
            is Clause -> t
            is Struct -> return factOf(t)
            else -> it.raiseErrorConvertingTo(Clause::class)
        }
    }

    override fun directive(function: LogicProgrammingScope.() -> Any): Directive = LogicProgrammingScope.empty().function().let {
        when (val t = it.toTerm()) {
            is Directive -> t
            is Struct -> return directiveOf(t)
            else -> it.raiseErrorConvertingTo(Directive::class)
        }
    }

    override fun fact(function: LogicProgrammingScope.() -> Any): Fact = LogicProgrammingScope.empty().function().let {
        when (val t = it.toTerm()) {
            is Fact -> t
            is Struct -> return factOf(t)
            else -> it.raiseErrorConvertingTo(Fact::class)
        }
    }

    override fun Var.to(termObject: Any) = Substitution.of(this, termObject.toTerm())

    override fun String.to(termObject: Any) = Substitution.of(varOf(this), termObject.toTerm())

    override fun Substitution.get(term: Any): Term? =
        when (val t = term.toTerm()) {
            is Var -> this[t]
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    override fun Substitution.containsKey(term: Any): Boolean =
        when (val t = term.toTerm()) {
            is Var -> this.containsKey(t)
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    override fun Substitution.contains(term: Any): Boolean = containsKey(term)

    override fun Substitution.containsValue(term: Any): Boolean =
        this.containsValue(term.toTerm())
}
