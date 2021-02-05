package it.unibo.tuprolog.solve.problog.lib.knowledge

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution

/**
 * This class wraps a [ProbExplanation] instance into a Prolog term
 * so that it can be used inside Prolog resolutions. This allows for proper leveraging
 * of the multi-paradigm nature of tuProlog.
 *
 * @author Jason Dellaluce
 */
internal class ProbExplanationTerm(
    val explanation: ProbExplanation
) : Atom by Atom.of("<expl:$explanation>") {

    override val isConstant: Boolean
        get() = false

    override fun freshCopy(): ProbExplanationTerm {
        return ProbExplanationTerm(explanation.apply { it.freshCopy() })
    }

    override fun freshCopy(scope: Scope): ProbExplanationTerm {
        return ProbExplanationTerm(explanation.apply { it.freshCopy(scope) })
    }

    override fun get(substitution: Substitution, vararg substitutions: Substitution): ProbExplanationTerm {
        return this.apply(substitution, *substitutions)
    }

    override fun apply(substitution: Substitution, vararg substitutions: Substitution): ProbExplanationTerm {
        return this.apply(Substitution.of(substitution, *substitutions))
    }

    override fun apply(substitution: Substitution): ProbExplanationTerm {
        return if (substitution.isEmpty() || isGround || !explanation.containsAnyVariable(substitution.keys)) {
            this
        } else {
            ProbExplanationTerm(explanation.apply { it.apply(substitution) })
        }
    }

    override val isGround: Boolean
        get() = !explanation.containsAnyNotGroundTerm
}
