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

    @JsName("logicListOfAny")
    fun logicListOf(vararg terms: Any): LogicList = this.logicListOf(*terms.map { it.toTerm() }.toTypedArray())

    @JsName("blockOfAny")
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

    @JsName("logicList")
    fun logicList(
        vararg items: Any,
        tail: Any? = null,
    ): LogicList =
        listOf(*items).map { it.toTerm() }.let {
            if (tail != null) {
                logicListFrom(it, last = tail.toTerm())
            } else {
                logicListOf(it)
            }
        }

    @JsName("rule")
    fun rule(function: S.() -> Any): Rule = newScope().function().toTerm() as Rule

    fun clause(function: S.() -> Any): Clause =
        newScope().function().toSpecificSubTypeOfTerm(Clause::class, this::factOf)

    @JsName("directive")
    fun directive(function: S.() -> Any): Directive =
        newScope().function().toSpecificSubTypeOfTerm(Directive::class, this::directiveOf)

    @JsName("fact")
    fun fact(function: S.() -> Any): Fact = newScope().function().toSpecificSubTypeOfTerm(Fact::class, this::factOf)
}
