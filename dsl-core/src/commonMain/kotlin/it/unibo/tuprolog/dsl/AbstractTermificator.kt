package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermConvertible
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.assertItemsAreNotNull
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.reflect.KClass
import kotlin.collections.List as KtList

@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractTermificator(override val scope: Scope) : Termificator {
    private val converters: MutableMap<KClass<*>, (Any) -> Term> = linkedMapOf()

    fun <T : Any> handleType(
        type: KClass<T>,
        conversion: (T) -> Term,
    ) {
        converters[type] = {
            @Suppress("UNCHECKED_CAST")
            conversion(it as T)
        }
    }

    override fun termify(value: Any?): Term {
        requireNotNull(value) { "Cannot termify null" }
        if (value is Term) {
            return value
        }
        if (value is TermConvertible) {
            return value.toTerm()
        }
        val type = value::class
        val exactConverter = converters[type]
        if (exactConverter != null) {
            return exactConverter(value)
        }
        for ((superType, converter) in converters) {
            if (superType.isInstance(value)) {
                return converter(value)
            }
        }
        value.raiseErrorConvertingTo(Term::class)
    }

    fun handleBooleanAsTruth(value: Boolean) = scope.truthOf(value)

    fun handleStringAsAtomOrVariable(value: String) =
        if (value matches Var.NAME_PATTERN) {
            scope.varOf(value)
        } else {
            scope.atomOf(value)
        }

    abstract fun handleNumberAsNumeric(value: Number): Term

    fun handleBigIntegerAsInteger(value: BigInteger) = scope.intOf(value)

    fun handleBigDecimalAsReal(value: BigDecimal) = scope.realOf(value)

    fun handleArrayAsList(value: Array<*>) =
        scope.listOf(value.asIterable().assertItemsAreNotNull().map { termify(it) })

    fun handleSequenceAsList(value: Sequence<*>) = scope.listOf(value.assertItemsAreNotNull().map { termify(it) })

    fun handleIterableAsList(value: Iterable<*>) = scope.listOf(value.assertItemsAreNotNull().map { termify(it) })

    fun handleKotlinListAsLogicList(value: KtList<*>) = scope.listOf(value.assertItemsAreNotNull().map { termify(it) })

    fun handleSetAsBlock(value: Set<*>) = scope.blockOf(value.assertItemsAreNotNull().map { termify(it) })

    fun handlePairAsTuple(value: Pair<*, *>) = scope.tupleOf(termify(value.first), termify(value.second))

    fun handleTripletAsTuple(value: Triple<*, *, *>) =
        scope.tupleOf(termify(value.first), termify(value.second), termify(value.third))

    fun handlePairValuePairAsStruct(value: Pair<*, *>) =
        scope.structOf(":", termify(value.first), termify(value.second))

    fun handleKeyValuePairAsStruct(value: Map.Entry<*, *>) =
        scope.structOf(":", termify(value.key), termify(value.value))

    fun handleMapAsBlock(value: Map<*, *>) =
        scope.blockOf(value.entries.assertItemsAreNotNull().map { handleKeyValuePairAsStruct(it) })

    fun legacyConfiguration() {
        handleType(Boolean::class, ::handleBooleanAsTruth)
        handleType(String::class, ::handleStringAsAtomOrVariable)
        handleType(BigInteger::class, ::handleBigIntegerAsInteger)
        handleType(BigDecimal::class, ::handleBigDecimalAsReal)
        handleType(Number::class, ::handleNumberAsNumeric)
        handleType(Array::class, ::handleArrayAsList)
        handleType(Sequence::class, ::handleSequenceAsList)
        handleType(Iterable::class, ::handleIterableAsList)
    }

    fun novelConfiguration() {
        handleType(Boolean::class, ::handleBooleanAsTruth)
        handleType(String::class, ::handleStringAsAtomOrVariable)
        handleType(BigInteger::class, ::handleBigIntegerAsInteger)
        handleType(BigDecimal::class, ::handleBigDecimalAsReal)
        handleType(Number::class, ::handleNumberAsNumeric)
        handleType(Array::class, ::handleArrayAsList)
        handleType(Sequence::class, ::handleSequenceAsList)
        handleType(KtList::class, ::handleKotlinListAsLogicList)
        handleType(Set::class, ::handleSetAsBlock)
        handleType(Pair::class, ::handlePairAsTuple)
        handleType(Triple::class, ::handleTripletAsTuple)
        handleType(Map.Entry::class, ::handleKeyValuePairAsStruct)
        handleType(Map::class, ::handleMapAsBlock)
        handleType(Iterable::class, ::handleIterableAsList)
    }
}
