package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import kotlin.js.JsName
import it.unibo.tuprolog.core.List as LogicList

/**
 * Core term/clause-building mixin of the Prolog DSL: overloads [it.unibo.tuprolog.core.Scope]'s own
 * `structOf`/`tupleOf`/`logicListOf`/... factory methods to accept plain `Any` arguments (auto-[toTerm]-ed),
 * adds the "calling a `String`" shorthand for building [Struct]s, and adds scoped clause builders
 * ([rule]/[fact]/[directive]/[clause]) that isolate each clause's variables in their own [newScope].
 *
 * Calling a `String` with one or more arguments builds a [Struct] with that string as functor, whereas calling a
 * [Var] builds a [Pattern], i.e. a compound term whose functor is that variable:
 * ```kotlin
 * logicProgramming {
 *     "parent"("abraham", "isaac") // Struct: parent(abraham, isaac)
 *     "ancestor"("X", "Y")         // "X"/"Y" are termified to Vars, since they match a variable name pattern
 *     varOf("P")("X", "Y")         // Pattern: P(X, Y)
 * }
 * ```
 */
interface MinimalLogicProgrammingScope<S : MinimalLogicProgrammingScope<S>> : BaseLogicProgrammingScope<S> {
    /** Builds a [Struct] whose functor is this [String] and whose arguments are [term] followed by [terms]. */
    @JsName("stringInvoke")
    operator fun String.invoke(
        term: Any,
        vararg terms: Any,
    ): Struct = structOf(this, sequenceOf(term, *terms).map { it.toTerm() })

    /** Builds a [Pattern] whose functor is this [Var] and whose arguments are [term] followed by [terms]. */
    @JsName("varInvoke")
    operator fun Var.invoke(
        term: Any,
        vararg terms: Any,
    ): Pattern = patternOf(this, sequenceOf(term, *terms).map { it.toTerm() })

    /**
     * Overload of [it.unibo.tuprolog.core.Scope.patternOf] accepting plain values, [toTerm]-ing [functor] and each of
     * [args].
     * @throws ClassCastException if [functor] does not [toTerm] into a [Var].
     */
    @JsName("patternOfAny")
    fun patternOf(
        functor: Any,
        vararg args: Any,
    ): Pattern = patternOf(functor.toTerm().castToVar(), args.map { it.toTerm() })

    /** Overload of [it.unibo.tuprolog.core.Scope.structOf] accepting plain values, [toTerm]-ing each of [args]. */
    @JsName("structOfAny")
    fun structOf(
        functor: String,
        vararg args: Any,
    ): Struct = structOf(functor, *args.map { it.toTerm() }.toTypedArray())

    /** Overload of [it.unibo.tuprolog.core.Scope.tupleOf] accepting plain values, [toTerm]-ing each of [terms]. */
    @JsName("tupleOfAny")
    fun tupleOf(vararg terms: Any): Tuple = tupleOf(*terms.map { it.toTerm() }.toTypedArray())

    /** Overload of [it.unibo.tuprolog.core.Scope.logicListOf] accepting plain values, [toTerm]-ing each of [terms]. */
    @JsName("logicListOfAny")
    fun logicListOf(vararg terms: Any): LogicList = this.logicListOf(*terms.map { it.toTerm() }.toTypedArray())

    /** Overload of [it.unibo.tuprolog.core.Scope.blockOf] accepting plain values, [toTerm]-ing each of [terms]. */
    @JsName("blockOfAny")
    fun blockOf(vararg terms: Any): Block = this.blockOf(*terms.map { it.toTerm() }.toTypedArray())

    /**
     * Overload of [it.unibo.tuprolog.core.Scope.factOf] accepting a plain value, [toTerm]-ing [term].
     * @throws ClassCastException if [term] does not [toTerm] into a [Compound].
     */
    @JsName("factOfAny")
    fun factOf(term: Any): Fact = factOf(term.toTerm().castToCompound())

    /** Overload of [it.unibo.tuprolog.core.Scope.consOf] accepting plain values, [toTerm]-ing both [head] and [tail]. */
    @JsName("consOfAny")
    fun consOf(
        head: Any,
        tail: Any,
    ): Cons = consOf(head.toTerm(), tail.toTerm())

    /** Overload of [it.unibo.tuprolog.core.Scope.goalOf] accepting plain values, [toTerm]-ing [term] and each of [terms]. */
    @JsName("goalOfAny")
    fun goalOf(
        term: Any,
        vararg terms: Any,
    ): Goal = goalOf(term.toTerm(), *terms.map { it.toTerm() }.toTypedArray())

    /** Runs [function] with a [newScope] as its receiver, isolating any [it.unibo.tuprolog.core.Var] it creates. */
    @JsName("withScope")
    fun <R> scope(function: S.() -> R): R = newScope().function()

    /**
     * Builds a logic list out of [items] (each [toTerm]-ed), optionally ending in [tail] (also [toTerm]-ed)
     * rather than the empty list — mirroring [it.unibo.tuprolog.core.Scope.logicListFrom]'s `last` parameter.
     */
    @JsName("logicList")
    fun logicList(
        vararg items: Any,
        tail: Any? = null,
    ): LogicList =
        listOf(*items).map { it.toTerm() }.let {
            if (tail != null) {
                logicListFrom(it, last = tail.toTerm())
            } else {
                logicListOf(it)
            }
        }

    /**
     * Runs [function] with a [newScope] as its receiver and coerces its result to a [Rule] — typically built via
     * the `head `if` body` / `head `impliedBy` body` infix builders from [LogicProgrammingScopeWithOperators].
     *
     * @throws ClassCastException if [function]'s result does not [toTerm] into a [Rule].
     */
    @JsName("rule")
    fun rule(function: S.() -> Any): Rule = newScope().function().toTerm() as Rule

    /**
     * Runs [function] with a [newScope] as its receiver and coerces its result to a [Clause]: returned as-is if
     * it already is a [Clause] (e.g. a [Rule]), or wrapped into a [Fact] via [factOf] if it is a plain [Compound].
     *
     * @throws IllegalArgumentException if [function]'s result is neither a [Clause] nor a [Compound].
     */
    fun clause(function: S.() -> Any): Clause =
        newScope().function().toSpecificSubTypeOfTerm(Clause::class, this::factOf)

    /**
     * Runs [function] with a [newScope] as its receiver and coerces its result to a [Goal] via [goalOf].
     *
     * @throws IllegalArgumentException if [function]'s result is neither a [Goal] nor a [Struct].
     */
    @JsName("directive")
    fun directive(function: S.() -> Any): Goal =
        newScope().function().toSpecificSubTypeOfTerm(Goal::class, this::goalOf)

    /**
     * Runs [function] with a [newScope] as its receiver and coerces its result to a [Fact] via [factOf].
     *
     * @throws IllegalArgumentException if [function]'s result is neither a [Fact] nor a [Struct].
     */
    @JsName("fact")
    fun fact(function: S.() -> Any): Fact = newScope().function().toSpecificSubTypeOfTerm(Fact::class, this::factOf)
}
