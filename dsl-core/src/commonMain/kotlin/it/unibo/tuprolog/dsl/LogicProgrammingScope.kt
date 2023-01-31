package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.VariablesProvider
import kotlin.js.JsName
import it.unibo.tuprolog.core.List as LogicList

interface LogicProgrammingScope : PrologStdLibScope, VariablesProvider {

    @JsName("stringInvoke")
    operator fun String.invoke(term: Any, vararg terms: Any): Struct

    @JsName("structOfAny")
    fun structOf(functor: String, vararg args: Any): Struct

    @JsName("anyPlus")
    operator fun Any.plus(other: Any): Struct

    @JsName("anyMinus")
    operator fun Any.minus(other: Any): Struct

    @JsName("anyTimes")
    operator fun Any.times(other: Any): Struct

    @JsName("anyDiv")
    operator fun Any.div(other: Any): Indicator

    /** Creates a structure whose functor is `'='/2` (term unification operator) */
    @JsName("anyEqualsTo")
    infix fun Any.equalsTo(other: Any): Struct

    @JsName("anyNotEqualsTo")
    infix fun Any.notEqualsTo(other: Any): Struct

    @JsName("anyGreaterThan")
    infix fun Any.greaterThan(other: Any): Struct

    @JsName("anyGreaterThanOrEqualsTo")
    infix fun Any.greaterThanOrEqualsTo(other: Any): Struct

    @JsName("anyNonLowerThan")
    infix fun Any.nonLowerThan(other: Any): Struct

    @JsName("anyLowerThan")
    infix fun Any.lowerThan(other: Any): Struct

    @JsName("anyLowerThanOrEqualsTo")
    infix fun Any.lowerThanOrEqualsTo(other: Any): Struct

    @JsName("anyNonGreaterThan")
    infix fun Any.nonGreaterThan(other: Any): Struct

    @JsName("anyIntDiv")
    infix fun Any.intDiv(other: Any): Struct

    @JsName("anyRem")
    operator fun Any.rem(other: Any): Struct

    @JsName("anyAnd")
    infix fun Any.and(other: Any): Struct

    @JsName("anyOr")
    infix fun Any.or(other: Any): Struct

    @JsName("anyPow")
    infix fun Any.pow(other: Any): Struct

    @JsName("anySup")
    infix fun Any.sup(other: Any): Struct

    @JsName("anyIs")
    infix fun Any.`is`(other: Any): Struct

    @JsName("anyThen")
    infix fun Any.then(other: Any): Struct

    @JsName("anyImpliedBy")
    infix fun Any.impliedBy(other: Any): Rule

    @JsName("anyIf")
    infix fun Any.`if`(other: Any): Rule

    @JsName("anyImpliedByVararg")
    fun Any.impliedBy(vararg other: Any): Rule

    @JsName("anyIfVararg")
    fun Any.`if`(vararg other: Any): Rule

    @JsName("tupleOfAny")
    fun tupleOf(vararg terms: Any): Tuple

    @JsName("listOfAny")
    fun listOf(vararg terms: Any): LogicList

    @JsName("blockOf")
    fun blockOf(vararg terms: Any): Block

    @JsName("factOfAny")
    fun factOf(term: Any): Fact

    @JsName("consOfAny")
    fun consOf(head: Any, tail: Any): Cons

    @JsName("directiveOfAny")
    fun directiveOf(term: Any, vararg terms: Any): Directive

    @JsName("scope")
    fun <R> scope(function: LogicProgrammingScope.() -> R): R

    @JsName("list")
    fun list(vararg items: Any, tail: Any? = null): LogicList

    @JsName("rule")
    fun rule(function: LogicProgrammingScope.() -> Any): Rule

    fun clause(function: LogicProgrammingScope.() -> Any): Clause

    @JsName("directive")
    fun directive(function: LogicProgrammingScope.() -> Any): Directive

    @JsName("fact")
    fun fact(function: LogicProgrammingScope.() -> Any): Fact

    @JsName("varTo")
    infix fun Var.to(termObject: Any): Substitution.Unifier

    @JsName("stringTo")
    infix fun String.to(termObject: Any): Substitution.Unifier

    @JsName("substitutionGet")
    operator fun Substitution.get(term: Any): Term?

    @JsName("substitutionContainsKey")
    fun Substitution.containsKey(term: Any): Boolean

    @JsName("substitutionContains")
    operator fun Substitution.contains(term: Any): Boolean

    @JsName("substitutionContainsValue")
    fun Substitution.containsValue(term: Any): Boolean

    companion object {
        @JsName("empty")
        fun empty(): LogicProgrammingScope = of(Scope.empty())

        @JsName("of")
        fun of(scope: Scope): LogicProgrammingScope = LogicProgrammingScopeImpl(scope)
    }
}
