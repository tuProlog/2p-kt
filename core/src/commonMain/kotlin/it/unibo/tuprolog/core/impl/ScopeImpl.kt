package it.unibo.tuprolog.scoping

import it.unibo.tuprolog.core.*
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.core.Set as LogicSet

internal class ScopeImpl(private val _variables: MutableMap<String, Var>) : Scope {
    override fun setOf(terms: Iterable<Term>): LogicSet {
        return LogicSet.of(terms)
    }

    override fun listOf(terms: Iterable<Term>): LogicList {
        return LogicList.of(terms)
    }

    override fun contains(variable: Var): Boolean {
        return variable.name in _variables
    }

    override fun contains(variable: String): Boolean {
        return variable in _variables
    }

    override fun get(variable: String): Var? {
        return _variables[variable]
    }

    override fun atomOf(value: String): Atom {
        return Atom.of(value)
    }

    override fun structOf(functor: String, vararg args: Term): Struct {
        return Struct.of(functor, *args)
    }

    override fun structOf(functor: String, args: Sequence<Term>): Struct {
        return Struct.of(functor, args)
    }

    override fun listOf(vararg terms: Term): LogicList {
        return LogicList.of(*terms)
    }

    override fun setOf(vararg terms: Term): LogicSet {
        return LogicSet.of(*terms)
    }

    override fun factOf(head: Struct): Fact {
        return Fact.of(head)
    }

    override fun ruleOf(head: Struct, body1: Term, vararg body: Term): Rule {
        return Rule.of(head, *(arrayOf(body1) + arrayOf(*body)))
    }

    override fun directiveOf(body1: Term, vararg body: Term): Directive {
        return Directive.of(body1, *body)
    }

    override fun clauseOf(head: Struct?, vararg body: Term): Clause {
        return Clause.of(head, *body)
    }

    override fun coupleOf(head: Term, tail: Term): Couple {
        return Couple.of(head, tail)
    }

    override fun anonymous(): Var {
        return Var.anonymous()
    }

    override fun whatever(): Var {
        return anonymous()
    }

    override fun numOf(value: BigDecimal): Real {
        return Numeric.of(value)
    }

    override fun numOf(value: Double): Real {
        return Numeric.of(value)
    }

    override fun numOf(value: Float): Real {
        return Numeric.of(value)
    }

    override fun numOf(value: BigInteger): Integral {
        return Numeric.of(value)
    }

    override fun numOf(value: Int): Integral {
        return Numeric.of(value)
    }

    override fun numOf(value: Long): Integral {
        return Numeric.of(value)
    }

    override fun numOf(value: Short): Integral {
        return Numeric.of(value)
    }

    override fun numOf(value: Byte): Integral {
        return Numeric.of(value)
    }

    override fun numOf(value: String): Numeric {
        return Numeric.of(value)
    }

    override val variables: Map<String, Var>
        get() = _variables.toMap()

    override fun varOf(name: String): Var {
        if (name !in _variables) {
            _variables[name] = Var.of(name)
        }
        return _variables[name]!!
    }

    override fun where(lambda: Scope.() -> Unit): Scope {
        this.lambda()
        return this
    }
}