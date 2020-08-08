package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.dsl.PrologScope
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

interface PrologScopeWithUnification : PrologScope, Unificator {

    @JsName("anyMguWith")
    infix fun Any.mguWith(other: Any): Substitution =
        this@PrologScopeWithUnification.mgu(this.toTerm(), other.toTerm())

    @JsName("anyMatches")
    infix fun Any.matches(other: Any): Boolean =
        this@PrologScopeWithUnification.match(this.toTerm(), other.toTerm())

    @JsName("anyUnifyWith")
    infix fun Any.unifyWith(other: Any): Term? =
        this@PrologScopeWithUnification.unify(this.toTerm(), other.toTerm())

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
        @JsName("empty")
        fun empty(): PrologScopeWithUnification = PrologScopeWithUnificationImpl()

        @JsName("of")
        fun of(unificator: Unificator): PrologScopeWithUnification = PrologScopeWithUnificationImpl(unificator)
    }
}