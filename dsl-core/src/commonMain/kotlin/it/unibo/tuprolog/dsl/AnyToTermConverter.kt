package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.*
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

import it.unibo.tuprolog.core.toTerm as extToTerm


internal interface AnyToTermConverter {

    val prolog: Prolog

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
        is Array<*> -> any.map { toTerm(it!!) }.extToTerm()
        is Sequence<*> -> any.map { toTerm(it!!) }.extToTerm()
        is Iterable<*> -> any.map { toTerm(it!!) }.extToTerm()
        else -> raiseErrorConvertingTo(Term::class)
    }

    val Number.isInteger: Boolean
    val Any.isBoolean: Boolean
    val String.isVariable: Boolean
        get() = this matches Var.VAR_REGEX_PATTERN

    fun Number.toInteger(): Integer
    fun Number.toReal(): Real
    fun String.toVariable(): Var = prolog.varOf(this)
    fun String.toAtom(): Atom = prolog.atomOf(this)
    fun Any.toTruth(): Truth = prolog.truthOf(this as Boolean)

    companion object {
        fun of(prolog: Prolog): AnyToTermConverter {
            return AnyToTermConverterImpl(prolog)
        }
    }
}