package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.toTerm as extToTerm

internal interface AnyToTermConverter {

    val prolog: Prolog

    fun Any.toTerm(): Term = when {
        this is Term -> this
        this is ToTermConvertible -> this.toTerm()
        this.isInteger -> toInteger()
        this.isReal -> toReal()
        this is String -> when {
            isVariable -> toVar()
            else -> toAtom()
        }
        this is Array<*> -> this.map { it!!.toTerm() }.extToTerm()
        this is Sequence<*> -> this.map { it!!.toTerm() }.extToTerm()
        this is Iterable<*> -> this.map { it!!.toTerm() }.extToTerm()
        else -> raiseErrorConvertingTo(Term::class)
    }

    val Any.isInteger: Boolean
    val Any.isReal: Boolean
    val Any.isBoolean: Boolean
    val String.isVariable: Boolean
        get() = this matches Var.VAR_REGEX_PATTERN

    fun Any.toInteger(): Integer
    fun Any.toReal(): Real
    fun String.toVariable(): Var = prolog.varOf(this)
    fun String.toAtom(): Atom = prolog.atomOf(this)
    fun Any.toTruth(): Truth = prolog.truthOf(this as Boolean)
}