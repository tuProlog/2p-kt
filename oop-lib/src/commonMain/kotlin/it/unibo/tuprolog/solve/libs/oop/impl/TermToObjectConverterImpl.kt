package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.libs.oop.*
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.reflect.KClass
import kotlin.collections.Set

internal class TermToObjectConverterImpl : TermToObjectConverter {
    override fun convertInto(type: KClass<*>, term: Term): Any? {
        return when (term) {
            is NullRef -> null
            is ObjectRef -> {
                if (term.`object`::class isSubtypeOf type) {
                    term.`object`
                } else {
                    throw TermToObjectConversionException(term, type)
                }
            }
            is Truth -> when {
                Boolean::class isSubtypeOf type -> term.isTrue
                String::class isSubtypeOf type -> term.value
                else -> throw TermToObjectConversionException(term, type)
            }
            is Atom -> when {
                String::class isSubtypeOf type -> term.value
                Char::class isSubtypeOf type -> term.value[0]
                else -> throw TermToObjectConversionException(term, type)
            }
            is Real -> when {
                BigDecimal::class isSubtypeOf type -> term.decimalValue
                Double::class isSubtypeOf type -> term.decimalValue.toDouble()
                Float::class isSubtypeOf type -> term.decimalValue.toFloat()
                else -> throw TermToObjectConversionException(term, type)
            }
            is Integer -> when {
                BigInteger::class isSubtypeOf type -> term.intValue
                Long::class isSubtypeOf type -> term.intValue.toLongExact()
                Int::class isSubtypeOf type -> term.intValue.toIntExact()
                Short::class isSubtypeOf type -> term.intValue.toShortExact()
                Byte::class isSubtypeOf type -> term.intValue.toByteExact()
                else -> throw TermToObjectConversionException(term, type)
            }
            else -> throw TermToObjectConversionException(term)
        }
    }

    override fun possibleConversions(term: Term): Sequence<Any?> {
        return admissibleTypes(term).asSequence().map { convertInto(it, term) }
    }

    override fun admissibleTypes(term: Term): Set<KClass<*>>  {
        return when (term) {
            is NullRef -> setOf(Nothing::class)
            is ObjectRef -> setOf(term.`object`::class)
            is Truth -> setOf(Boolean::class, String::class)
            is Atom -> mutableSetOf<KClass<*>>(String::class).also {
                if (term.value.length == 1) {
                    it += Char::class
                }
            }
            is Real -> setOf(
                Double::class,
                BigDecimal::class,
                Float::class
            )
            is Integer -> mutableSetOf<KClass<*>>(BigInteger::class).also {
                if (term.intValue in BigInteger.of(Long.MIN_VALUE)..BigInteger.of(Long.MAX_VALUE)) {
                    it += Long::class
                }
                if (term.intValue in BigInteger.of(Int.MIN_VALUE)..BigInteger.of(Int.MAX_VALUE)) {
                    it += Int::class
                }
                if (term.intValue in BigInteger.of(Short.MIN_VALUE.toInt())..BigInteger.of(Short.MAX_VALUE.toInt())) {
                    it += Short::class
                }
                if (term.intValue in BigInteger.of(Byte.MIN_VALUE.toInt())..BigInteger.of(Byte.MAX_VALUE.toInt())) {
                    it += Byte::class
                }
            }
            else -> throw TermToObjectConversionException(term)
        }
    }
}