package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.UnificationAware
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

/**
 * A factory of [Theory]/[MutableTheory] instances, all sharing the same backing data structure (e.g. indexed or
 * listed, see [IndexedTheoryFactory] and [ListedTheoryFactory]) and, by default, the same [unificator].
 *
 * This exists so that code building many theories (e.g. a solver library loading several source files) can
 * depend on "a way of making theories" without hard-coding which concrete implementation — indexed or listed —
 * gets used; swapping [IndexedTheoryFactory] for [ListedTheoryFactory] (or vice versa) changes every theory the
 * factory produces without touching the calling code.
 */
interface TheoryFactory : UnificationAware {
    /** Returns a factory equivalent to this one, but using the given [unificator] as its default. */
    fun copy(unificator: Unificator): TheoryFactory

    /** Creates an empty [Theory] using the given [unificator]. */
    @JsName("emptyTheoryWithUnificator")
    fun emptyTheory(unificator: Unificator): Theory

    /** Creates an empty [Theory] using this factory's [unificator]. */
    @JsName("emptyTheory")
    fun emptyTheory(): Theory = emptyTheory(unificator)

    /** Creates a [Theory] containing the given [clauses], using the given [unificator]. */
    @JsName("theoryOfIterableWithUnificator")
    fun theoryOf(
        clauses: Iterable<Clause>,
        unificator: Unificator,
    ): Theory

    /** Creates a [Theory] containing the given [clauses], using this factory's [unificator]. */
    @JsName("theoryOfIterable")
    fun theoryOf(clauses: Iterable<Clause>): Theory = theoryOf(clauses, unificator)

    /** Creates a [Theory] containing the given [clauses], using the given [unificator]. */
    @JsName("theoryOfWithUnificator")
    fun theoryOf(
        unificator: Unificator,
        vararg clauses: Clause,
    ): Theory

    /** Creates a [Theory] containing the given [clauses], using this factory's [unificator]. */
    @JsName("theoryOf")
    fun theoryOf(vararg clauses: Clause): Theory = theoryOf(unificator, *clauses)

    /** Creates a [Theory] containing the given [clauses], using the given [unificator]. */
    @JsName("theoryOfSequenceWithUnificator")
    fun theoryOf(
        clauses: Sequence<Clause>,
        unificator: Unificator,
    ): Theory

    /** Creates a [Theory] containing the given [clauses], using this factory's [unificator]. */
    @JsName("theoryOfSequence")
    fun theoryOf(clauses: Sequence<Clause>): Theory = theoryOf(clauses, unificator)

    /** Creates an empty [MutableTheory] using the given [unificator]. */
    @JsName("emptyMutableTheoryWithUnificator")
    fun emptyMutableTheory(unificator: Unificator): MutableTheory

    /** Creates an empty [MutableTheory] using this factory's [unificator]. */
    @JsName("emptyMutableTheory")
    fun emptyMutableTheory(): MutableTheory = emptyMutableTheory(unificator)

    /** Creates a [MutableTheory] containing the given [clauses], using the given [unificator]. */
    @JsName("mutableTheoryOfIterableWithUnificator")
    fun mutableTheoryOf(
        clauses: Iterable<Clause>,
        unificator: Unificator,
    ): MutableTheory

    /** Creates a [MutableTheory] containing the given [clauses], using this factory's [unificator]. */
    @JsName("mutableTheoryOfIterable")
    fun mutableTheoryOf(clauses: Iterable<Clause>): MutableTheory = mutableTheoryOf(clauses, unificator)

    /** Creates a [MutableTheory] containing the given [clauses], using the given [unificator]. */
    @JsName("mutableTheoryOfWithUnificator")
    fun mutableTheoryOf(
        unificator: Unificator,
        vararg clauses: Clause,
    ): MutableTheory

    /** Creates a [MutableTheory] containing the given [clauses], using this factory's [unificator]. */
    @JsName("mutableTheoryOf")
    fun mutableTheoryOf(vararg clauses: Clause): MutableTheory = mutableTheoryOf(unificator, *clauses)

    /** Creates a [MutableTheory] containing the given [clauses], using the given [unificator]. */
    @JsName("mutableTheoryOfSequenceWithUnificator")
    fun mutableTheoryOf(
        clauses: Sequence<Clause>,
        unificator: Unificator,
    ): MutableTheory

    /** Creates a [MutableTheory] containing the given [clauses], using this factory's [unificator]. */
    @JsName("mutableTheoryOfSequence")
    fun mutableTheoryOf(clauses: Sequence<Clause>): MutableTheory = mutableTheoryOf(clauses, unificator)
}
