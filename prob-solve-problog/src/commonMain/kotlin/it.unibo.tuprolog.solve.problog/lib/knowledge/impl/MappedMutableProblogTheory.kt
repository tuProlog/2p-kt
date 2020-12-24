package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.problog.lib.knowledge.MutableProblogTheory
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory

internal class MappedMutableProblogTheory(
    inputClauses: Iterable<Clause>
) : MutableProblogTheory {

    /* NOTE: Delegation has been implemented manually due to the occurrence
    * of type conflicts of return values in many methods. */
    private val delegate: MutableTheory = MutableTheory.indexedOf(
        when (inputClauses) {
            is ProblogTheory -> inputClauses
            else -> inputClauses.flatMap { ProblogClauseMapper.apply(it) }
        }
    )

    override fun plus(theory: ProblogTheory): MutableProblogTheory {
        delegate.plus(theory)
        return this
    }

    override fun assertA(clauses: Iterable<Clause>): MutableProblogTheory {
        delegate.assertA(clauses.flatMap { ProblogClauseMapper.apply(it) })
        return this
    }

    override fun assertZ(clauses: Iterable<Clause>): MutableProblogTheory {
        delegate.assertZ(clauses.flatMap { ProblogClauseMapper.apply(it) })
        return this
    }

    override fun retract(clauses: Iterable<Clause>): RetractResult<MutableProblogTheory> {
        return when (val result = delegate.retract(clauses)) {
            is RetractResult.Success -> RetractResult.Success(this, result.clauses)
            else -> RetractResult.Failure(this)
        }
    }

    override fun plus(theory: Theory): MutableProblogTheory {
        return when (theory) {
            is ProblogTheory -> plus(theory)
            else -> plus(MappedMutableProblogTheory(theory))
        }
    }

    override fun retractAll(clause: Clause): RetractResult<MutableProblogTheory> {
        return when (val result = delegate.retractAll(clause)) {
            is RetractResult.Success -> RetractResult.Success(this, result.clauses)
            else -> RetractResult.Failure(this)
        }
    }

    override fun abolish(indicator: Indicator): MutableProblogTheory {
        delegate.abolish(indicator)
        return this
    }

    override fun assertA(clause: Clause): MutableProblogTheory {
        return assertA(listOf(clause))
    }

    override fun assertA(clauses: Sequence<Clause>): MutableProblogTheory {
        return assertA(clauses.asIterable())
    }

    override fun assertZ(clause: Clause): MutableProblogTheory {
        return assertZ(listOf(clause))
    }

    override fun assertZ(clauses: Sequence<Clause>): MutableProblogTheory {
        return assertZ(clauses.asIterable())
    }

    override fun retract(clause: Clause): RetractResult<MutableProblogTheory> {
        return retract(listOf(clause))
    }

    override fun retract(clauses: Sequence<Clause>): RetractResult<MutableProblogTheory> {
        return retract(clauses.asIterable())
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
