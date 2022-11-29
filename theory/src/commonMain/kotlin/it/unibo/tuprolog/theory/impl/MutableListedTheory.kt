package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryUtils.checkClauseCorrect
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.addFirst

internal class MutableListedTheory private constructor(
    unificator: Unificator,
    override val clauses: MutableList<Clause>,
    tags: Map<String, Any>
) : AbstractListedTheory(unificator, clauses, tags), MutableTheory {

    override fun setUnificator(unificator: Unificator): MutableTheory {
        this.unificator = unificator
        return this
    }

    constructor(
        unificator: Unificator,
        clauses: Iterable<Clause>,
        tags: Map<String, Any> = emptyMap()
    ) : this(unificator, clauses.toMutableList(), tags) {
        checkClausesCorrect(clauses)
    }

    constructor(
        unificator: Unificator,
        clauses: Sequence<Clause>,
        tags: Map<String, Any> = emptyMap()
    ) : this(unificator, clauses.toMutableList(), tags) {
        checkClausesCorrect(clauses)
    }

    override fun toMutableTheory(): MutableTheory = super<MutableTheory>.toMutableTheory()

    override fun createNewTheory(
        clauses: Sequence<Clause>,
        tags: Map<String, Any>,
        unificator: Unificator
    ) = MutableListedTheory(unificator, clauses, tags)

    override fun retract(clause: Clause): RetractResult<MutableListedTheory> {
        val i = clauses.listIterator()
        while (i.hasNext()) {
            val c = i.next()
            if (unificator.match(c, clause)) {
                i.remove()
                return RetractResult.Success(this, listOf(c))
            }
        }
        return RetractResult.Failure(this)
    }

    override fun retract(clauses: Iterable<Clause>): RetractResult<MutableListedTheory> {
        val i = this.clauses.listIterator()
        val retracted = mutableListOf<Clause>()
        while (i.hasNext()) {
            val c = i.next()
            for (clause in clauses) {
                if (unificator.match(c, clause)) {
                    retracted.add(c)
                    i.remove()
                }
            }
        }
        return if (retracted.isEmpty()) {
            RetractResult.Failure(this)
        } else {
            RetractResult.Success(this, retracted)
        }
    }

    override fun retractAll(clause: Clause): RetractResult<MutableListedTheory> {
        val i = clauses.listIterator()
        val retracted = mutableListOf<Clause>()
        while (i.hasNext()) {
            val c = i.next()
            if (unificator.match(c, clause)) {
                retracted.add(c)
                i.remove()
            }
        }
        return if (retracted.isEmpty()) {
            RetractResult.Failure(this)
        } else {
            RetractResult.Success(this, retracted)
        }
    }

    override fun plus(clause: Clause): MutableListedTheory = assertZ(clause)

    override fun plus(theory: Theory): MutableListedTheory {
        return if (theory === this) {
            assertZ(theory.toList())
        } else {
            assertZ(theory)
        }
    }

    override fun assertA(clause: Clause): MutableListedTheory =
        this.also { it.clauses.addFirst(checkClauseCorrect(clause)) }

    override fun assertA(clauses: Iterable<Clause>): MutableListedTheory =
        this.also { it.clauses.addFirst(checkClausesCorrect(clauses)) }

    override fun assertA(clauses: Sequence<Clause>): MutableListedTheory = assertA(clauses.asIterable())

    override fun assertZ(clause: Clause): MutableListedTheory =
        this.also { it.clauses.add(checkClauseCorrect(clause)) }

    override fun assertZ(clauses: Iterable<Clause>): MutableListedTheory =
        this.also { it.clauses.addAll(checkClausesCorrect(clauses)) }

    override fun assertZ(clauses: Sequence<Clause>): MutableListedTheory = assertZ(clauses.asIterable())

    override fun retract(clauses: Sequence<Clause>): RetractResult<MutableListedTheory> = retract(clauses.asIterable())

    override fun abolish(indicator: Indicator): MutableTheory = super.abolish(indicator).toMutableTheory()

    override fun toImmutableTheory(): Theory = Theory.listedOf(unificator, this)

    override fun replaceTags(tags: Map<String, Any>): MutableListedTheory =
        if (tags === this.tags) this else MutableListedTheory(unificator, clauses, tags)

    override fun clone(): MutableTheory = createNewTheory(clauses.asSequence())
}
