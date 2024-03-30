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
abstract class AbstractTermificator(
    override val scope: Scope,
) : Termificator {
    protected val converters: MutableMap<KClass<*>, (Any) -> Term> = linkedMapOf()

    protected fun <T : Any> handleType(
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

    protected fun handleBooleanAsTruth(value: Boolean) = scope.truthOf(value)

    protected fun handleCharAsString(value: Char) = termify(value.toString())

    protected fun handleStringAsAtomOrVariable(value: String) =
        if (value matches Var.NAME_PATTERN) {
            scope.varOf(value)
        } else {
            scope.atomOf(value)
        }

    protected abstract fun handleNumberAsNumeric(value: Number): Term

    protected fun handleBigIntegerAsInteger(value: BigInteger) = scope.intOf(value)

    protected fun handleBigDecimalAsReal(value: BigDecimal) = scope.realOf(value)

    protected fun handleArrayAsList(value: Array<*>) =
        scope.logicListOf(value.asIterable().assertItemsAreNotNull().map { termify(it) })

    protected fun handleSequenceAsList(value: Sequence<*>) =
        scope.logicListOf(
            value.assertItemsAreNotNull().map { termify(it) },
        )

    protected fun handleIterableAsList(value: Iterable<*>) =
        scope.logicListOf(
            value.assertItemsAreNotNull().map { termify(it) },
        )

    protected fun handleKotlinListAsLogicList(value: KtList<*>) =
        scope.logicListOf(
            value.assertItemsAreNotNull().map {
                termify(it)
            },
        )

    protected fun handleSetAsBlock(value: Set<*>) = scope.blockOf(value.assertItemsAreNotNull().map { termify(it) })

    protected fun handlePairAsTuple(value: Pair<*, *>) = scope.tupleOf(termify(value.first), termify(value.second))

    protected fun handleTripletAsTuple(value: Triple<*, *, *>) =
        scope.tupleOf(termify(value.first), termify(value.second), termify(value.third))

    @Suppress("unused")
    protected fun handlePairValuePairAsStruct(value: Pair<*, *>) =
        scope.structOf(":", termify(value.first), termify(value.second))

    protected fun handleKeyValuePairAsStruct(value: Map.Entry<*, *>) =
        scope.structOf(":", termify(value.key), termify(value.value))

    protected fun handleMapAsBlock(value: Map<*, *>) =
        scope.blockOf(value.entries.assertItemsAreNotNull().map { handleKeyValuePairAsStruct(it) })

    protected fun legacyConfiguration() {
        handleType(Char::class, ::handleCharAsString)
        handleType(Boolean::class, ::handleBooleanAsTruth)
        handleType(String::class, ::handleStringAsAtomOrVariable)
        handleType(BigInteger::class, ::handleBigIntegerAsInteger)
        handleType(BigDecimal::class, ::handleBigDecimalAsReal)
        handleType(Number::class, ::handleNumberAsNumeric)
        handleType(Array::class, ::handleArrayAsList)
        handleType(Sequence::class, ::handleSequenceAsList)
        handleType(Iterable::class, ::handleIterableAsList)
    }

    protected fun novelConfiguration() {
        handleType(Char::class, ::handleCharAsString)
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

    protected fun defaultConfiguration(novel: Boolean = false) {
        if (novel) {
            novelConfiguration()
        } else {
            legacyConfiguration()
        }
    }
}
