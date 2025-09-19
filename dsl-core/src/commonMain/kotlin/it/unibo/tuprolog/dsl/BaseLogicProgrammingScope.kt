package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.reflect.KClass

interface BaseLogicProgrammingScope<S : BaseLogicProgrammingScope<S>> : Scope {
    @JsName("termificator")
    val termificator: Termificator

    @JsName("convertToTerm")
    fun Any.toTerm(): Term = termificator.termify(this)

    @Suppress("UNCHECKED_CAST")
    @JsName("convertToSpecificSubTypeOfTerm")
    fun <T : Term> Any.toSpecificSubTypeOfTerm(
        type: KClass<T>,
        converter: (Struct) -> T,
    ): T {
        val t = toTerm()
        return when {
            type.isInstance(t) -> t as T
            t is Struct -> converter(t)
            else -> raiseErrorConvertingTo(type)
        }
    }

    @JsName("newScope")
    fun newScope(): S
}
