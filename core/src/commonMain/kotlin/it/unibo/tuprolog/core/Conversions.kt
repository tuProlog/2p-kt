package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

fun Any.toTerm(): Term = when (this) {
    is Term -> this
    is BigDecimal -> this.toTerm()
    is Double -> this.toTerm()
    is Float -> this.toTerm()
    is BigInteger -> this.toTerm()
    is Long -> this.toTerm()
    is Int -> this.toTerm()
    is Short -> this.toTerm()
    is Byte -> this.toTerm()
    is String -> this.toTerm()
    is Array<*> -> this.map { it!!.toTerm() }.toTerm()
    is Sequence<*> -> this.map { it!!.toTerm() }.toTerm()
    is Iterable<*> -> this.map { it!!.toTerm() }.toTerm()
    else -> throw IllegalArgumentException("Cannot convert ${this::class} into ${Term::class}")
}

fun BigInteger.toTerm(): Integer = Numeric.of(this)

fun BigDecimal.toTerm(): Real = Numeric.of(this)

fun Float.toTerm(): Real = Numeric.of(this)

fun Double.toTerm(): Real = Numeric.of(this)

fun Int.toTerm(): Integer = Numeric.of(this)

fun Long.toTerm(): Integer = Numeric.of(this)

fun Short.toTerm(): Integer = Numeric.of(this)

fun Byte.toTerm(): Integer = Numeric.of(this)

fun Number.toTerm(): Numeric = Numeric.of(this)

fun String.toTerm(): Term =
        when {
            this matches Var.VAR_REGEX_PATTERN -> this.asVar()
            else -> this.asAtom()
        }

fun String.asAtom(): Atom = Atom.of(this)

fun String.asVar(): Var = Var.of(this)

fun kotlin.collections.List<Term>.toTerm(): List = List.of(this)

fun Sequence<Term>.toTerm(): List = this.asIterable().toTerm()

fun Iterable<Term>.toTerm(): List = List.of(this)

fun Array<out Term>.toTerm(): List = List.of(*this)