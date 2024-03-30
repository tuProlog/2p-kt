package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.js.JsName

interface LogicProgrammingScopeWithSubstitutions<S : LogicProgrammingScopeWithSubstitutions<S>> :
    BaseLogicProgrammingScope<S> {
    @JsName("varTo")
    infix fun Var.to(termObject: Any): Substitution.Unifier = Substitution.of(this, termObject.toTerm())

    @JsName("stringTo")
    infix fun String.to(termObject: Any): Substitution.Unifier = Substitution.of(varOf(this), termObject.toTerm())

    @JsName("substitutionGet")
    operator fun Substitution.get(term: Any): Term? =
        when (val t = term.toTerm()) {
            is Var -> this[t]
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    @JsName("substitutionContainsKey")
    fun Substitution.containsKey(term: Any): Boolean =
        when (val t = term.toTerm()) {
            is Var -> this.containsKey(t)
            else -> term.raiseErrorConvertingTo(Var::class)
        }

    @JsName("substitutionContains")
    operator fun Substitution.contains(term: Any): Boolean = containsKey(term)

    @JsName("substitutionContainsValue")
    fun Substitution.containsValue(term: Any): Boolean = this.containsValue(term.toTerm())
}
