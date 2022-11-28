package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.MutableClauseQueue
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryUtils.checkClauseCorrect
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect
import it.unibo.tuprolog.unify.Unificator

internal class MutableIndexedTheory private constructor(
    override val queue: MutableClauseQueue,
    tags: Map<String, Any>
) : AbstractIndexedTheory(queue, tags), MutableTheory {

    override fun setUnificator(unificator: Unificator): MutableTheory =
        super<AbstractIndexedTheory>.setUnificator(unificator).toMutableTheory()

    /** Construct a Clause database from given clauses */
    constructor(
        unificator: Unificator,
        clauses: Iterable<Clause>,
        tags: Map<String, Any> = emptyMap()
    ) : this(MutableClauseQueue.of(unificator, clauses), tags) {
        checkClausesCorrect(clauses)
    }

    /** Construct a Clause database from given clauses */
    constructor(
        unificator: Unificator,
        clauses: Sequence<Clause>,
        tags: Map<String, Any> = emptyMap()
    ) : this(unificator, clauses.asIterable(), tags)

    override fun toMutableTheory(): MutableTheory = super<MutableTheory>.toMutableTheory()

    override val clauses: List<Clause> get() = queue.toList()

    override fun iterator(): Iterator<Clause> = queue.iterator()

    override fun createNewTheory(
        clauses: Sequence<Clause>,
        tags: Map<String, Any>,
        unificator: Unificator
    ): MutableIndexedTheory = MutableIndexedTheory(unificator, clauses, tags)

    override fun retract(clause: Clause): RetractResult<MutableIndexedTheory> =
        queue.retrieve(clause).toRetractResult()

    private fun <C : ClauseCollection> RetrieveResult<C>.toRetractResult(): RetractResult<MutableIndexedTheory> =
        when {
            isSuccess -> RetractResult.Success(this@MutableIndexedTheory, clauses!!)
            else -> RetractResult.Failure(this@MutableIndexedTheory)
        }

    override fun retract(clauses: Iterable<Clause>): RetractResult<MutableIndexedTheory> {
        val retracted = clauses.asSequence()
            .map { queue.retrieve(it) }
            .filter { it.isSuccess }
            .flatMap { it.clauses!!.asSequence() }
            .toList()
        return if (retracted.isEmpty()) RetractResult.Failure(this)
        else RetractResult.Success(this, retracted)
    }

    override fun retractAll(clause: Clause): RetractResult<MutableIndexedTheory> =
        queue.retrieveAll(clause).toRetractResult()

    override fun plus(clause: Clause): MutableIndexedTheory = assertZ(checkClauseCorrect(clause))

    override fun plus(theory: Theory): MutableIndexedTheory {
        return if (theory === this) {
            assertZ(theory.toList())
        } else {
            assertZ(theory)
        }
    }

    override fun assertA(clause: Clause): MutableIndexedTheory =
        this.also { it.queue.addFirst(checkClauseCorrect(clause)) }

    override fun assertA(clauses: Iterable<Clause>): MutableIndexedTheory {
        return this.also {
            for (clause in checkClausesCorrect(clauses).toList().asReversed()) {
                it.queue.addFirst(clause)
            }
        }
    }

    override fun assertA(clauses: Sequence<Clause>): MutableIndexedTheory = assertA(clauses.asIterable())

    override fun assertZ(clause: Clause): MutableIndexedTheory =
        this.also { it.queue.addLast(checkClauseCorrect(clause)) }

    override fun assertZ(clauses: Iterable<Clause>): MutableIndexedTheory =
        this.also { it.queue.addAll(checkClausesCorrect(clauses)) }

    override fun assertZ(clauses: Sequence<Clause>): MutableIndexedTheory =
        assertZ(checkClausesCorrect(clauses.asIterable()))

    override fun retract(clauses: Sequence<Clause>): RetractResult<MutableIndexedTheory> =
        retract(clauses.asIterable())

    override fun abolish(indicator: Indicator): MutableTheory = super.abolish(indicator).toMutableTheory()

    override fun toImmutableTheory(): Theory = Theory.indexedOf(unificator, this)

    override fun replaceTags(tags: Map<String, Any>): MutableIndexedTheory =
        if (tags === this.tags) this else MutableIndexedTheory(queue, tags)

    override fun clone(): MutableTheory = createNewTheory(clauses.asSequence())
}
