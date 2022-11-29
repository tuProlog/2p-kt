package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.dequeOf
import kotlin.collections.List as KtList

internal class ListedTheory private constructor(
    unificator: Unificator,
    clauses: KtList<Clause>,
    tags: Map<String, Any>
) : AbstractListedTheory(unificator, clauses, tags) {

    constructor(
        unificator: Unificator,
        clauses: Iterable<Clause>,
        tags: Map<String, Any> = emptyMap()
    ) : this(unificator, clauses.toList(), tags) {
        checkClausesCorrect(clauses)
    }

    constructor(
        unificator: Unificator,
        clauses: Sequence<Clause>,
        tags: Map<String, Any> = emptyMap()
    ) : this(unificator, clauses.toList(), tags) {
        checkClausesCorrect(clauses)
    }

    override fun createNewTheory(
        clauses: Sequence<Clause>,
        tags: Map<String, Any>,
        unificator: Unificator
    ) = ListedTheory(unificator, clauses, tags)

    override fun retract(clause: Clause): RetractResult<ListedTheory> {
        val retractability = clauses.filter { unificator.match(it, clause) }
        return when {
            retractability.none() -> RetractResult.Failure(this)
            else -> {
                val toBeActuallyRetracted = retractability.first()
                val newTheory = clauses.filter { it != toBeActuallyRetracted }
                RetractResult.Success(
                    ListedTheory(unificator, newTheory, tags),
                    listOf(toBeActuallyRetracted)
                )
            }
        }
    }

    override fun toMutableTheory(): MutableTheory = MutableTheory.listedOf(unificator, this)

    override fun retract(clauses: Iterable<Clause>): RetractResult<ListedTheory> {
        val residual = dequeOf(this.clauses)
        val removed = dequeOf<Clause>()
        val i = residual.iterator()
        while (i.hasNext()) {
            val current = i.next()
            if (clauses.any { unificator.match(it, current) }) {
                i.remove()
                removed.add(current)
            }
        }
        return if (removed.isEmpty()) {
            RetractResult.Failure(this)
        } else {
            RetractResult.Success(ListedTheory(unificator, residual, tags), removed)
        }
    }

    override fun retractAll(clause: Clause): RetractResult<ListedTheory> {
        val retractability = clauses.filter { unificator.match(it, clause) }
        return when {
            retractability.none() -> RetractResult.Failure(this)
            else -> {
                val partitionedClauses = clauses.toList().partition { unificator.match(it, clause) }
                val newTheory = partitionedClauses.second
                val toBeActuallyRetracted = partitionedClauses.first
                RetractResult.Success(
                    ListedTheory(unificator, newTheory, tags),
                    toBeActuallyRetracted
                )
            }
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

    override fun replaceTags(tags: Map<String, Any>): ListedTheory =
        if (tags === this.tags) this else ListedTheory(unificator, clauses, tags)

    override fun plus(theory: Theory): Theory =
        when {
            isEmpty -> theory.toImmutableTheory()
            theory.isEmpty -> this
            else -> super.plus(theory)
        }
}
