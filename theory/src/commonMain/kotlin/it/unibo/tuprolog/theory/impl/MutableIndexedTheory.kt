package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.MutableClauseQueue
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.theory.AbstractTheory
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect

internal class MutableIndexedTheory private constructor(override val queue: MutableClauseQueue) :
    AbstractIndexedTheory(queue), MutableTheory {

    /** Construct a Clause database from given clauses */
    constructor(clauses: Iterable<Clause>) : this(MutableClauseQueue.of(clauses)) {
        checkClausesCorrect(clauses)
    }

    override fun createNewTheory(clauses: Sequence<Clause>): AbstractTheory {
        return MutableIndexedTheory(clauses)
    }

    /** Construct a Clause database from given clauses */
    constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

    override fun retract(clause: Clause): RetractResult<MutableIndexedTheory> {
        return queue.retrieve(clause).toRetractResult()
    }

    private fun <C : ClauseCollection> RetrieveResult<C>.toRetractResult(): RetractResult<MutableIndexedTheory> =
        when (this) {
            is RetrieveResult.Success -> RetractResult.Success(this@MutableIndexedTheory, clauses)
            else -> RetractResult.Failure(this@MutableIndexedTheory)
        }

    override fun retract(clauses: Iterable<Clause>): RetractResult<MutableIndexedTheory> {
        val retracted = clauses.asSequence()
            .map { queue.retrieve(it) }
            .filterIsInstance<RetrieveResult.Success<*>>()
            .flatMap { it.clauses.asSequence() }
            .toList()
        return if (retracted.isEmpty()) RetractResult.Failure(this)
        else RetractResult.Success(this, retracted)
    }

    override fun retractAll(clause: Clause): RetractResult<MutableIndexedTheory> {
        return queue.retrieveAll(clause).toRetractResult()
    }

    override fun plus(clause: Clause): MutableIndexedTheory {
        return assertZ(clause)
    }

    override fun plus(theory: Theory): MutableIndexedTheory {
        return assertZ(theory)
    }

    override fun assertA(clause: Clause): MutableIndexedTheory {
        return this.also { it.queue.addFirst(clause) }
    }

    override fun assertA(clauses: Iterable<Clause>): MutableIndexedTheory {
        return this.also {
            for (clause in clauses.toList().asReversed()) {
                it.queue.addFirst(clause)
            }
        }
    }

    override fun assertA(clauses: Sequence<Clause>): MutableIndexedTheory {
        return assertA(clauses.asIterable())
    }

    override fun assertZ(clause: Clause): MutableIndexedTheory {
        return this.also { it.queue.addLast(clause) }
    }

    override fun assertZ(clauses: Iterable<Clause>): MutableIndexedTheory {
        return this.also { it.queue.addAll(clauses) }
    }

    override fun assertZ(clauses: Sequence<Clause>): MutableIndexedTheory {
        return assertZ(clauses.asIterable())
    }

    override fun retract(clauses: Sequence<Clause>): RetractResult<MutableIndexedTheory> {
        return retract(clauses.asIterable())
    }

    override fun abolish(indicator: Indicator): MutableIndexedTheory {
        return super.abolish(indicator) as MutableIndexedTheory
    }
}
