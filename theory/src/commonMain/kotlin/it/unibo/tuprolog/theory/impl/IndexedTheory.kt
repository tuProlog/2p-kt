package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.collections.MutableClauseQueue
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.dequeOf

internal class IndexedTheory private constructor(
    queue: ClauseQueue,
    tags: Map<String, Any>
) : AbstractIndexedTheory(queue, tags) {

    /** Construct a Clause database from given clauses */
    constructor(
        unificator: Unificator,
        clauses: Iterable<Clause>,
        tags: Map<String, Any> = emptyMap()
    ) : this(ClauseQueue.of(unificator, clauses), tags) {
        checkClausesCorrect(clauses)
    }

    /** Construct a Clause database from given clauses */
    constructor(
        unificator: Unificator,
        clauses: Sequence<Clause>,
        tags: Map<String, Any> = emptyMap()
    ) : this(unificator, clauses.asIterable(), tags)

    override fun createNewTheory(
        clauses: Sequence<Clause>,
        tags: Map<String, Any>,
        unificator: Unificator
    ) = IndexedTheory(unificator, clauses, tags)

    override fun retract(clause: Clause): RetractResult<IndexedTheory> {
        val newTheory = ClauseQueue.of(unificator, clauses)
        val retracted = newTheory.retrieveFirst(clause)
        return when {
            retracted.isFailure -> RetractResult.Failure(this)
            else -> RetractResult.Success(
                IndexedTheory(retracted.collection, tags),
                retracted.clauses!!
            )
        }
    }

    override fun plus(theory: Theory): Theory =
        when {
            isEmpty -> theory.toImmutableTheory()
            theory.isEmpty -> this
            else -> super.plus(theory)
        }

    override fun toMutableTheory(): MutableTheory = MutableTheory.indexedOf(unificator, this)

    override fun retract(clauses: Iterable<Clause>): RetractResult<IndexedTheory> {
        val newTheory = MutableClauseQueue.of(unificator, this.clauses)
        val removed = dequeOf<Clause>()
        for (clause in clauses) {
            val result = newTheory.retrieveFirst(clause)
            if (result.isSuccess) {
                removed.addAll(result.clauses!!)
            }
        }
        return if (removed.isEmpty()) {
            RetractResult.Failure(this)
        } else {
            RetractResult.Success(
                IndexedTheory(newTheory, tags),
                removed
            )
        }
    }

    override fun retractAll(clause: Clause): RetractResult<IndexedTheory> {
        val newTheory = ClauseQueue.of(unificator, clauses)
        val retracted = newTheory.retrieveAll(clause)
        return when {
            retracted.isFailure -> RetractResult.Failure(this)
            else -> RetractResult.Success(
                IndexedTheory(retracted.collection, tags),
                retracted.clauses!!
            )
        }
    }

    private val hashCodeCache: Int by lazy { super.hashCode() }

    override fun hashCode(): Int { return hashCodeCache }

    private val sizeCache: Long by lazy { super.size }

    override val size: Long
        get() = sizeCache

    override fun replaceTags(tags: Map<String, Any>): IndexedTheory =
        if (tags === this.tags) this else IndexedTheory(queue, tags)
}
