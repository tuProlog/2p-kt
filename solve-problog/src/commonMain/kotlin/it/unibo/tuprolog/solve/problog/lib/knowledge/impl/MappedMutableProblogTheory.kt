package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.problog.lib.knowledge.MutableProblogTheory
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
import it.unibo.tuprolog.theory.MutableTheory
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
internal class MappedMutableProblogTheory(
    clauses: Iterable<Clause>?,
    unificator: Unificator = Unificator.default,
    val theory: MutableTheory = MutableTheory.indexedOf(unificator, clauses?.flatMap { ClauseMappingUtils.map(it) } ?: emptyList())
) : MutableTheory by theory, MutableProblogTheory {

    override val isMutable: Boolean
        get() = false

    override fun toMutableTheory(): MutableProblogTheory = MutableProblogTheory.of(unificator, this)

    override fun toImmutableTheory(): ProblogTheory = this

    override fun plus(theory: ProblogTheory): MutableProblogTheory {
        return MappedMutableProblogTheory(null, unificator, this.theory.plus(theory))
    }

    override fun plus(theory: Theory): MutableProblogTheory {
        return when (theory) {
            is ProblogTheory -> plus(theory)
            else -> plus(MappedMutableProblogTheory(theory, unificator))
        }
    }

    override fun plus(clause: Clause): MutableProblogTheory {
        val mapped = ClauseMappingUtils.map(clause)
        return MappedMutableProblogTheory(null, unificator, this.theory.plus(Theory.indexedOf(unificator, mapped)))
    }

    override fun assertA(clauses: Iterable<Clause>): MutableProblogTheory {
        val mapped = clauses.flatMap { ClauseMappingUtils.map(it) }
        return MappedMutableProblogTheory(null, unificator, this.theory.assertA(mapped))
    }

    override fun assertZ(clauses: Iterable<Clause>): MutableProblogTheory {
        val mapped = clauses.flatMap { ClauseMappingUtils.map(it) }
        return MappedMutableProblogTheory(null, unificator, this.theory.assertZ(mapped))
    }

    override fun retract(clauses: Iterable<Clause>): RetractResult<MutableProblogTheory> {
        val mapped = clauses.flatMap { ClauseMappingUtils.map(it) }
        return when (val result = theory.retract(mapped)) {
            is RetractResult.Success -> RetractResult.Success(
                MappedMutableProblogTheory(null, unificator, result.theory),
                result.clauses
            )
            else -> RetractResult.Failure(MappedMutableProblogTheory(null, unificator, result.theory))
        }
    }

    /* NOTE: This has a side effect. If a `retractAll` fails for a clause inside `mappedClauses`
    * with index `I` > 0, then this returns a `RetractResult.Failure` containing a theory in which
    * clauses with index < `I` has been retracted. */
    override fun retractAll(clause: Clause): RetractResult<MutableProblogTheory> {
        val mappedClauses = ClauseMappingUtils.map(clause)
        var result: RetractResult<MutableTheory>? = null
        for (c in mappedClauses) {
            if (result == null || result !is RetractResult.Failure) {
                result = theory.retractAll(c)
            }
        }

        return when (result) {
            is RetractResult.Success -> RetractResult.Success(
                MappedMutableProblogTheory(null, unificator, result.theory),
                result.clauses
            )
            is RetractResult.Failure -> RetractResult.Failure(MappedMutableProblogTheory(null, unificator, result.theory))
            else -> RetractResult.Failure(this)
        }
    }

    override fun abolish(indicator: Indicator): MutableProblogTheory {
        return MappedMutableProblogTheory(null, unificator, this.theory.abolish(ClauseMappingUtils.map(indicator)))
    }

    override fun assertA(struct: Struct): MutableProblogTheory = assertA(Fact.of(struct))

    override fun assertZ(struct: Struct): MutableProblogTheory = assertZ(Fact.of(struct))

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

    override fun retract(head: Struct): RetractResult<MutableProblogTheory> =
        retract(Rule.of(head, Var.anonymous()))

    override fun retract(clause: Clause): RetractResult<MutableProblogTheory> {
        return retract(listOf(clause))
    }

    override fun retract(clauses: Sequence<Clause>): RetractResult<MutableProblogTheory> {
        return retract(clauses.asIterable())
    }

    override fun retractAll(head: Struct): RetractResult<MutableProblogTheory> =
        retractAll(Rule.of(head, Var.anonymous()))
}
