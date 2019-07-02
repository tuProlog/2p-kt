package it.unibo.tuprolog.scoping

import it.unibo.tuprolog.core.*
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

interface Scope {

    val variables: Map<String, Var>

    operator fun contains(variable: Var): Boolean

    operator fun contains(variable: String): Boolean

    operator fun get(variable: String): Var?

    fun where (lambda: Scope.() -> Unit): Scope

    fun varOf(name: String): Var

    fun atomOf(value: String): Atom

    fun structOf(functor: String, vararg args: Term): Struct

    fun listOf(vararg terms: Term): List

    fun setOf(vararg terms: Term): Set

    fun factOf(head: Struct): Fact

    fun ruleOf(head: Struct, body1: Term, vararg body: Term): Rule

    fun directiveOf(body1: Term, vararg body: Term): Directive

    fun clauseOf(head: Struct?, vararg body: Term): Clause

    fun coupleOf(head: Term, tail: Term): Couple

    fun anonymous(): Term

    fun whatever(): Term

    fun numOf(decimal: BigDecimal): Real

    fun numOf(decimal: Double): Real

    fun numOf(decimal: Float): Real

    fun numOf(integer: BigInteger): Integral

    fun numOf(integer: Int): Integral

    fun numOf(integer: Long): Integral

    fun numOf(integer: Short): Integral

    fun numOf(integer: Byte): Integral

    fun numOf(number: String): Numeric

    companion object {

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