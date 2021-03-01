package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface NotableFlag {

    @JsName("name")
    val name: String

    @JsName("defaultValue")
    val defaultValue: Term

    @JsName("admissibleValues")
    val admissibleValues: Sequence<Term>

    @JsName("isAdmissibleValue")
    fun isAdmissibleValue(value: Term): Boolean =
        value in admissibleValues

    @JsName("toPair")
    fun toPair(): Pair<String, Term> =
        this to defaultValue

    @JsName("to")
    infix fun to(value: Term): Pair<String, Term> =
        name to value.also {
            require(isAdmissibleValue(it)) {
                "$value is not an admissible value for flag $name"
            }
        }

    companion object {
        @JsName("fromName")
        @JvmStatic
        fun fromName(name: String): NotableFlag? =
            sequenceOf(
                DoubleQuotes,
                LastCallOptimization,
                MaxArity,
                Unknown
            ).firstOrNull { it.name == name }
    }
}
