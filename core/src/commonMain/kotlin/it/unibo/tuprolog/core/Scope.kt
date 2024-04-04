package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.ScopeImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Scope : TermFactory, SubstitutionFactory {
    @JsName("variables")
    val variables: Map<String, Var>

    @JsName("_fail")
    val fail: Truth

    @JsName("_emptyLogicList")
    val emptyLogicList: EmptyList

    @JsName("_emptyBlock")
    val emptyBlock: EmptyBlock

    @JsName("anonymous")
    fun anonymous(): Var

    @Suppress("PropertyName")
    @JsName("_")
    val `_`: Var

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

    @JsName("whatever")
    fun whatever(): Var

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
