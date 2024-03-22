package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermConvertible
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.assertItemsAreNotNull
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.collections.List as KtList
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractTermificator(protected val scope: Scope) : Termificator {
    private val converters: MutableMap<KClass<*>, (Any) -> Term> = linkedMapOf()

    fun <T : Any> handleType(type: KClass<T>, conversion: (T) -> Term) {
        converters[type] = {
            @Suppress("UNCHECKED_CAST")
            conversion(it as T)
        }
    }

    override fun toTerm(value: Any): Term {
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
        scope.listOf(value.asIterable().assertItemsAreNotNull().map { toTerm(it) })

    fun handleSequenceAsList(value: Sequence<*>) =
        scope.listOf(value.assertItemsAreNotNull().map { toTerm(it) })

    fun handleIterableAsList(value: Iterable<*>) =
        scope.listOf(value.assertItemsAreNotNull().map { toTerm(it) })

    fun handleKotlinListAsLogicList(value: KtList<*>) =
        scope.listOf(value.assertItemsAreNotNull().map { toTerm(it) })

    fun handleSetAsBlock(value: Set<*>) = scope.blockOf(value.assertItemsAreNotNull().map { toTerm(it) })

    fun handlePairAsTuple(value: Pair<*, *>) = scope.tupleOf(toTerm(value.first!!), toTerm(value.second!!))

    fun handleTripletAsTuple(value: Triple<*, *, *>) =
        scope.tupleOf(toTerm(value.first!!), toTerm(value.second!!), toTerm(value.third!!))

    fun handlePairValuePairAsStruct(value: Pair<*, *>) =
        scope.structOf(":", toTerm(value.first!!), toTerm(value.second!!))

    fun handleKeyValuePairAsStruct(value: Map.Entry<*, *>) =
        scope.structOf(":", toTerm(value.key!!), toTerm(value.value!!))

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

    fun defaultConfiguration() {
        handleType(Boolean::class, ::handleBooleanAsTruth)
        handleType(String::class, ::handleStringAsAtomOrVariable)
        handleType(BigInteger::class, ::handleBigIntegerAsInteger)
        handleType(BigDecimal::class, ::handleBigDecimalAsReal)
        handleType(Number::class, ::handleNumberAsNumeric)
        handleType(Array::class, ::handleArrayAsList)
        handleType(Sequence::class, ::handleSequenceAsList)
        handleType(Iterable::class, ::handleIterableAsList)
        handleType(KtList::class, ::handleKotlinListAsLogicList)
        handleType(Set::class, ::handleSetAsBlock)
        handleType(Pair::class, ::handlePairAsTuple)
        handleType(Triple::class, ::handleTripletAsTuple)
        handleType(Map.Entry::class, ::handleKeyValuePairAsStruct)
        handleType(Pair::class, ::handlePairValuePairAsStruct)
        handleType(Map::class, ::handleMapAsBlock)
    }
}
