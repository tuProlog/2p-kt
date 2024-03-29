package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.UnificationAware
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

interface TheoryFactory : UnificationAware {
    fun copy(unificator: Unificator): TheoryFactory

    @JsName("emptyTheoryWithUnificator")
    fun emptyTheory(unificator: Unificator): Theory

    @JsName("emptyTheory")
    fun emptyTheory(): Theory = emptyTheory(unificator)

    @JsName("theoryOfIterableWithUnificator")
    fun theoryOf(
        clauses: Iterable<Clause>,
        unificator: Unificator,
    ): Theory

    @JsName("theoryOfIterable")
    fun theoryOf(clauses: Iterable<Clause>): Theory = theoryOf(clauses, unificator)

    @JsName("theoryOfWithUnificator")
    fun theoryOf(
        unificator: Unificator,
        vararg clauses: Clause,
    ): Theory

    @JsName("theoryOf")
    fun theoryOf(vararg clauses: Clause): Theory = theoryOf(unificator, *clauses)

    @JsName("theoryOfSequenceWithUnificator")
    fun theoryOf(
        clauses: Sequence<Clause>,
        unificator: Unificator,
    ): Theory

    @JsName("theoryOfSequence")
    fun theoryOf(clauses: Sequence<Clause>): Theory = theoryOf(clauses, unificator)

    @JsName("emptyMutableTheoryWithUnificator")
    fun emptyMutableTheory(unificator: Unificator): MutableTheory

    @JsName("emptyMutableTheory")
    fun emptyMutableTheory(): MutableTheory = emptyMutableTheory(unificator)

    @JsName("mutableTheoryOfIterableWithUnificator")
    fun mutableTheoryOf(
        clauses: Iterable<Clause>,
        unificator: Unificator,
    ): MutableTheory

    @JsName("mutableTheoryOfIterable")
    fun mutableTheoryOf(clauses: Iterable<Clause>): MutableTheory = mutableTheoryOf(clauses, unificator)

    @JsName("mutableTheoryOfWithUnificator")
    fun mutableTheoryOf(
        unificator: Unificator,
        vararg clauses: Clause,
    ): MutableTheory

    @JsName("mutableTheoryOf")
    fun mutableTheoryOf(vararg clauses: Clause): MutableTheory = mutableTheoryOf(unificator, *clauses)

    @JsName("mutableTheoryOfSequenceWithUnificator")
    fun mutableTheoryOf(
        clauses: Sequence<Clause>,
        unificator: Unificator,
    ): MutableTheory

    @JsName("mutableTheoryOfSequence")
    fun mutableTheoryOf(clauses: Sequence<Clause>): MutableTheory = mutableTheoryOf(clauses, unificator)
}
