package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

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