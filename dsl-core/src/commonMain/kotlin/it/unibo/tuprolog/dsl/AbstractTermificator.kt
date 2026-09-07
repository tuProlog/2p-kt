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

/**
 * Skeletal implementation of [Termificator], providing the type-dispatch machinery ([termify]) and a library of
 * `protected` `handleXxxAsYyy` conversion functions that subclasses register (via [handleType]) to implement
 * [Termificator.default]/[Termificator.legacy] and any other custom conversion policy.
 *
 * Conversions are looked up in [converters], a registration-ordered map from [KClass] to converter function: an
 * exact match on `value::class` is tried first, then the map is scanned in registration order for the first
 * registered type [value] is an instance of — so more specific types should be [handleType]-registered before
 * more general ones (see [legacyConfiguration]/[novelConfiguration] for the order this module itself relies on).
 *
 * A subclass only needs to implement [handleNumberAsNumeric] (the concrete `Number`-to-[Term] policy differs
 * between platforms, e.g. to leverage native `BigInteger`/`BigDecimal` types) and call [defaultConfiguration] —
 * or [handleType] directly — from its constructor to populate [converters].
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractTermificator(
    override val scope: Scope,
) : Termificator {
    /** The type-dispatch table backing [termify], from a registered [KClass] to the [Term]-converter for it. */
    protected val converters: MutableMap<KClass<*>, (Any) -> Term> = linkedMapOf()

    /** Registers [conversion] as the converter to use, in [termify], for values whose type is (or extends) [type]. */
    protected fun <T : Any> handleType(
        type: KClass<T>,
        conversion: (T) -> Term,
    ) {
        converters[type] = {
            @Suppress("UNCHECKED_CAST")
            conversion(it as T)
        }
    }

    /**
     * Converts [value] to a [Term]: returns it unchanged if it already is one, delegates to
     * [TermConvertible.toTerm] if it is a [TermConvertible], otherwise dispatches to the [converters] registered
     * via [handleType] (exact runtime type first, then the first registered supertype [value] is an instance of).
     *
     * @throws IllegalArgumentException if [value] is `null`, or no registered converter applies to it.
     */
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

    /** Converts [value] into [it.unibo.tuprolog.core.Truth.TRUE] or [it.unibo.tuprolog.core.Truth.FALSE]. */
    protected fun handleBooleanAsTruth(value: Boolean) = scope.truthOf(value)

    /**
     * Converts [value] by turning it into a single-character [String] and re-[termify]-ing it — typically
     * yielding an [it.unibo.tuprolog.core.Atom] (or a [it.unibo.tuprolog.core.Var], if [value] is a single
     * upper-case letter or `_`; see [handleStringAsAtomOrVariable]).
     */
    protected fun handleCharAsString(value: Char) = termify(value.toString())

    /**
     * Converts [value] into a [it.unibo.tuprolog.core.Var] if it looks like a legal Prolog variable name (see
     * [Var.NAME_PATTERN]), or into an [it.unibo.tuprolog.core.Atom] otherwise.
     */
    protected fun handleStringAsAtomOrVariable(value: String) =
        if (value matches Var.NAME_PATTERN) {
            scope.varOf(value)
        } else {
            scope.atomOf(value)
        }

    /** Converts [value] into a [it.unibo.tuprolog.core.Numeric], per the host platform's own numeric types. */
    protected abstract fun handleNumberAsNumeric(value: Number): Term

    /** Converts [value] into an [it.unibo.tuprolog.core.Integer]. */
    protected fun handleBigIntegerAsInteger(value: BigInteger) = scope.intOf(value)

    /** Converts [value] into a [it.unibo.tuprolog.core.Real]. */
    protected fun handleBigDecimalAsReal(value: BigDecimal) = scope.realOf(value)

    /**
     * Converts [value] into a logic list, [termify]-ing each item.
     * @throws IllegalArgumentException if [value] contains a `null` item.
     */
    protected fun handleArrayAsList(value: Array<*>) =
        scope.logicListOf(value.asIterable().assertItemsAreNotNull().map { termify(it) })

    /**
     * Converts [value] into a logic list, [termify]-ing each item.
     * @throws IllegalArgumentException if [value] contains a `null` item.
     */
    protected fun handleSequenceAsList(value: Sequence<*>) =
        scope.logicListOf(
            value.assertItemsAreNotNull().map { termify(it) },
        )

    /**
     * Converts [value] into a logic list, [termify]-ing each item.
     * @throws IllegalArgumentException if [value] contains a `null` item.
     */
    protected fun handleIterableAsList(value: Iterable<*>) =
        scope.logicListOf(
            value.assertItemsAreNotNull().map { termify(it) },
        )

    /**
     * Converts [value] into a logic list, [termify]-ing each item.
     * @throws IllegalArgumentException if [value] contains a `null` item.
     */
    protected fun handleKotlinListAsLogicList(value: KtList<*>) =
        scope.logicListOf(
            value.assertItemsAreNotNull().map {
                termify(it)
            },
        )

    /**
     * Converts [value] into a [it.unibo.tuprolog.core.Block], [termify]-ing each item.
     * @throws IllegalArgumentException if [value] contains a `null` item.
     */
    protected fun handleSetAsBlock(value: Set<*>) = scope.blockOf(value.assertItemsAreNotNull().map { termify(it) })

    /** Converts [value] into a 2-ary [it.unibo.tuprolog.core.Tuple], [termify]-ing both components. */
    protected fun handlePairAsTuple(value: Pair<*, *>) = scope.tupleOf(termify(value.first), termify(value.second))

    /** Converts [value] into a 3-ary [it.unibo.tuprolog.core.Tuple], [termify]-ing all three components. */
    protected fun handleTripletAsTuple(value: Triple<*, *, *>) =
        scope.tupleOf(termify(value.first), termify(value.second), termify(value.third))

    /** Converts [value] into a `:`/2 [it.unibo.tuprolog.core.Struct], [termify]-ing both components. */
    @Suppress("unused")
    protected fun handlePairValuePairAsStruct(value: Pair<*, *>) =
        scope.structOf(":", termify(value.first), termify(value.second))

    /** Converts [value] into a `:`/2 [it.unibo.tuprolog.core.Struct], [termify]-ing key and value. */
    protected fun handleKeyValuePairAsStruct(value: Map.Entry<*, *>) =
        scope.structOf(":", termify(value.key), termify(value.value))

    /**
     * Converts [value] into a [it.unibo.tuprolog.core.Block] of `key:value` structs, one per entry
     * (see [handleKeyValuePairAsStruct]).
     * @throws IllegalArgumentException if any of [value]'s entries is `null`.
     */
    protected fun handleMapAsBlock(value: Map<*, *>) =
        scope.blockOf(value.entries.assertItemsAreNotNull().map { handleKeyValuePairAsStruct(it) })

    /**
     * Registers the conversions backing [Termificator.legacy]: [Char], [Boolean], [String], [BigInteger],
     * [BigDecimal], [Number], `Array`, `Sequence` and (any other) `Iterable`.
     */
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

    /**
     * Registers the conversions backing [Termificator.default]: everything [legacyConfiguration] registers, plus
     * dedicated handling for Kotlin `List`s, `Set`s, `Pair`s, `Triple`s, `Map.Entry`s and `Map`s (registered
     * before the catch-all `Iterable` conversion, so they take precedence over it).
     */
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

    /** Calls [novelConfiguration] if [novel], [legacyConfiguration] otherwise. Meant to be called from constructors. */
    protected fun defaultConfiguration(novel: Boolean = false) {
        if (novel) {
            novelConfiguration()
        } else {
            legacyConfiguration()
        }
    }
}
