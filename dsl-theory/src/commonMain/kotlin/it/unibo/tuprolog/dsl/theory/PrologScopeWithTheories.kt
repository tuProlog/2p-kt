package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.dsl.PrologScope
import it.unibo.tuprolog.dsl.unify.PrologScopeWithUnification
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

interface PrologScopeWithTheories : PrologScopeWithUnification {
    @JsName("theoryOf")
    fun theoryOf(vararg clause: Clause): Theory {
        return Theory.indexedOf(*clause)
    }

    @JsName("theory")
    fun theory(vararg clauseFunctions: PrologScope.() -> Any): Theory = theoryOf(
        *clauseFunctions.map { function ->
            PrologScope.empty().function().let { clause { it } }
        }.toTypedArray()
    )

    companion object {
        @JsName("empty")
        fun empty(): PrologScopeWithTheories = PrologScopeWithTheoriesImpl()
    }
}