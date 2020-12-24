package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.problog.lib.knowledge.MutableProblogTheory
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory

internal class MappedProblogTheory(
    inputClauses: Iterable<Clause>
) : ProblogTheory {

    /* NOTE: Delegation has been implemented manually due to the occurrence
    * of type conflicts of return values in many methods. */
    private var delegate: Theory = Theory.indexedOf(
        when (inputClauses) {
            is ProblogTheory -> inputClauses
            else -> inputClauses.flatMap { ProblogClauseMapper.apply(it) }
        }
    )

    private constructor(theory: Theory) : this(listOf()) {
        this.delegate = when (theory) {
            is MappedProblogTheory -> theory.delegate
            else -> theory
        }
    }

    override fun assertA(clauses: Iterable<Clause>): ProblogTheory {
        val newDelegate = delegate.assertA(clauses.flatMap { ProblogClauseMapper.apply(it) })
        return MappedProblogTheory(newDelegate)
    }

    override fun assertZ(clauses: Iterable<Clause>): ProblogTheory {
        val newDelegate = delegate.assertZ(clauses.flatMap { ProblogClauseMapper.apply(it) })
        return MappedProblogTheory(newDelegate)
    }

    override fun retract(clauses: Iterable<Clause>): RetractResult<ProblogTheory> {
        val result = delegate.retract(clauses.flatMap { ProblogClauseMapper.apply(it) })
        val newDelegate = result.theory
        return when (result) {
            is RetractResult.Success -> RetractResult.Success(MappedProblogTheory(newDelegate), result.clauses)
            else -> RetractResult.Failure(MappedProblogTheory(newDelegate))
        }
    }

    override fun plus(theory: ProblogTheory): ProblogTheory {
        val newDelegate = delegate.plus(theory)
        return MappedProblogTheory(newDelegate)
    }

    /* NOTE: Double-check this, i'm not 100% sure this is gonna work */
    override fun retractAll(clause: Clause): RetractResult<ProblogTheory> {
        val mappedClauses = ProblogClauseMapper.apply(clause)
        var result: RetractResult<Theory>? = null
        for (c in mappedClauses) {
            if (result == null || result !is RetractResult.Failure) {
                result = delegate.retractAll(c)
            }
        }
        val newDelegate = result?.theory!!
        return when (result) {
            is RetractResult.Success -> RetractResult.Success(MappedProblogTheory(newDelegate), result.clauses)
            else -> RetractResult.Failure(MappedProblogTheory(delegate))
        }
    }

    override fun abolish(indicator: Indicator): ProblogTheory {
        val newDelegate = delegate.abolish(indicator)
        return MappedProblogTheory(newDelegate)
    }

    override fun plus(theory: Theory): ProblogTheory {
        return when (theory) {
            is MappedProblogTheory -> plus(theory)
            else -> plus(MappedProblogTheory(theory))
        }
    }

    override fun assertA(clause: Clause): ProblogTheory {
        return assertA(listOf(clause))
    }

    override fun assertA(clauses: Sequence<Clause>): ProblogTheory {
        return assertA(clauses.asIterable())
    }

    override fun assertZ(clause: Clause): ProblogTheory {
        return assertZ(listOf(clause))
    }

    override fun assertZ(clauses: Sequence<Clause>): ProblogTheory {
        return assertZ(clauses.asIterable())
    }

    override fun retract(clause: Clause): RetractResult<ProblogTheory> {
        return retract(listOf(clause))
    }

    override fun retract(clauses: Sequence<Clause>): RetractResult<ProblogTheory> {
        return retract(clauses.asIterable())
    }

    override fun toMutableTheory(): MutableProblogTheory {
        return MappedMutableProblogTheory(this)
    }

    override val clauses: Iterable<Clause>
        get() = delegate.clauses

    override val size: Long
        get() = delegate.size

    override fun contains(clause: Clause): Boolean {
        return delegate.contains(clause)
    }

    override fun contains(head: Struct): Boolean {
        return delegate.contains(head)
    }

    override fun contains(indicator: Indicator): Boolean {
        return delegate.contains(indicator)
    }

    override fun get(clause: Clause): Sequence<Clause> {
        return delegate[clause]
    }

    override fun get(head: Struct): Sequence<Rule> {
        return delegate[head]
    }

    override fun get(indicator: Indicator): Sequence<Rule> {
        return delegate[indicator]
    }

    override fun toString(asPrologText: Boolean): String {
        return delegate.toString(asPrologText)
    }

    override fun iterator(): Iterator<Clause> {
        return delegate.iterator()
    }
}
