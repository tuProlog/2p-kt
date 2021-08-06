package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.jvm.Synchronized
import it.unibo.tuprolog.core.List as LogicList

@Suppress("RemoveRedundantQualifierName")
internal class ScopeImpl(private val _variables: MutableMap<String, Var>) : Scope {

    override fun contains(variable: Var): Boolean = _variables.containsKey(variable.name)

    override fun contains(variable: String): Boolean = _variables.containsKey(variable)

    override fun get(variable: String): Var? = _variables[variable]

    override val variables: Map<String, Var>
        get() = _variables.toMap()

    @Synchronized
    override fun varOf(name: String): Var {
        if (!_variables.containsKey(name)) {
            _variables[name] = Var.of(name)
        }
        return _variables[name]!!
    }

    override fun where(lambda: Scope.() -> Unit): Scope =
        this.also(lambda)

    override fun <R> with(lambda: Scope.() -> R): R =
        with(this, lambda)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ScopeImpl

        if (_variables != other._variables) return false

        return true
    }

    override fun hashCode(): Int =
        _variables.hashCode()

    override fun toString(): String =
        variables.toString()

    override fun truthOf(value: Boolean): Truth =
        Truth.of(value)

    override val fail: Truth
        get() = Truth.FAIL

    override fun blockOf(terms: Iterable<Term>): Block =
        Block.of(terms)

    override fun blockOf(terms: Sequence<Term>): Block =
        Block.of(terms)

    override fun <T> ktSetOf(vararg items: T): Set<T> = kotlin.collections.setOf(*items)

    override fun <T> ktEmptySet(): Set<T> = kotlin.collections.emptySet()

    override fun blockOf(vararg terms: Term): Block =
        Block.of(*terms)

    override fun listOf(terms: Iterable<Term>): LogicList =
        LogicList.of(terms)

    override fun listOf(terms: Sequence<Term>): LogicList =
        LogicList.of(terms)

    override fun <T> ktListOf(vararg items: T): List<T> = kotlin.collections.listOf(*items)

    override fun <T> ktEmptyList(): List<T> = kotlin.collections.emptyList()

    override fun <T> List<T>.append(item: T, vararg items: T): List<T> =
        concat(ktListOf(item, *items))

    override fun <T> List<T>.concat(other: Iterable<T>): List<T> = plus(other)

    override val emptyList: EmptyList
        get() = EmptyList()

    override val emptyBlock: EmptyBlock
        get() = EmptyBlock()

    override fun listOf(vararg terms: Term): LogicList =
        LogicList.of(*terms)

    override fun listFrom(vararg terms: Term, last: Term?): LogicList =
        LogicList.from(*terms, last = last)

    override fun listFrom(terms: Sequence<Term>, last: Term?): LogicList =
        LogicList.from(terms, last)

    override fun listFrom(terms: Iterable<Term>, last: Term?): LogicList =
        LogicList.from(terms, last)

    override fun tupleOf(terms: Iterable<Term>): Tuple =
        Tuple.of(terms.toList())

    override fun tupleOf(terms: Sequence<Term>): Tuple =
        Tuple.of(terms.toList())

    override fun tupleOf(vararg terms: Term): Tuple =
        Tuple.of(terms.toList())

    override fun atomOf(value: String): Atom =
        Atom.of(value)

    override fun structOf(functor: String, vararg args: Term): Struct =
        Struct.of(functor, *args)

    override fun structOf(functor: String, args: Sequence<Term>): Struct =
        Struct.of(functor, args)

    override fun structOf(functor: String, args: Iterable<Term>): Struct =
        Struct.of(functor, args)

    override fun structOf(functor: String, args: List<Term>): Struct =
        Struct.of(functor, args)

    override fun factOf(head: Struct): Fact =
        Fact.of(head)

    override fun ruleOf(head: Struct, body1: Term, vararg body: Term): Rule =
        Rule.of(head, body1, *body)

    override fun directiveOf(body1: Term, vararg body: Term): Directive =
        Directive.of(body1, *body)

    override fun clauseOf(head: Struct?, vararg body: Term): Clause =
        Clause.of(head, *body)

    override fun consOf(head: Term, tail: Term): Cons =
        Cons.of(head, tail)

    override fun indicatorOf(name: Term, arity: Term): Indicator =
        Indicator.of(name, arity)

    override fun indicatorOf(name: String, arity: Int): Indicator =
        Indicator.of(name, arity)

    override fun anonymous(): Var =
        Var.anonymous()

    override fun whatever(): Var =
        anonymous()

    override fun numOf(value: BigDecimal): Real =
        Numeric.of(value)

    override fun numOf(value: Double): Real =
        Numeric.of(value)

    override fun numOf(value: Float): Real =
        Numeric.of(value)

    override fun numOf(value: BigInteger): Integer =
        Numeric.of(value)

    override fun numOf(value: Int): Integer =
        Numeric.of(value)

    override fun numOf(value: Long): Integer =
        Numeric.of(value)

    override fun numOf(value: Short): Integer =
        Numeric.of(value)

    override fun numOf(value: Byte): Integer =
        Numeric.of(value)

    override fun numOf(value: String): Numeric =
        Numeric.of(value)

    override fun numOf(value: Number): Numeric =
        Numeric.of(value)

    override fun intOf(value: BigInteger): Integer =
        Integer.of(value)

    override fun intOf(value: Int): Integer =
        Integer.of(value)

    override fun intOf(value: Long): Integer =
        Integer.of(value)

    override fun intOf(value: Short): Integer =
        Integer.of(value)

    override fun intOf(value: Byte): Integer =
        Integer.of(value)

    override fun intOf(value: String): Integer =
        Integer.of(value)

    override fun intOf(value: String, radix: Int): Integer =
        Integer.of(value, radix)

    override fun realOf(value: BigDecimal): Real =
        Real.of(value)

    override fun realOf(value: Double): Real =
        Real.of(value)

    override fun realOf(value: Float): Real =
        Real.of(value)

    override fun realOf(value: String): Real =
        Real.of(value)
}
