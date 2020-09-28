package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.collections.MutableClauseQueue
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.AbstractTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect
import it.unibo.tuprolog.utils.dequeOf

internal class IndexedTheory private constructor(queue: ClauseQueue) : AbstractIndexedTheory(queue) {

    /** Construct a Clause database from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ClauseQueue.of(clauses)) {
        checkClausesCorrect(clauses)
    }

    /** Construct a Clause database from given clauses */
    constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

    override fun createNewTheory(clauses: Sequence<Clause>): AbstractTheory {
        return IndexedTheory(clauses)
    }

    override fun retract(clause: Clause): RetractResult<IndexedTheory> {
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

    override fun retract(clauses: Iterable<Clause>): RetractResult<IndexedTheory> {
        val newTheory = MutableClauseQueue.of(this.clauses)
        val removed = dequeOf<Clause>()
        for (clause in clauses) {
            val result = newTheory.retrieveFirst(clause)
            if (result is RetrieveResult.Success) {
                removed.addAll(result.clauses)
            }
        }
        return if (removed.isEmpty()) {
            RetractResult.Failure(this)
        } else {
            RetractResult.Success(
                IndexedTheory(newTheory),
                removed
            )
        }
    }

    override fun retractAll(clause: Clause): RetractResult<IndexedTheory> {
        val newTheory = ClauseQueue.of(clauses)
        return when (val retracted = newTheory.retrieveAll(clause)) {
            is RetrieveResult.Failure -> RetractResult.Failure(this)
            else -> RetractResult.Success(
                IndexedTheory(retracted.collection),
                (retracted as RetrieveResult.Success).clauses
            )
        }
    }

    private val hashCodeCache: Int by lazy {
        super.hashCode()
    }

    override fun hashCode(): Int {
        return hashCodeCache
    }

    private val sizeCache: Long by lazy {
        super.size
    }

    override val size: Long
        get() = sizeCache
}
