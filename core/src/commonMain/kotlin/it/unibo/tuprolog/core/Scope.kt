package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.ScopeImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.core.Set as LogicSet

interface Scope {

    val variables: Map<String, Var>

    operator fun contains(variable: Var): Boolean

    operator fun contains(variable: String): Boolean

    operator fun get(variable: String): Var?

    fun where(lambda: Scope.() -> Unit): Scope

    fun varOf(name: String): Var

    fun atomOf(value: String): Atom

    fun structOf(functor: String, vararg args: Term): Struct

    fun structOf(functor: String, args: Sequence<Term>): Struct

    fun tupleOf(vararg terms: Term): Tuple

    fun tupleOf(terms: Iterable<Term>): Tuple

    fun listOf(vararg terms: Term): LogicList

    fun listOf(terms: Iterable<Term>): LogicList

    fun listFrom(terms: Iterable<Term>, last: Term? = null): LogicList

    fun setOf(vararg terms: Term): LogicSet

    fun setOf(terms: Iterable<Term>): LogicSet

    fun factOf(head: Struct): Fact

    fun ruleOf(head: Struct, body1: Term, vararg body: Term): Rule

    fun directiveOf(body1: Term, vararg body: Term): Directive

    fun clauseOf(head: Struct?, vararg body: Term): Clause

    fun coupleOf(head: Term, tail: Term): Couple

    fun anonymous(): Var

    fun whatever(): Var

    fun numOf(value: BigDecimal): Real

    fun numOf(value: Double): Real

    fun numOf(value: Float): Real

    fun numOf(value: BigInteger): Integral

    fun numOf(value: Int): Integral

    fun numOf(value: Long): Integral

    fun numOf(value: Short): Integral

    fun numOf(value: Byte): Integral

    fun numOf(value: String): Numeric

    companion object {

        fun empty(): Scope = ScopeImpl(mutableMapOf())

        fun of(vararg vars: Var): Scope {
            val variables: MutableMap<String, Var> = mutableMapOf()
            for (v in vars) {
                variables[v.name] = v
            }
            return ScopeImpl(variables)
        }

        fun of(vararg vars: Var, lambda: Scope.() -> Unit): Scope {
            val scope = of(*vars)
            scope.where(lambda)
            return scope
        }

        fun of(vararg vars: String): Scope {
            val variables: MutableMap<String, Var> = mutableMapOf()
            for (v in vars) {
                variables[v] = Var.of(v)
            }
            return ScopeImpl(variables)
        }

        fun of(vararg vars: String, lambda: Scope.() -> Unit): Scope {
            val scope = of(*vars)
            scope.where(lambda)
            return scope
        }
    }
}