package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.solve.libs.oop.fullName
import java.util.TreeMap
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

internal object KotlinTypesHacks {
    private data class CallableId(
        val type: KClass<*>,
        val name: String,
    ) : Comparable<CallableId> {
        private val stringRepresentation = "${type.fullName}::$name"

        override fun compareTo(other: CallableId): Int = stringRepresentation.compareTo(other.stringRepresentation)

        override fun toString(): String = stringRepresentation
    }

    private val typeCallableMap =
        TreeMap<CallableId, KCallable<*>>().also {
            it[CallableId(Int.Companion::class, "MAX_VALUE")] = Int.Companion::MAX_VALUE
            it[CallableId(Int.Companion::class, "MIN_VALUE")] = Int.Companion::MIN_VALUE
            it[CallableId(Int.Companion::class, "SIZE_BITS")] = Int.Companion::SIZE_BITS
            it[CallableId(Int.Companion::class, "SIZE_BYTES")] = Int.Companion::SIZE_BYTES

            it[CallableId(Short.Companion::class, "MAX_VALUE")] = Short.Companion::MAX_VALUE
            it[CallableId(Short.Companion::class, "MIN_VALUE")] = Short.Companion::MIN_VALUE
            it[CallableId(Short.Companion::class, "SIZE_BITS")] = Short.Companion::SIZE_BITS
            it[CallableId(Short.Companion::class, "SIZE_BYTES")] = Short.Companion::SIZE_BYTES

            it[CallableId(Long.Companion::class, "MAX_VALUE")] = Long.Companion::MAX_VALUE
            it[CallableId(Long.Companion::class, "MIN_VALUE")] = Long.Companion::MIN_VALUE
            it[CallableId(Long.Companion::class, "SIZE_BITS")] = Long.Companion::SIZE_BITS
            it[CallableId(Long.Companion::class, "SIZE_BYTES")] = Long.Companion::SIZE_BYTES

            it[CallableId(Byte.Companion::class, "MAX_VALUE")] = Byte.Companion::MAX_VALUE
            it[CallableId(Byte.Companion::class, "MIN_VALUE")] = Byte.Companion::MIN_VALUE
            it[CallableId(Byte.Companion::class, "SIZE_BITS")] = Byte.Companion::SIZE_BITS
            it[CallableId(Byte.Companion::class, "SIZE_BYTES")] = Byte.Companion::SIZE_BYTES

            it[CallableId(Char.Companion::class, "MAX_VALUE")] = Char.Companion::MAX_VALUE
            it[CallableId(Char.Companion::class, "MIN_VALUE")] = Char.Companion::MIN_VALUE
            it[CallableId(Char.Companion::class, "SIZE_BITS")] = Char.Companion::SIZE_BITS
            it[CallableId(Char.Companion::class, "SIZE_BYTES")] = Char.Companion::SIZE_BYTES
            it[CallableId(Char.Companion::class, "MIN_HIGH_SURROGATE")] = Char.Companion::MIN_HIGH_SURROGATE
            it[CallableId(Char.Companion::class, "MIN_LOW_SURROGATE")] = Char.Companion::MIN_LOW_SURROGATE
            it[CallableId(Char.Companion::class, "MIN_SURROGATE")] = Char.Companion::MIN_SURROGATE
            it[CallableId(Char.Companion::class, "MAX_HIGH_SURROGATE")] = Char.Companion::MAX_HIGH_SURROGATE
            it[CallableId(Char.Companion::class, "MAX_LOW_SURROGATE")] = Char.Companion::MAX_LOW_SURROGATE
            it[CallableId(Char.Companion::class, "MAX_SURROGATE")] = Char.Companion::MAX_SURROGATE

            it[CallableId(Double.Companion::class, "MAX_VALUE")] = Double.Companion::MAX_VALUE
            it[CallableId(Double.Companion::class, "MIN_VALUE")] = Double.Companion::MIN_VALUE
            it[CallableId(Double.Companion::class, "SIZE_BITS")] = Double.Companion::SIZE_BITS
            it[CallableId(Double.Companion::class, "SIZE_BYTES")] = Double.Companion::SIZE_BYTES
            it[CallableId(Double.Companion::class, "NEGATIVE_INFINITY")] = Double.Companion::NEGATIVE_INFINITY
            it[CallableId(Double.Companion::class, "NaN")] = Double.Companion::NaN
            it[CallableId(Double.Companion::class, "POSITIVE_INFINITY")] = Double.Companion::POSITIVE_INFINITY

            it[CallableId(Float.Companion::class, "MAX_VALUE")] = Float.Companion::MAX_VALUE
            it[CallableId(Float.Companion::class, "MIN_VALUE")] = Float.Companion::MIN_VALUE
            it[CallableId(Float.Companion::class, "SIZE_BITS")] = Float.Companion::SIZE_BITS
            it[CallableId(Float.Companion::class, "SIZE_BYTES")] = Float.Companion::SIZE_BYTES
            it[CallableId(Float.Companion::class, "NEGATIVE_INFINITY")] = Float.Companion::NEGATIVE_INFINITY
            it[CallableId(Float.Companion::class, "NaN")] = Float.Companion::NaN
            it[CallableId(Float.Companion::class, "POSITIVE_INFINITY")] = Float.Companion::POSITIVE_INFINITY

            it[CallableId(String.Companion::class, "CASE_INSENSITIVE_ORDER")] = String.Companion::CASE_INSENSITIVE_ORDER
        }

    operator fun get(
        type: KClass<*>,
        name: String,
        args: List<Any?>,
    ): KCallable<*>? = if (args.isNotEmpty()) null else typeCallableMap[CallableId(type, name)]
}
