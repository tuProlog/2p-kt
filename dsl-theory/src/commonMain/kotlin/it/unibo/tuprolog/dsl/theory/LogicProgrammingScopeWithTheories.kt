package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScopeWithUnificator
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryFactory
import kotlin.js.JsName

/**
 * Adds theory/clause-database-building sugar to [LogicProgrammingScopeWithUnificator]: implements [TheoryFactory]
 * itself (forwarding to [theoryFactory], e.g. via `TheoryFactory by theoryFactory` as [LogicProgrammingScopeImpl]
 * does) so a [it.unibo.tuprolog.theory.Theory]/[MutableTheory] can be built directly from this scope, and adds
 * [theory]/[mutableTheory] overloads accepting one DSL lambda per clause (rather than an already-built [Clause])
 * plus [theoryOf]/[mutableTheoryOf] overloads that concatenate several [Iterable]/[Sequence]s of clauses into one:
 * ```kotlin
 * logicProgramming {
 *     val db =
 *         theory(
 *             { factOf(structOf("parent", atomOf("tom"), atomOf("bob"))) },
 *             { ruleOf(structOf("grandparent", varOf("X"), varOf("Z")), structOf("parent", varOf("X"), varOf("Z"))) },
 *         )
 * }
 * ```
 * Each `S.() -> Any` lambda passed to [theory]/[mutableTheory] runs against a fresh [newScope] (exactly like
 * [it.unibo.tuprolog.dsl.MinimalLogicProgrammingScope.fact]/[it.unibo.tuprolog.dsl.MinimalLogicProgrammingScope.rule]
 * do), so variables of the same name in two different lambdas denote distinct, unrelated [it.unibo.tuprolog.core.Var]s
 * — as they would be in two separate clauses of a real Prolog theory.
 *
 * @param S the concrete, self-referential scope type (see [it.unibo.tuprolog.dsl.BaseLogicProgrammingScope]).
 */
interface LogicProgrammingScopeWithTheories<S : LogicProgrammingScopeWithTheories<S>> :
    LogicProgrammingScopeWithUnificator<S>,
    TheoryFactory {
    /** The [TheoryFactory] this scope's [TheoryFactory] implementation (and [theory]/[mutableTheory]) forward to. */
    @JsName("theoryFactory")
    val theoryFactory: TheoryFactory

    /**
     * Converts this value into a [Clause]: returned as-is if [it.unibo.tuprolog.dsl.BaseLogicProgrammingScope.toTerm]
     * already yields a [Clause] (e.g. a [it.unibo.tuprolog.core.Rule], [it.unibo.tuprolog.core.Fact] or
     * [it.unibo.tuprolog.core.Directive]), or wrapped into a headless-body [Clause] via
     * [it.unibo.tuprolog.core.Scope.clauseOf] if it is a plain [it.unibo.tuprolog.core.Struct] (mirroring how
     * [it.unibo.tuprolog.dsl.MinimalLogicProgrammingScope.fact] wraps a bare [it.unibo.tuprolog.core.Struct] via
     * [it.unibo.tuprolog.core.Scope.factOf]).
     * @throws IllegalArgumentException if this value is neither a [Clause] nor a [it.unibo.tuprolog.core.Struct]
     * once converted to a [it.unibo.tuprolog.core.Term].
     */
    @JsName("convertToClause")
    fun Any.toClause(): Clause = toSpecificSubTypeOfTerm(Clause::class, ::clauseOf)

    /**
     * Builds a [Theory] out of [clauseFunctions], one per clause: each lambda runs with a fresh [newScope] as its
     * receiver, and its result is coerced into a [Clause] via [toClause] — so it may return an already-built
     * [Clause] (from [it.unibo.tuprolog.core.Scope.factOf]/[it.unibo.tuprolog.core.Scope.ruleOf]/
     * [it.unibo.tuprolog.core.Scope.directiveOf]) or a plain [it.unibo.tuprolog.core.Struct], as shown in this
     * interface's example. Delegates to [TheoryFactory.theoryOf] (via [theoryFactory]) for the actual construction.
     * @throws IllegalArgumentException if any of [clauseFunctions] returns a value that [toClause] cannot convert.
     */
    @JsName("theory")
    fun theory(vararg clauseFunctions: S.() -> Any): Theory =
        theoryOf(clauseFunctions.map { newScope().it().toClause() })

    /** Overload of [TheoryFactory.theoryOf] concatenating [clauses] and [otherClauses] into a single [Theory]. */
    @JsName("theoryOfConcatenatedSequences")
    fun theoryOf(
        clauses: Sequence<Clause>,
        vararg otherClauses: Sequence<Clause>,
    ): Theory = theoryOf(clauses + otherClauses.flatMap { it })

    /** Overload of [TheoryFactory.theoryOf] concatenating [clauses] and [otherClauses] into a single [Theory]. */
    @JsName("theoryOfConcatenatedIterables")
    fun theoryOf(
        clauses: Iterable<Clause>,
        vararg otherClauses: Iterable<Clause>,
    ): Theory = theoryOf(kotlin.collections.listOf(clauses, *otherClauses).flatten())

    /**
     * Builds a [MutableTheory] out of [clauseFunctions], one per clause. Otherwise identical to [theory], down to
     * how each lambda is run and its result converted via [toClause]; delegates to [TheoryFactory.mutableTheoryOf]
     * (via [theoryFactory]).
     * @throws IllegalArgumentException if any of [clauseFunctions] returns a value that [toClause] cannot convert.
     */
    @JsName("mutableTheory")
    fun mutableTheory(vararg clauseFunctions: S.() -> Any): MutableTheory =
        mutableTheoryOf(clauseFunctions.map { newScope().it().toClause() })

    /** Overload of [TheoryFactory.mutableTheoryOf] concatenating [clauses] and [otherClauses] into one [MutableTheory]. */
    @JsName("mutableTheoryOfConcatenatedSequences")
    fun mutableTheoryOf(
        clauses: Sequence<Clause>,
        vararg otherClauses: Sequence<Clause>,
    ): MutableTheory = mutableTheoryOf(clauses + otherClauses.flatMap { it })

    /** Overload of [TheoryFactory.mutableTheoryOf] concatenating [clauses] and [otherClauses] into one [MutableTheory]. */
    @JsName("mutableTheoryOfConcatenatedIterables")
    fun mutableTheoryOf(
        clauses: Iterable<Clause>,
        vararg otherClauses: Iterable<Clause>,
    ): MutableTheory = mutableTheoryOf(kotlin.collections.listOf(clauses, *otherClauses).flatten())
}
