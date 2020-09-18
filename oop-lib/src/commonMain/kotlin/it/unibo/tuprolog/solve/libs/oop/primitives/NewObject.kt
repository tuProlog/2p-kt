package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object NewObject : TernaryRelation.Functional<ExecutionContext>("new_object") {

    private val typeFactory = TypeFactory.default

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term
    ): Substitution {
        ensuringArgumentIsAtom(0)
        ensuringArgumentIsList(1)

        val arguments = (second as List).toArray()

        val type = if (first is TypeRef) {
            first
        } else {
            val className = (first as Atom).value
            when (val ref = typeFactory.typeRefFromName(className)) {
                null -> when (val aliased = findRefFromAlias(first)) {
                    null, is ObjectRef -> null
                    else -> aliased as TypeRef
                }
                else -> ref
            }
        }

        val objectReference = type?.create(*arguments)?.asObjectRef()

        return objectReference?.mguWith(third) ?: Substitution.failed()
    }
}
