package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.libs.oop.NullRef
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.TermToObjectConverter
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import it.unibo.tuprolog.solve.libs.oop.isSubtypeOf
import it.unibo.tuprolog.solve.libs.oop.primitives.CAST_TEMPLATE
import it.unibo.tuprolog.solve.libs.oop.primitives.DEALIASING_TEMPLATE
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.reflect.KClass

internal class TermToObjectConverterImpl(
    private val typeFactory: TypeFactory,
    private val dealiaser: (Struct) -> TypeRef?
) : TermToObjectConverter {
    override fun convertInto(type: KClass<*>, term: Term): Any? {
        return when (term) {
            is NullRef, is Var -> null
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
                BigDecimal::class isSubtypeOf type -> term.decimalValue
                Double::class isSubtypeOf type -> term.decimalValue.toDouble()
                Float::class isSubtypeOf type -> term.decimalValue.toFloat()
                else -> throw TermToObjectConversionException(term, type)
            }
            is Struct ->
                when {
                    term matches CAST_TEMPLATE -> explicitConversion(term, term[0], term[1])
                    else -> throw TermToObjectConversionException(term)
                }.also {
                    if (it != null && !(it::class isSubtypeOf type)) {
                        throw TermToObjectConversionException(term)
                    }
                }
            else -> throw TermToObjectConversionException(term)
        }
    }

    private fun explicitConversion(castExpression: Struct, term: Term, type: Term): Any? {
        val targetType = getType(castExpression, type)
        try {
            return convertInto(targetType, term)
        } catch (e: TermToObjectConversionException) {
            throw e
        } catch (_: Exception) {
            throw TermToObjectConversionException(term, targetType)
        }
    }

    private fun getType(castExpression: Struct, typeTerm: Term): KClass<*> =
        when (typeTerm) {
            is TypeRef -> typeTerm.type
            is Atom -> {
                typeFactory.typeFromName(typeTerm.value) ?: throw TermToObjectConversionException(castExpression)
            }
            is Struct -> when {
                typeTerm matches DEALIASING_TEMPLATE -> {
                    dealiaser(typeTerm)?.type ?: throw TermToObjectConversionException(castExpression)
                }
                else -> throw TermToObjectConversionException(castExpression)
            }
            else -> throw TermToObjectConversionException(castExpression)
        }

    override fun possibleConversions(term: Term): Sequence<Any?> {
        return admissibleTypes(term).asSequence().map { convertInto(it, term) }
    }

    override fun mostAdequateType(term: Term): KClass<*> {
        return when (term) {
            is NullRef, is Var -> Nothing::class
            is ObjectRef -> term.`object`::class
            is Truth -> Boolean::class
            is Atom -> String::class
            is Real -> BigDecimal::class
            is Integer -> BigInteger::class
            is Struct -> when {
                term matches CAST_TEMPLATE -> getType(term, term[1])
                else -> throw TermToObjectConversionException(term)
            }
            else -> throw TermToObjectConversionException(term)
        }
    }

    override fun admissibleTypes(term: Term): Set<KClass<*>> {
        return when (term) {
            is NullRef, is Var -> setOf(Nothing::class)
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
            is Integer -> mutableSetOf<KClass<*>>(BigInteger::class, BigDecimal::class).also {
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
                if (term.decimalValue in BigDecimal.of(Double.MIN_VALUE)..BigDecimal.of(Double.MAX_VALUE)) {
                    it += Double::class
                }
                if (term.decimalValue in BigDecimal.of(Float.MIN_VALUE)..BigDecimal.of(Float.MAX_VALUE)) {
                    it += Float::class
                }
            }
            is Struct -> when {
                term matches CAST_TEMPLATE -> setOf(getType(term, term[1]))
                else -> throw TermToObjectConversionException(term)
            }
            else -> throw TermToObjectConversionException(term)
        }
    }
}
