package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.core.Set as LogicSet

internal class ScopeImpl(private val _variables: MutableMap<String, Var>) : Scope {

    ////////////////////
    // Scope specific //
    ////////////////////

    override fun contains(variable: Var): Boolean = variable.name in _variables

    override fun contains(variable: String): Boolean = variable in _variables

    override fun get(variable: String): Var? = _variables[variable]

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

    ///////////////////////
    // General Factories //
    ///////////////////////

    override fun setOf(terms: Iterable<Term>): LogicSet = LogicSet.of(terms)

    override fun listOf(terms: Iterable<Term>): LogicList = LogicList.of(terms)

    override fun listFrom(terms: Iterable<Term>, last: Term?): LogicList =
            LogicList.from(terms, last)

    override fun listOf(vararg terms: Term): LogicList = LogicList.of(*terms)

    override fun tupleOf(terms: Iterable<Term>): Tuple = Tuple.of(terms.toList())

    override fun tupleOf(vararg terms: Term): Tuple = Tuple.of(terms.toList())

    override fun atomOf(value: String): Atom = Atom.of(value)

    override fun structOf(functor: String, vararg args: Term): Struct = Struct.of(functor, *args)

    override fun structOf(functor: String, args: Sequence<Term>): Struct = Struct.of(functor, args)

    override fun setOf(vararg terms: Term): LogicSet = LogicSet.of(*terms)

    override fun factOf(head: Struct): Fact = Fact.of(head)

    override fun ruleOf(head: Struct, body1: Term, vararg body: Term): Rule =
            Rule.of(head, body1, *body)

    override fun directiveOf(body1: Term, vararg body: Term): Directive = Directive.of(body1, *body)

    override fun clauseOf(head: Struct?, vararg body: Term): Clause = Clause.of(head, *body)

    override fun consOf(head: Term, tail: Term): Cons = Cons.of(head, tail)

    override fun anonymous(): Var = Var.anonymous()

    override fun whatever(): Var = anonymous()

    override fun numOf(value: BigDecimal): Real = Numeric.of(value)

    override fun numOf(value: Double): Real = Numeric.of(value)

    override fun numOf(value: Float): Real = Numeric.of(value)

    override fun numOf(value: BigInteger): Integer = Numeric.of(value)

    override fun numOf(value: Int): Integer = Numeric.of(value)

    override fun numOf(value: Long): Integer = Numeric.of(value)

    override fun numOf(value: Short): Integer = Numeric.of(value)

    override fun numOf(value: Byte): Integer = Numeric.of(value)

    override fun numOf(value: String): Numeric = Numeric.of(value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ScopeImpl

        if (_variables != other._variables) return false

        return true
    }

    override fun hashCode(): Int {
        return _variables.hashCode()
    }
}