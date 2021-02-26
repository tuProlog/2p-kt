@file:JvmName("Conversions")

package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.VAR_NAME_PATTERN
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("bigIntegerToTerm")
fun BigInteger.toTerm(): Integer = Numeric.of(this)

@JsName("bigDecimalToTerm")
fun BigDecimal.toTerm(): Real = Numeric.of(this)

@JsName("floatToTerm")
fun Float.toTerm(): Real = Numeric.of(this)

@JsName("doubleToTerm")
fun Double.toTerm(): Real = Numeric.of(this)

@JsName("intToTerm")
fun Int.toTerm(): Integer = Numeric.of(this)

@JsName("longToTerm")
fun Long.toTerm(): Integer = Numeric.of(this)

@JsName("shortToTerm")
fun Short.toTerm(): Integer = Numeric.of(this)

@JsName("byteToTerm")
fun Byte.toTerm(): Integer = Numeric.of(this)

@JsName("numberToTerm")
fun Number.toTerm(): Numeric = Numeric.of(this)

@JsName("stringToTerm")
fun String.toTerm(): Term =
    when {
        this matches VAR_NAME_PATTERN -> this.toVar()
        else -> this.toAtom()
    }

@JsName("stringToAtom")
fun String.toAtom(): Atom = Atom.of(this)

@JsName("stringToVar")
fun String.toVar(): Var = Var.of(this)

@JsName("listToTerm")
fun kotlin.collections.List<Term>.toTerm(): List = List.of(this)

@JsName("sequenceToTerm")
fun Sequence<Term>.toTerm(): List = this.asIterable().toTerm()

@JsName("iterableToTerm")
fun Iterable<Term>.toTerm(): List = List.of(this)

@JsName("arrayToTerm")
fun Array<out Term>.toTerm(): List = List.of(*this)

/** Conversion from a raw `Map<Var, Term>` to the [Substitution.Unifier] type */
@JsName("asUnifier")
fun Map<Var, Term>.asUnifier(): Substitution.Unifier =
    this as? Substitution.Unifier ?: Substitution.of(this)
