package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Tuple
import kotlin.js.JsName
import it.unibo.tuprolog.core.List as LogicList

interface MinimalLogicProgrammingScope<S : MinimalLogicProgrammingScope<S>> : BaseLogicProgrammingScope<S> {
    @JsName("stringInvoke")
    operator fun String.invoke(
        term: Any,
        vararg terms: Any,
    ): Struct = structOf(this, sequenceOf(term, *terms).map { it.toTerm() })

    @JsName("structOfAny")
    fun structOf(
        functor: String,
        vararg args: Any,
    ): Struct = structOf(functor, *args.map { it.toTerm() }.toTypedArray())

    @JsName("tupleOfAny")
    fun tupleOf(vararg terms: Any): Tuple = tupleOf(*terms.map { it.toTerm() }.toTypedArray())

    @JsName("listOfAny")
    fun listOf(vararg terms: Any): LogicList = this.listOf(*terms.map { it.toTerm() }.toTypedArray())

    @JsName("blockOf")
    fun blockOf(vararg terms: Any): Block = this.blockOf(*terms.map { it.toTerm() }.toTypedArray())

    @JsName("factOfAny")
    fun factOf(term: Any): Fact = factOf(term.toTerm() as Struct)

    @JsName("consOfAny")
    fun consOf(
        head: Any,
        tail: Any,
    ): Cons = consOf(head.toTerm(), tail.toTerm())

    @JsName("directiveOfAny")
    fun directiveOf(
        term: Any,
        vararg terms: Any,
    ): Directive = directiveOf(term.toTerm(), *terms.map { it.toTerm() }.toTypedArray())

    @JsName("withScope")
    fun <R> scope(function: S.() -> R): R = newScope().function()

    @JsName("list")
    fun list(
        vararg items: Any,
        tail: Any? = null,
    ): LogicList =
        ktListOf(*items).map { it.toTerm() }.let {
            if (tail != null) {
                listFrom(it, last = tail.toTerm())
            } else {
                listOf(it)
            }
        }

    @JsName("rule")
    fun rule(function: S.() -> Any): Rule = newScope().function().toTerm() as Rule

    fun clause(function: S.() -> Any): Clause =
        newScope().function().let {
            when (val t = it.toTerm()) {
                is Clause -> t
                is Struct -> factOf(t)
                else -> it.raiseErrorConvertingTo(Clause::class)
            }
        }

    @JsName("directive")
    fun directive(function: S.() -> Any): Directive =
        newScope().function().let {
            when (val t = it.toTerm()) {
                is Directive -> t
                is Struct -> directiveOf(t)
                else -> it.raiseErrorConvertingTo(Directive::class)
            }
        }

    @JsName("fact")
    fun fact(function: S.() -> Any): Fact =
        newScope().function().let {
            when (val t = it.toTerm()) {
                is Fact -> t
                is Struct -> factOf(t)
                else -> it.raiseErrorConvertingTo(Fact::class)
            }
        }
}
