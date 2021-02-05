package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.theory.AbstractTheory
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryUtils.checkClauseCorrect
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.utils.addFirst

internal class MutableListedTheory private constructor(
    override val clauses: MutableList<Clause>,
    tags: Map<String, Any>
) : AbstractListedTheory(clauses, tags), MutableTheory {

    constructor(
        clauses: Iterable<Clause>,
        tags: Map<String, Any> = emptyMap()
    ) : this(clauses.toMutableList(), tags) {
        checkClausesCorrect(clauses)
    }

    constructor(
        clauses: Sequence<Clause>,
        tags: Map<String, Any> = emptyMap()
    ) : this(clauses.toMutableList(), tags) {
        checkClausesCorrect(clauses)
    }

    override fun createNewTheory(clauses: Sequence<Clause>, tags: Map<String, Any>): AbstractTheory =
        MutableListedTheory(clauses, tags)

    override fun retract(clause: Clause): RetractResult<MutableListedTheory> {
        val i = clauses.listIterator()
        while (i.hasNext()) {
            val c = i.next()
            if (c matches clause) {
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
                if (c matches clause) {
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
            if (c matches clause) {
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

    override fun abolish(indicator: Indicator): MutableListedTheory = super.abolish(indicator) as MutableListedTheory

    override fun toImmutableTheory(): Theory = Theory.listedOf(this)

    override fun replaceTags(tags: Map<String, Any>): MutableListedTheory =
        if (tags === this.tags) this else MutableListedTheory(clauses, tags)

    override fun clone(): MutableTheory = super.clone() as MutableTheory
}
