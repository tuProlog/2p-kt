package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.problog.lib.knowledge.MutableProblogTheory
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * An implementation of [MutableProblogTheory] that makes use of our internal
 * probabilistic knowledge mapping strategy. The core idea is to convert the Problog theory
 * in a Prolog compliant theory, so that we can leverage a simple Prolog solver to compute
 * probabilistic query solutions.
 *
 * @author Jason Dellaluce
 */
internal class MappedProblogTheory(
    clauses: Iterable<Clause>?,
    unificator: Unificator = Unificator.default,
    val theory: Theory = Theory.indexedOf(unificator, clauses?.flatMap { ClauseMappingUtils.map(it) } ?: emptyList())
) : Theory by theory, ProblogTheory {

    override val isMutable: Boolean
        get() = false

    override fun toMutableTheory(): MutableProblogTheory = MutableProblogTheory.of(unificator, this)

    override fun toImmutableTheory(): ProblogTheory = this

    override fun plus(theory: ProblogTheory): ProblogTheory {
        return MappedProblogTheory(null, unificator, this.theory.plus(theory))
    }

    override fun plus(theory: Theory): ProblogTheory {
        return when (theory) {
            is ProblogTheory -> plus(theory)
            else -> plus(MappedProblogTheory(theory, unificator))
        }
    }

    override fun plus(clause: Clause): ProblogTheory {
        val mapped = ClauseMappingUtils.map(clause)
        return MappedProblogTheory(null, unificator, this.theory.plus(Theory.indexedOf(unificator, mapped)))
    }

    override fun assertA(clauses: Iterable<Clause>): ProblogTheory {
        val mapped = clauses.flatMap { ClauseMappingUtils.map(it) }
        return MappedProblogTheory(null, unificator, this.theory.assertA(mapped))
    }

    override fun assertZ(clauses: Iterable<Clause>): ProblogTheory {
        val mapped = clauses.flatMap { ClauseMappingUtils.map(it) }
        return MappedProblogTheory(null, unificator, this.theory.assertZ(mapped))
    }

    override fun retract(clauses: Iterable<Clause>): RetractResult<ProblogTheory> {
        val mapped = clauses.flatMap { ClauseMappingUtils.map(it) }
        return when (val result = theory.retract(mapped)) {
            is RetractResult.Success -> RetractResult.Success(
                MappedProblogTheory(null, unificator, result.theory),
                result.clauses
            )
            else -> RetractResult.Failure(MappedProblogTheory(null, unificator, result.theory))
        }
    }

    override fun retractAll(clause: Clause): RetractResult<ProblogTheory> {
        val mappedClauses = ClauseMappingUtils.map(clause)
        var result: RetractResult<Theory>? = null
        for (c in mappedClauses) {
            if (result == null || result !is RetractResult.Failure) {
                result = theory.retractAll(c)
            }
        }

        return when (result) {
            is RetractResult.Success -> RetractResult.Success(
                MappedProblogTheory(null, unificator, result.theory),
                result.clauses
            )
            else -> RetractResult.Failure(this)
        }
    }

    override fun abolish(indicator: Indicator): ProblogTheory {
        return MappedProblogTheory(null, unificator, this.theory.abolish(ClauseMappingUtils.map(indicator)))
    }

    override fun assertA(struct: Struct): ProblogTheory = assertA(Fact.of(struct))

    override fun assertZ(struct: Struct): ProblogTheory = assertZ(Fact.of(struct))

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

    override fun retract(head: Struct): RetractResult<ProblogTheory> =
        retract(Rule.of(head, Var.anonymous()))

    override fun retract(clause: Clause): RetractResult<ProblogTheory> {
        return retract(listOf(clause))
    }

    override fun retract(clauses: Sequence<Clause>): RetractResult<ProblogTheory> {
        return retract(clauses.asIterable())
    }

    override fun retractAll(head: Struct): RetractResult<ProblogTheory> =
        retractAll(Rule.of(head, Var.anonymous()))
}
