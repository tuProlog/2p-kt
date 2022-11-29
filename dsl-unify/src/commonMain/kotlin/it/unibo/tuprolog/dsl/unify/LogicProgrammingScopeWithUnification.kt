package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.LogicProgrammingScope
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

interface LogicProgrammingScopeWithUnification : LogicProgrammingScope, Unificator {

    @JsName("unificator")
    val unificator: Unificator

    @JsName("anyMguWith")
    infix fun Any.mguWith(other: Any): Substitution =
        this@LogicProgrammingScopeWithUnification.mgu(this.toTerm(), other.toTerm())

    @JsName("anyMatches")
    infix fun Any.matches(other: Any): Boolean =
        this@LogicProgrammingScopeWithUnification.match(this.toTerm(), other.toTerm())

    @JsName("anyUnifyWith")
    infix fun Any.unifyWith(other: Any): Term? =
        this@LogicProgrammingScopeWithUnification.unify(this.toTerm(), other.toTerm())

    @JsName("mguAny")
    fun mgu(term1: Any, term2: Any, occurCheckEnabled: Boolean = true): Substitution =
        mgu(term1.toTerm(), term2.toTerm(), occurCheckEnabled)

    @JsName("matchAny")
    fun match(term1: Any, term2: Any, occurCheckEnabled: Boolean = true): Boolean =
        match(term1.toTerm(), term2.toTerm(), occurCheckEnabled)

    @JsName("unifyAny")
    fun unify(term1: Any, term2: Any, occurCheckEnabled: Boolean = true): Term? =
        unify(term1.toTerm(), term2.toTerm(), occurCheckEnabled)

    companion object {
        @JsName("of")
        fun of(
            unificator: Unificator = Unificator.default
        ): LogicProgrammingScopeWithUnification = LogicProgrammingScopeWithUnificationImpl(unificator)
    }
}
