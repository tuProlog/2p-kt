package it.unibo.tuprolog.solve.problog.lib.knowledge

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.any
import it.unibo.tuprolog.bdd.map
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.libs.oop.ObjectRef

class ProblogObjectRef(
    val bdd: BinaryDecisionDiagram<ProbChoice>
) : ObjectRef by ObjectRef.of(bdd) {

    override val isConstant: Boolean
        get() = false

    override fun freshCopy(): ProblogObjectRef {
        return ProblogObjectRef(this.bdd.map { it.freshCopy() })
    }

    override fun freshCopy(scope: Scope): ProblogObjectRef {
        return ProblogObjectRef(this.bdd.map { it.freshCopy(scope) })
    }

    override fun get(substitution: Substitution, vararg substitutions: Substitution): ProblogObjectRef {
        return this.apply(substitution, *substitutions)
    }

    override fun apply(substitution: Substitution, vararg substitutions: Substitution): ProblogObjectRef {
        return this.apply(Substitution.of(substitution, *substitutions))
    }

    override fun apply(substitution: Substitution): ProblogObjectRef {
        return if (substitution.isEmpty() ||
            this.isGround ||
            !this.bdd.any { it.variables.any { v -> substitution.containsKey(v) } }
        ) {
            this
        } else {
            ProblogObjectRef(this.bdd.map { it.apply(substitution) })
        }
    }

    override val isGround: Boolean
        get() = !this.bdd.any {
            !it.isGround
        }
}
