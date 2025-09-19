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
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.TermToObjectConverter
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import it.unibo.tuprolog.solve.libs.oop.isPrimitiveType
import it.unibo.tuprolog.solve.libs.oop.isSubtypeOf
import it.unibo.tuprolog.solve.libs.oop.primitives.CAST_TEMPLATE
import it.unibo.tuprolog.solve.libs.oop.primitives.DEALIASING_TEMPLATE
import it.unibo.tuprolog.solve.libs.oop.subTypeDistance
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.utils.indexed
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.reflect.KClass

internal class TermToObjectConverterImpl(
    private val typeFactory: TypeFactory,
    private val dealiaser: (Struct) -> Ref?,
) : TermToObjectConverter {
    override fun convertInto(
        type: KClass<*>,
        term: Term,
    ): Any? =
        when (term) {
            is NullRef, is Var -> {
                if (type.isPrimitiveType) {
                    throw TermToObjectConversionException(term, type)
                } else {
                    null
                }
            }
            is ObjectRef -> {
                if (term.`object`::class isSubtypeOf type) {
                    term.`object`
                } else {
                    throw TermToObjectConversionException(term, type)
                }
            }
            is Truth ->
                when {
                    Boolean::class isSubtypeOf type -> term.isTrue
                    String::class isSubtypeOf type -> term.value
                    else -> throw TermToObjectConversionException(term, type)
                }
            is Atom ->
                when {
                    String::class isSubtypeOf type -> term.value
                    Char::class isSubtypeOf type -> {
                        if (term.value.length == 1) {
                            term.value[0]
                        } else {
                            throw TermToObjectConversionException(term, type)
                        }
                    }
                    else -> throw TermToObjectConversionException(term, type)
                }
            is Real ->
                when {
                    BigDecimal::class isSubtypeOf type -> term.decimalValue
                    Double::class isSubtypeOf type -> term.decimalValue.toDouble()
                    Float::class isSubtypeOf type -> term.decimalValue.toFloat()
                    else -> throw TermToObjectConversionException(term, type)
                }
            is Integer ->
                when {
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
                    term matches DEALIASING_TEMPLATE ->
                        when (val ref = dealiaser(term)) {
                            is ObjectRef -> convertInto(type, ref)
                            else -> throw TermToObjectConversionException(term)
                        }
                    else -> throw TermToObjectConversionException(term)
                }.also {
                    if (it != null && !(it::class isSubtypeOf type)) {
                        throw TermToObjectConversionException(term)
                    }
                }
            else -> throw TermToObjectConversionException(term)
        }

    private fun explicitConversion(
        castExpression: Struct,
        term: Term,
        type: Term,
    ): Any? {
        val targetType = getType(castExpression, type)
        try {
            return convertInto(targetType, term)
        } catch (e: TermToObjectConversionException) {
            throw e
        } catch (_: Exception) {
            throw TermToObjectConversionException(term, targetType)
        }
    }

    private fun getType(
        castExpression: Struct,
        typeTerm: Term,
    ): KClass<*> =
        when (typeTerm) {
            is TypeRef -> typeTerm.type
            is Atom -> {
                typeFactory.typeFromName(typeTerm.value) ?: throw TermToObjectConversionException(castExpression)
            }
            is Struct ->
                when {
                    typeTerm matches DEALIASING_TEMPLATE ->
                        when (val ref = dealiaser(typeTerm)) {
                            is TypeRef -> ref.type
                            else -> throw TermToObjectConversionException(castExpression)
                        }
                    else -> throw TermToObjectConversionException(castExpression)
                }
            else -> throw TermToObjectConversionException(castExpression)
        }

    override fun possibleConversions(term: Term): Sequence<Any?> =
        admissibleTypes(term).asSequence().map {
            convertInto(it, term)
        }

    override fun mostAdequateType(term: Term): KClass<*> = admissibleTypesByPriority(term).first()

    override fun priorityOfConversion(
        type: KClass<*>,
        term: Term,
    ): Int? =
        admissibleTypes(term)
            .asSequence()
            .map { type.subTypeDistance(it) }
            .indexed()
            .map { (index, dist) -> dist?.let { (index + 1) * (it + 1) } }
            .filterNotNull()
            .minByOrNull { it }

    private fun admissibleTypesByPriority(term: Term): Sequence<KClass<*>> =
        when (term) {
            is NullRef, is Var -> sequenceOf(Nothing::class)
            is ObjectRef -> sequenceOf(term.`object`::class)
            is Truth -> sequenceOf(Boolean::class, String::class)
            is Atom -> {
                var admissible = sequenceOf<KClass<*>>(String::class)
                if (term.value.length == 1) {
                    admissible += sequenceOf(Char::class)
                }
                admissible
            }
            is Real -> sequenceOf(BigDecimal::class, Double::class, Float::class)
            is Integer -> {
                var admissible = sequenceOf<KClass<*>>(BigInteger::class)
                if (term.intValue in BigInteger.of(Long.MIN_VALUE)..BigInteger.of(Long.MAX_VALUE)) {
                    admissible += sequenceOf(Long::class)
                }
                if (term.intValue in BigInteger.of(Int.MIN_VALUE)..BigInteger.of(Int.MAX_VALUE)) {
                    admissible += sequenceOf(Int::class)
                }
                if (term.intValue in BigInteger.of(Short.MIN_VALUE.toInt())..BigInteger.of(Short.MAX_VALUE.toInt())) {
                    admissible += sequenceOf(Short::class)
                }
                if (term.intValue in BigInteger.of(Byte.MIN_VALUE.toInt())..BigInteger.of(Byte.MAX_VALUE.toInt())) {
                    admissible += sequenceOf(Byte::class)
                }
                admissible += sequenceOf(BigDecimal::class)
                if (term.decimalValue in BigDecimal.of(Double.MIN_VALUE)..BigDecimal.of(Double.MAX_VALUE)) {
                    admissible += sequenceOf(Double::class)
                }
                if (term.decimalValue in BigDecimal.of(Float.MIN_VALUE)..BigDecimal.of(Float.MAX_VALUE)) {
                    admissible += sequenceOf(Float::class)
                }
                admissible
            }
            is Struct ->
                when {
                    term matches CAST_TEMPLATE -> sequenceOf(getType(term, term[1]))
                    term matches DEALIASING_TEMPLATE ->
                        when (val ref = dealiaser(term)) {
                            is ObjectRef -> admissibleTypesByPriority(ref)
                            else -> throw TermToObjectConversionException(term)
                        }
                    else -> throw TermToObjectConversionException(term)
                }
            else -> throw TermToObjectConversionException(term)
        }

    override fun admissibleTypes(term: Term): Set<KClass<*>> = admissibleTypesByPriority(term).toSet()
}
