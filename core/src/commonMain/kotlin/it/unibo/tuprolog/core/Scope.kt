package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.ScopeImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.core.List as LogicList

interface Scope {
    @JsName("variables")
    val variables: Map<String, Var>

    @JsName("fail")
    val fail: Truth

    @JsName("emptyLogicList")
    val emptyLogicList: EmptyList

    @JsName("emptyBlock")
    val emptyBlock: EmptyBlock

    @Suppress("PropertyName")
    @JsName("_")
    val `_`: Var
        get() = anonymous()

    @JsName("containsVar")
    operator fun contains(variable: Var): Boolean

    @JsName("contains")
    operator fun contains(variable: String): Boolean

    @JsName("get")
    operator fun get(variable: String): Var?

    @JsName("where")
    fun where(lambda: Scope.() -> Unit): Scope

    @JsName("with")
    fun <R> with(lambda: Scope.() -> R): R

    @JsName("varOf")
    fun varOf(name: String): Var

    @JsName("varOfChar")
    fun varOf(name: Char): Var

    @JsName("atomOf")
    fun atomOf(value: String): Atom

    @JsName("atomOfChar")
    fun atomOf(value: Char): Atom

    @JsName("structOf")
    fun structOf(
        functor: String,
        vararg args: Term,
    ): Struct

    @JsName("structOfSequence")
    fun structOf(
        functor: String,
        args: Sequence<Term>,
    ): Struct

    @JsName("structOfIterable")
    fun structOf(
        functor: String,
        args: Iterable<Term>,
    ): Struct

    @JsName("structOfList")
    fun structOf(
        functor: String,
        args: List<Term>,
    ): Struct

    @JsName("tupleOf")
    fun tupleOf(vararg terms: Term): Tuple

    @JsName("tupleOfIterable")
    fun tupleOf(terms: Iterable<Term>): Tuple

    @JsName("tupleOfSequence")
    fun tupleOf(terms: Sequence<Term>): Tuple

    @JsName("logicListOf")
    fun logicListOf(vararg terms: Term): LogicList

    @JsName("logicListOfIterable")
    fun logicListOf(terms: Iterable<Term>): LogicList

    @JsName("logicListOfSequence")
    fun logicListOf(terms: Sequence<Term>): LogicList

    @JsName("logicListFromFrom")
    fun logicListFrom(
        vararg terms: Term,
        last: Term? = null,
    ): LogicList

    @JsName("logicListFromIterable")
    fun logicListFrom(
        terms: Iterable<Term>,
        last: Term? = null,
    ): LogicList

    @JsName("logicListFromSequence")
    fun logicListFrom(
        terms: Sequence<Term>,
        last: Term? = null,
    ): LogicList

    @JsName("blockOf")
    fun blockOf(vararg terms: Term): Block

    @JsName("blockOfIterable")
    fun blockOf(terms: Iterable<Term>): Block

    @JsName("blockOfSequence")
    fun blockOf(terms: Sequence<Term>): Block

    @JsName("factOf")
    fun factOf(head: Struct): Fact

    @JsName("ruleOf")
    fun ruleOf(
        head: Struct,
        body1: Term,
        vararg body: Term,
    ): Rule

    @JsName("directiveOf")
    fun directiveOf(
        body1: Term,
        vararg body: Term,
    ): Directive

    @JsName("clauseOf")
    fun clauseOf(
        head: Struct?,
        vararg body: Term,
    ): Clause

    @JsName("consOf")
    fun consOf(
        head: Term,
        tail: Term,
    ): Cons

    @JsName("indicatorOf")
    fun indicatorOf(
        name: Term,
        arity: Term,
    ): Indicator

    @JsName("indicatorOfStringInt")
    fun indicatorOf(
        name: String,
        arity: Int,
    ): Indicator

    @JsName("anonymous")
    fun anonymous(): Var

    @JsName("whatever")
    fun whatever(): Var

    @JsName("numOfBigDecimal")
    fun numOf(value: BigDecimal): Real

    @JsName("numOfDouble")
    fun numOf(value: Double): Real

    @JsName("numOfFloat")
    fun numOf(value: Float): Real

    @JsName("numOfBigInteger")
    fun numOf(value: BigInteger): Integer

    @JsName("numOfInt")
    fun numOf(value: Int): Integer

    @JsName("numOfLong")
    fun numOf(value: Long): Integer

    @JsName("numOfShort")
    fun numOf(value: Short): Integer

    @JsName("numOfByte")
    fun numOf(value: Byte): Integer

    @JsName("parseNum")
    fun numOf(value: String): Numeric

    @JsName("numOf")
    fun numOf(value: Number): Numeric

    @JsName("intOfBigInteger")
    fun intOf(value: BigInteger): Integer

    @JsName("intOf")
    fun intOf(value: Int): Integer

    @JsName("intOfLong")
    fun intOf(value: Long): Integer

    @JsName("intOfShort")
    fun intOf(value: Short): Integer

    @JsName("intOfByte")
    fun intOf(value: Byte): Integer

    @JsName("parseInt")
    fun intOf(value: String): Integer

    @JsName("parseIntRadix")
    fun intOf(
        value: String,
        radix: Int,
    ): Integer

    @JsName("realOfBigDecimal")
    fun realOf(value: BigDecimal): Real

    @JsName("realOf")
    fun realOf(value: Double): Real

    @JsName("realOfFloat")
    fun realOf(value: Float): Real

    @JsName("parseReal")
    fun realOf(value: String): Real

    @JsName("truthOf")
    fun truthOf(value: Boolean): Truth

    @JsName("unifierOf")
    fun unifierOf(vararg assignments: Pair<String, Term>): Substitution.Unifier

    @JsName("substitutionOf")
    fun substitutionOf(vararg assignments: Pair<String, Term>): Substitution

    @JsName("unifierOfIterable")
    fun unifierOf(assignments: Iterable<Pair<Var, Term>>): Substitution.Unifier

    @JsName("unifierOfSequence")
    fun unifierOf(assignments: Sequence<Pair<Var, Term>>): Substitution.Unifier

    @JsName("substitutionOfIterable")
    fun substitutionOf(assignments: Iterable<Pair<Var, Term>>): Substitution

    @JsName("substitutionOfSequence")
    fun substitutionOf(assignments: Sequence<Pair<Var, Term>>): Substitution

    companion object {
        @JvmStatic
        @JsName("empty")
        fun empty(): Scope = ScopeImpl(mutableMapOf())

        @JvmStatic
        @JsName("of")
        fun of(vararg vars: String): Scope = of(vars.map { Var.of(it) })

        @JvmStatic
        @JsName("ofVar")
        fun of(
            `var`: Var,
            vararg vars: Var,
        ): Scope = of(listOf(`var`, *vars))

        @JvmStatic
        @JsName("ofVarIterable")
        fun of(vars: Iterable<Var>): Scope = ScopeImpl(vars.associateByTo(mutableMapOf()) { it.name })

        @JvmStatic
        @JsName("ofVarSequence")
        fun of(vars: Sequence<Var>): Scope = of(vars.asIterable())

        @JvmStatic
        @JsName("ofVariabled")
        fun of(variabled: Variabled): Scope = of(variabled.variables)

        @JvmStatic
        @JsName("emptyWithFunction")
        fun <R> empty(lambda: Scope.() -> R): R = empty().with(lambda)

        @JvmStatic
        @JsName("ofWithFunction")
        fun <R> of(
            vararg vars: String,
            lambda: Scope.() -> R,
        ): R = of(*vars).with(lambda)

        @JvmStatic
        @JsName("ofVarWithFunction")
        fun <R> of(
            `var`: Var,
            vararg vars: Var,
            lambda: Scope.() -> R,
        ): R = of(`var`, *vars).with(lambda)

        @JvmStatic
        @JsName("ofVarIterableWithFunction")
        fun <R> of(
            vars: Iterable<Var>,
            lambda: Scope.() -> R,
        ): R = of(vars).with(lambda)

        @JvmStatic
        @JsName("ofVarSequenceWithFunction")
        fun <R> of(
            vars: Sequence<Var>,
            lambda: Scope.() -> R,
        ): R = of(vars).with(lambda)

        @JvmStatic
        @JsName("ofVariabledWithFunction")
        fun <R> of(
            vars: Variabled,
            lambda: Scope.() -> R,
        ): R = of(vars).with(lambda)
    }
}
