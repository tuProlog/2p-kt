package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.dsl.Prolog
import it.unibo.tuprolog.dsl.unify.PrologWithUnification
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

interface PrologWithTheories : PrologWithUnification {
    @JsName("theoryOf")
    fun theoryOf(vararg clause: Clause): Theory {
        return Theory.indexedOf(*clause)
    }

    @JsName("theory")
    fun theory(vararg clauseFunctions: Prolog.() -> Any): Theory = theoryOf(
        *clauseFunctions.map { function ->
            Prolog.empty().function().let { clause { it } }
        }.toTypedArray()
    )

    companion object {
        @JsName("empty")
        fun empty(): PrologWithTheories = PrologWithTheoriesImpl()
    }
}