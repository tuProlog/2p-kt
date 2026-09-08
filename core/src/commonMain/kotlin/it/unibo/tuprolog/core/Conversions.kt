@file:JvmName("Conversions")

package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.VAR_NAME_PATTERN
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmName

/** Converts this [BigInteger] into an [Integer]. Shorthand for [Numeric.of]. */
@JsName("bigIntegerToTerm")
fun BigInteger.toTerm(): Integer = Numeric.of(this)

/** Converts this [BigDecimal] into a [Real]. Shorthand for [Numeric.of]. */
@JsName("bigDecimalToTerm")
fun BigDecimal.toTerm(): Real = Numeric.of(this)

/** Converts this [Float] into a [Real]. Shorthand for [Numeric.of]. */
@JsName("floatToTerm")
fun Float.toTerm(): Real = Numeric.of(this)

/** Converts this [Double] into a [Real]. Shorthand for [Numeric.of]. */
@JsName("doubleToTerm")
fun Double.toTerm(): Real = Numeric.of(this)

/** Converts this [Int] into an [Integer]. Shorthand for [Numeric.of]. */
@JsName("intToTerm")
fun Int.toTerm(): Integer = Numeric.of(this)

/** Converts this [Long] into an [Integer]. Shorthand for [Numeric.of]. */
@JsName("longToTerm")
fun Long.toTerm(): Integer = Numeric.of(this)

/** Converts this [Short] into an [Integer]. Shorthand for [Numeric.of]. */
@JsName("shortToTerm")
fun Short.toTerm(): Integer = Numeric.of(this)

/** Converts this [Byte] into an [Integer]. Shorthand for [Numeric.of]. */
@JsName("byteToTerm")
fun Byte.toTerm(): Integer = Numeric.of(this)

/** Converts this [Number] into a [Numeric]. Shorthand for [Numeric.of]. */
@JsName("numberToTerm")
fun Number.toTerm(): Numeric = Numeric.of(this)

/**
 * Converts this [String] into a [Term]: a [Var] if it matches [Terms.VAR_NAME_PATTERN] (i.e. it looks like a
 * legal variable name), or an [Atom] otherwise.
 */
@JsName("stringToTerm")
fun String.toTerm(): Term =
    when {
        this matches VAR_NAME_PATTERN -> this.toVar()
        else -> this.toAtom()
    }

/** Converts this [String] into an [Atom] with it as [Atom.value]. Shorthand for [Atom.of]. */
@JsName("stringToAtom")
fun String.toAtom(): Atom = Atom.of(this)

/** Converts this [String] into a [Var] named after it. Shorthand for [Var.of]. */
@JsName("stringToVar")
fun String.toVar(): Var = Var.of(this)

/** Converts this Kotlin [kotlin.collections.List] of [Term]s into a logic [List]. Shorthand for [List.of]. */
@JsName("listToTerm")
fun kotlin.collections.List<Term>.toTerm(): List = List.of(this)

/** Converts this [Sequence] of [Term]s into a logic [List]. Shorthand for [List.of]. */
@JsName("sequenceToTerm")
fun Sequence<Term>.toTerm(): List = this.asIterable().toTerm()

/** Converts this [Iterable] of [Term]s into a logic [List]. Shorthand for [List.of]. */
@JsName("iterableToTerm")
fun Iterable<Term>.toTerm(): List = List.of(this)

/** Converts this [Array] of [Term]s into a logic [List]. Shorthand for [List.of]. */
@JsName("arrayToTerm")
fun Array<out Term>.toTerm(): List = List.of(*this)

/** Conversion from a raw `Map<Var, Term>` to the [Substitution.Unifier] type */
@JsName("asUnifier")
fun Map<Var, Term>.asUnifier(): Substitution.Unifier = this as? Substitution.Unifier ?: Substitution.of(this)
