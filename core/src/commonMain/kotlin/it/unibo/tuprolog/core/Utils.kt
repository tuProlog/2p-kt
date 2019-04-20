package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

typealias Substitution = Map<Var, Term>

fun Term.groundTo(substitution: Substitution): Term {
    return when {
        this.isGround -> this
        this is Var -> substitution[this] ?: this
        this is Struct -> Struct.of(this.functor, this.argsList.map { it.groundTo(substitution) })
        else -> this
    }
}

fun Term.groundTo(substitution: Substitution, vararg substitutions: Substitution): Term {
    return this.groundTo(substitutionOf(substitution, *substitutions))
}

operator fun Term.get(substitution: Substitution, vararg substitutions: Substitution): Term {
    return this.groundTo(substitution, *substitutions)
}

fun Substitution.ground(term: Term): Term {
    return term[this]
}

fun Array<Substitution>.ground(term: Term): Term {
    return term[substitutionOf(this[0], *this.sliceArray(1..lastIndex))]
}

inline fun atomOf(value: String): Atom {
    return Atom.of(value)
}

inline fun structOf(functor: String, arg1: Term, vararg args: Term): Struct {
    return Struct.of(functor, arg1, *args)
}

inline fun varOf(name: String = Var.ANONYMOUS_VAR_NAME): Var {
    return Var.of(name)
}

inline fun lstOf(vararg terms: Term): List {
    return List.of(*terms)
}

inline fun coupleOf(term1: Term, vararg terms: Term): Couple {
    return List.from(sequenceOf(term1) + sequenceOf(*terms)) as Couple
}

inline fun anonymous(): Term {
    return Var.anonymous()
}

inline fun whatever(): Term {
    return Var.anonymous()
}

fun numOf(decimal: BigDecimal): Real {
    return Real.of(decimal)
}

fun numOf(decimal: Double): Real {
    return Real.of(decimal)
}

fun numOf(decimal: Float): Real {
    return Real.of(decimal)
}

fun numOf(integer: BigInteger): Integral {
    return Integral.of(integer)
}

fun numOf(integer: Int): Integral {
    return Integral.of(integer)
}

fun numOf(integer: Long): Integral {
    return Integral.of(integer)
}

fun numOf(integer: Short): Integral {
    return Integral.of(integer)
}

fun numOf(integer: Byte): Integral {
    return Integral.of(integer)
}

fun numOf(number: String): Numeric {
    return try {
        Integral.of(number)
    } catch (ex: NumberFormatException) {
        Real.of(number)
    }

}

inline operator fun Any.div(obj: Any): Substitution {
    return when {
        this is Var -> substitutionOf(this, obj.toTerm())
        this is String -> substitutionOf(this, obj.toTerm())
        else -> throw IllegalArgumentException("${obj::class} cannot be converted into ${Var::class}")
    }
}

inline operator fun String.invoke(term: Any, vararg terms: Any): Term {
    return Struct.of(this, (sequenceOf(term) + sequenceOf(*terms)).map { it.toTerm() })
}

fun Any.toTerm(): Term {
    return when(this) {
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
        is Iterable<*> -> this.map { it!!.toTerm() }.toTerm()
        else -> throw IllegalArgumentException("Cannot convert ${this::class} into ${Term::class}")
    }
}

inline fun BigInteger.toTerm(): Integral {
    return Numeric.of(this)
}

inline fun BigDecimal.toTerm(): Real {
    return Numeric.of(this)
}

inline fun Float.toTerm(): Real {
    return Numeric.of(this)
}

inline fun Double.toTerm(): Real {
    return Numeric.of(this)
}

inline fun Int.toTerm(): Integral {
    return Numeric.of(this)
}

inline fun Long.toTerm(): Integral {
    return Numeric.of(this)
}

inline fun Short.toTerm(): Integral {
    return Numeric.of(this)
}

inline fun Byte.toTerm(): Integral {
    return Numeric.of(this)
}

inline fun Number.toTerm(): Numeric {
    return Numeric.of(this)
}

inline fun String.toTerm(): Term {
    return if (Var.WELL_FORMED_NAME_PATTERN.matches(this)) {
        this.asVar()
    } else {
        this.asAtom()
    }
}

inline fun String.asAtom(): Atom {
    return Atom.of(this)
}

inline fun String.asVar(): Var {
    return Var.of(this)
}

inline fun kotlin.collections.List<Term>.toTerm(): List {
    return List.of(this)
}

inline fun Iterable<Term>.toTerm(): List {
    return List.of(this)
}

inline fun Array<out Term>.toTerm(): List {
    return List.of(*this)
}

fun substitutionOf(substitution: Substitution, vararg substitutions: Substitution): Substitution {
    return substitutions.fold(substitution) { s1, s2 -> s1 + s2}
}

fun substitutionOf(v1: Any, t1: Term): Substitution {
    return v1 / t1
}

fun substitutionOf(v1: Var, t1: Term): Substitution {
    return mapOf(Pair(v1, t1))
}

fun substitutionOf(v1: String, t1: Term): Substitution {
    return substitutionOf(varOf(v1), t1)
}

fun substitutionOf(v1: Var, t1: Term, v2: Var, t2: Term): Substitution {
    return mapOf(Pair(v1, t1), Pair(v2, t2))
}

fun substitutionOf(v1: String, t1: Term, v2: String, t2: Term): Substitution {
    return substitutionOf(varOf(v1), t1, varOf(v2), t2)
}

