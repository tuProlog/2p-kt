package it.unibo.tuprolog.solve.problog.lib.knowledge

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Constant
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
    val explanation: ProbExplanation,
) : Atom by Atom.of("<expl:$explanation>") {
    override val isConstant: Boolean
        get() = false

    override fun asConstant(): Constant = this

    override fun freshCopy(): ProbExplanationTerm = ProbExplanationTerm(explanation.apply { it.freshCopy() })

    override fun freshCopy(scope: Scope): ProbExplanationTerm =
        ProbExplanationTerm(
            explanation.apply {
                it.freshCopy(scope)
            },
        )

    override fun get(
        substitution: Substitution,
        vararg substitutions: Substitution,
    ): ProbExplanationTerm = this.apply(substitution, *substitutions)

    override fun apply(
        substitution: Substitution,
        vararg substitutions: Substitution,
    ): ProbExplanationTerm = this.apply(Substitution.of(substitution, *substitutions))

    override fun apply(substitution: Substitution): ProbExplanationTerm =
        if (substitution.isEmpty() || isGround) {
            this
        } else {
            ProbExplanationTerm(explanation.apply { it.apply(substitution) })
        }

    override val isGround: Boolean
        get() = !explanation.containsAnyNotGroundTerm
}
