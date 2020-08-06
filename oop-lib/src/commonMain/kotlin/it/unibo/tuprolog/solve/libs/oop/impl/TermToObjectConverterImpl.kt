package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.libs.oop.TermToObjectConversionException
import it.unibo.tuprolog.solve.libs.oop.NullRef
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.TermToObjectConverter
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.collections.Set
import kotlin.reflect.KClass

internal class TermToObjectConverterImpl : TermToObjectConverter {
    override fun possibleConversions(term: Term): Sequence<Any?> {
        return when (term) {
            is NullRef -> sequenceOf(null)
            is ObjectRef -> sequenceOf(term.`object`)
            is Truth -> {
                sequenceOf(term.isTrue, term.value)
            }
            is Atom -> {
                sequenceOf(term.value) + if (term.value.length == 1) {
                    sequenceOf(term.value[0])
                } else {
                    emptySequence()
                }
            }
            is Real -> sequenceOf(
                term.decimalValue.toDouble(),
                term.decimalValue,
                term.decimalValue.toFloat()
            )
            is Integer -> sequenceOf(
                term.intValue.toInt(),
                term.intValue.toLong(),
                term.intValue,
                term.intValue.toShort(),
                term.intValue.toByte()
            )
            else -> throw TermToObjectConversionException(term)
        }
    }

    override fun admissibleTypes(term: Term): Set<KClass<*>>  {
        return when (term) {
            is NullRef -> setOf(Nothing::class)
            is ObjectRef -> setOf(term.`object`::class)
            is Truth -> setOf(Boolean::class, String::class)
            is Atom -> setOf(String::class, Char::class)
            is Real -> setOf(
                Double::class,
                BigDecimal::class,
                Float::class
            )
            is Integer -> setOf(
                Int::class,
                Long::class,
                BigInteger::class,
                Short::class,
                Byte::class
            )
            else -> throw TermToObjectConversionException(term)
        }
    }
}