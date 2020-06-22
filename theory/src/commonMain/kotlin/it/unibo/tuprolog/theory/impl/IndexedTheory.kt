package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.AbstractTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect

internal class IndexedTheory private constructor(private val queue: ClauseQueue) : AbstractTheory() {

    /** Construct a Clause database from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ClauseQueue.of(clauses)) {
        checkClausesCorrect(clauses)
    }

    /** Construct a Clause database from given clauses */
    constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

    override val clauses: Iterable<Clause> by lazy { queue.toList() }

    override fun get(clause: Clause): Sequence<Clause> = queue[clause]

    override fun createNewTheory(clauses: Sequence<Clause>): AbstractTheory {
        return IndexedTheory(clauses)
    }

    override fun retract(clause: Clause): RetractResult {
        val newTheory = ClauseQueue.of(clauses)
        return when (val retracted = newTheory.retrieveFirst(clause)) {
            is RetrieveResult.Failure ->
                RetractResult.Failure(this)
            else -> RetractResult.Success(
                IndexedTheory(retracted.collection),
                (retracted as RetrieveResult.Success).clauses
            )
        }
    }

    override fun retractAll(clause: Clause): RetractResult {
        val newTheory = ClauseQueue.of(clauses)
        return when (val retracted = newTheory.retrieveAll(clause)) {
            is RetrieveResult.Failure -> RetractResult.Failure(this)
            else -> RetractResult.Success(
                IndexedTheory(retracted.collection),
                (retracted as RetrieveResult.Success).clauses
            )
        }
    }
}