package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.*
import kotlin.js.JsName
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

import it.unibo.tuprolog.core.toTerm as extToTerm


internal interface AnyToTermConverter {

    @JsName("prologScope")
    val prologScope: PrologScope

    @JsName("toTerm")
    fun toTerm(any: Any): Term = when (any) {
        is Term -> any
        is ToTermConvertible -> any.toTerm()
        is BigInteger -> prologScope.numOf(any)
        is BigDecimal -> prologScope.numOf(any)
        is Number -> when {
            any.isInteger -> any.toInteger()
            else -> any.toReal()
        }
        is String -> when {
            any.isVariable -> prologScope.varOf(any)
            else -> prologScope.atomOf(any)
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
    fun String.toVariable(): Var = prologScope.varOf(this)

    @JsName("stringToAtom")
    fun String.toAtom(): Atom = prologScope.atomOf(this)

    @JsName("booleanToTruth")
    fun Boolean.toTruth(): Truth = prologScope.truthOf(this)

    companion object {
        @JsName("of")
        fun of(prologScope: PrologScope): AnyToTermConverter {
            return AnyToTermConverterImpl(prologScope)
        }
    }
}