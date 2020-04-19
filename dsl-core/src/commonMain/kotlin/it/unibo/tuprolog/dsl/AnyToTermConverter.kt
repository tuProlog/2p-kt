package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.*
import kotlin.js.JsName
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

import it.unibo.tuprolog.core.toTerm as extToTerm


internal interface AnyToTermConverter {

    @JsName("prolog")
    val prolog: Prolog

    @JsName("toTerm")
    fun toTerm(any: Any): Term = when (any) {
        is Term -> any
        is ToTermConvertible -> any.toTerm()
        is BigInteger -> prolog.numOf(any)
        is BigDecimal -> prolog.numOf(any)
        is Number -> when {
            any.isInteger -> any.toInteger()
            else -> any.toReal()
        }
        is String -> when {
            any.isVariable -> prolog.varOf(any)
            else -> prolog.atomOf(any)
        }
        is Boolean -> any.toTruth()
        is Array<*> -> any.map { toTerm(it!!) }.extToTerm()
        is Sequence<*> -> any.map { toTerm(it!!) }.extToTerm()
        is Iterable<*> -> any.map { toTerm(it!!) }.extToTerm()
        else -> any.raiseErrorConvertingTo(Term::class)
    }

    val Number.isInteger: Boolean

    val String.isVariable: Boolean
        get() = this matches Var.VAR_REGEX_PATTERN

    @JsName("numberToInteger")
    fun Number.toInteger(): Integer

    @JsName("numberToReal")
    fun Number.toReal(): Real

    @JsName("stringToVariable")
    fun String.toVariable(): Var = prolog.varOf(this)

    @JsName("stringToAtom")
    fun String.toAtom(): Atom = prolog.atomOf(this)

    @JsName("booleanToTruth")
    fun Boolean.toTruth(): Truth = prolog.truthOf(this)

    companion object {
        @JsName("of")
        fun of(prolog: Prolog): AnyToTermConverter {
            return AnyToTermConverterImpl(prolog)
        }
    }
}