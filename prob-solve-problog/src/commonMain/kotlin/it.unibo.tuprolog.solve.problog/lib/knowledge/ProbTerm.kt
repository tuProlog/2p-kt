package it.unibo.tuprolog.solve.problog.lib.knowledge

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import kotlin.math.round

/**
 * This class represents a single probabilistic term that selected in a [ProbExplanation]
 * related to the solutions of a probabilistic query.
 *
 * [id] is a unique numeric identifier assigned to each probabilistic clause. It is useful for knowledge
 * compilation data structures to have a fastly-comparable unique label for optimization purposes and
 * to distinguish one clause from the others.
 *
 * [probability] is a [Term] representing the probability value annotated on a clause. This is
 * supposed to be either an instance of [Numeric] or [Var].
 *
 * [term] is the bare Prolog [Term] contained in an explanation for a given query solution.
 *
 * [extraVariables] is a collection of [Var]s that are unused inside the original clause from which
 * [term] has been extracted, but that are however relevant for probabilistic computation.
 */
internal class ProbTerm (
    val id: Long,
    val probability: Double,
    val term: Term,
    val extraVariables: Set<Var> = emptySet(),
) : Term by Struct.of(ProblogLib.PROB_FUNCTOR, Numeric.of(probability), term) {

    override fun freshCopy(): ProbTerm {
        val termCopy = term.freshCopy()
        return ProbTerm(id, probability, termCopy, extraVariables.map { it.freshCopy() }.toSet())
    }

    override fun freshCopy(scope: Scope): ProbTerm {
        val termCopy = term.freshCopy(scope)
        return ProbTerm(id, probability, termCopy,  extraVariables.map { it.freshCopy(scope) }.toSet())
    }

    override fun get(substitution: Substitution, vararg substitutions: Substitution): ProbTerm {
        return this.apply(substitution, *substitutions)
    }

    override fun apply(substitution: Substitution, vararg substitutions: Substitution): ProbTerm {
        return this.apply(Substitution.of(substitution, *substitutions))
    }

    override fun apply(substitution: Substitution): ProbTerm {
        return if (substitution.isEmpty() || this.isGround) {
            this
        } else {
            val newExtraVars = if (extraVariables.isEmpty())
                extraVariables
            else
                extraVariables
                    .map { substitution.applyTo(it) }
                    .filterIsInstance<Var>()
                    .toSet()
            ProbTerm(id, probability, term.apply(substitution), newExtraVars)
        }
    }

    override val isGround: Boolean by lazy {
        term.isGround && extraVariables.isEmpty()
    }

    private val cachedHashCode: Int by lazy {
        var result = id.hashCode()
        result = 31 * result + probability.hashCode()
        result = 31 * result + term.hashCode()
        result = 31 * result + extraVariables.hashCode()
        result
    }

    override fun compareTo(other: Term): Int {
        if (other !is ProbTerm) {
            return super.compareTo(other)
        }
        var res = this.id.compareTo(other.id)
        if (res != 0) return res

        res = term.compareTo(other.term)
        if (res != 0) return res

        res = if (extraVariables == other.extraVariables) 0 else -1
        if (res != 0) return res

        res = probability.compareTo(other.probability)
        return res
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ProbTerm) compareTo(other) == 0 else false
    }

    override fun hashCode(): Int {
        return cachedHashCode
    }

    override fun toString(): String {
        return "${round(probability * 100) / 100.0}::$term"
    }

}
