package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.reflect.KClass

/**
 * Root mixin of the Prolog DSL scope hierarchy: extends [Scope] (so every term-building factory method it exposes
 * is directly available) with a [termificator] and the [toTerm] extension it powers, letting every other mixin in
 * the hierarchy (see [MinimalLogicProgrammingScope], [LogicProgrammingScopeWithOperators], ...) accept a plain
 * `Any` wherever a [Term] is expected.
 *
 * [S] is the concrete, self-referential scope type (an F-bounded type parameter) that [newScope] returns — each
 * mixin in the hierarchy is generic over it so that chaining calls (e.g. `newScope().someBuilder { ... }`) keeps
 * resolving to the richest scope type available, rather than widening to this bare mixin.
 */
interface BaseLogicProgrammingScope<S : BaseLogicProgrammingScope<S>> : Scope {
    /** The [Termificator] used by [toTerm] to convert arbitrary values into [Term]s within this scope. */
    @JsName("termificator")
    val termificator: Termificator

    /** Converts this value into a [Term], via [termificator]. See [Termificator.termify] for the conversion rules. */
    @JsName("convertToTerm")
    fun Any.toTerm(): Term = termificator.termify(this)

    /**
     * Converts this value into a [Term] (via [toTerm]) and coerces it to [type]: returned as-is if it already is
     * a [type], passed through [converter] if it is a [Struct] (e.g. to turn a plain `Struct` into a `Fact`), or
     * rejected otherwise.
     *
     * @throws IllegalArgumentException if the converted [Term] is neither a [type] nor a [Struct].
     */
    @Suppress("UNCHECKED_CAST")
    @JsName("convertToSpecificSubTypeOfTerm")
    fun <T : Term> Any.toSpecificSubTypeOfTerm(
        type: KClass<T>,
        converter: (Struct) -> T,
    ): T {
        val t = toTerm()
        return when {
            type.isInstance(t) -> t as T
            t is Struct -> converter(t)
            else -> raiseErrorConvertingTo(type)
        }
    }

    /**
     * Creates a fresh scope of type [S], backed by a brand-new, empty [Scope] — so that [it.unibo.tuprolog.core.Var]s
     * created within it are unrelated to the ones created through this scope. Used by builders such as
     * [MinimalLogicProgrammingScope.rule]/[MinimalLogicProgrammingScope.fact] to isolate each clause's variables.
     */
    @JsName("newScope")
    fun newScope(): S
}
