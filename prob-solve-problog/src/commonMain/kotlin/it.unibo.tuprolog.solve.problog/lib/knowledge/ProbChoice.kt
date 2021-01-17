package it.unibo.tuprolog.solve.problog.lib.knowledge

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import kotlin.math.round

class ProbChoice(
    val id: Long,
    val probability: Double,
    val term: Term
) : Term by Struct.of(ProblogLib.PROB_FUNCTOR, Numeric.of(probability), term) {

    override fun freshCopy(): ProbChoice {
        val termCopy = term.freshCopy()
        return ProbChoice(id, probability, termCopy)
    }

    override fun freshCopy(scope: Scope): ProbChoice {
        val termCopy = term.freshCopy(scope)
        return ProbChoice(id, probability, termCopy)
    }

    override fun compareTo(other: Term): Int {
        if (other is ProbChoice) {
            var res = this.id.compareTo(other.id)
            if (res == 0) {
                res = term.compareTo(other.term)
            }
            return res
        }
        return super.compareTo(other)
    }

    override fun toString(): String {
        return "${round(probability * 100) / 100.0}::$term"
    }

    override fun get(substitution: Substitution, vararg substitutions: Substitution): ProbChoice {
        return this.apply(substitution, *substitutions)
    }

    override fun apply(substitution: Substitution, vararg substitutions: Substitution): ProbChoice {
        return this.apply(Substitution.of(substitution, *substitutions))
    }

    override fun apply(substitution: Substitution): ProbChoice {
        return if (substitution.isEmpty() || this.term.isGround) {
            this
        } else {
            ProbChoice(id, probability, term.apply(substitution))
        }
    }

    override val isGround: Boolean
        get() = term.isGround

    override fun equals(other: Any?): Boolean {
        return if (other is ProbChoice) compareTo(other) == 0 else false
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + probability.hashCode()
        result = 31 * result + term.hashCode()
        return result
    }
}
