package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermConvertible
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Converts arbitrary Kotlin values into Prolog [Term]s, so that DSL builders (see [MinimalLogicProgrammingScope]
 * and friends) can accept a plain `Any` — a [String], a [Number], a [Boolean], a `List`, ... — wherever a [Term]
 * is expected, instead of forcing callers to spell out `atomOf`/`intOf`/`logicListOf` everywhere.
 *
 * Conversion is driven by the runtime type of the value being converted:
 * - a [Term] is returned as-is;
 * - a [TermConvertible] is converted via [TermConvertible.toTerm];
 * - any other type is looked up against a table of registered converters (exact type first, then the first
 *   registered supertype the value is an instance of) — see [Termificator.default]/[Termificator.legacy] for what
 *   is registered by default (numbers, strings, booleans, chars, collections, pairs, maps, ...).
 *
 * All [Term]s produced by a given [Termificator] are created through its [scope], so repeated conversions of the
 * same variable name (e.g. `"X"` appearing twice while building a clause) resolve to the same [it.unibo.tuprolog.core.Var].
 *
 * @throws IllegalArgumentException if [termify] is given `null`, or a value whose runtime type — and none of its
 * registered supertypes — has a registered conversion.
 */
interface Termificator {
    /** The [Scope] backing every [Term] this [Termificator] produces. */
    @JsName("scope")
    val scope: Scope

    /**
     * Converts [value] into a [Term], per this [Termificator]'s conversion rules (see the type-level doc).
     *
     * @throws IllegalArgumentException if [value] is `null`, or has no known conversion to [Term].
     */
    @JsName("toTerm")
    fun termify(value: Any?): Term

    /** Creates a copy of this [Termificator], backed by [scope] (defaulting to this one's own [scope]). */
    @JsName("copy")
    fun copy(scope: Scope = this.scope): Termificator

    companion object {
        /**
         * The default [Termificator], backed by [scope]. Handles [Char]s, [Boolean]s, [String]s (atoms, or
         * variables when they look like a Prolog variable name), [Number]s, `Array`s and `Sequence`s (as logic
         * lists) like [legacy] does, but additionally distinguishes Kotlin `List`s (as logic lists) from `Set`s
         * (as [it.unibo.tuprolog.core.Block]s), and also handles `Pair`s/`Triple`s (as
         * [it.unibo.tuprolog.core.Tuple]s) and `Map`s/`Map.Entry`s (as blocks of `key:value` structs) —
         * any other `Iterable` still falls back to a logic list.
         */
        @JvmStatic
        @JsName("default")
        fun default(scope: Scope): Termificator = DefaultTermificator(scope, true)

        /**
         * The legacy [Termificator], backed by [scope]: handles [Char]s, [Boolean]s, [String]s (atoms, or
         * variables when they look like a Prolog variable name), [Number]s, `Array`s, `Sequence`s and any other
         * `Iterable` (including `List`s and `Set`s) — all converted to logic lists. Unlike [default], it has no
         * special handling for `Pair`s, `Triple`s or `Map`s.
         */
        @JvmStatic
        @JsName("legacy")
        fun legacy(scope: Scope): Termificator = DefaultTermificator(scope, false)
    }
}
