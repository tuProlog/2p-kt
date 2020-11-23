package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.name
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object FindType : BinaryRelation.Functional<ExecutionContext>("find_type") {

    private val typeFactory = TypeFactory.default

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term
    ): Substitution = catchingOopExceptions {
        when {
            first is Var && second is Var -> {
                ensuringArgumentIsInstantiated(0)
                Substitution.failed()
            }
            first is Var -> {
                ensuringArgumentIsAtom(1)
                ensuringArgumentIsTypeRef(1)
                first mguWith Atom.of((second as TypeRef).type.name)
            }
            second is Var -> {
                ensuringArgumentIsAtom(0)
                typeFactory.typeRefFromName((first as Atom).value)?.mguWith(second) ?: Substitution.failed()
            }
            else -> {
                ensuringArgumentIsAtom(0)
                Substitution.failed()
            }
        }
    }
}
