package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object NewObject3 : TernaryRelation.Functional<ExecutionContext>("new_object") {

    private val typeFactory = TypeFactory.default

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term
    ): Substitution {
        ensuringArgumentIsStruct(0)
        ensuringArgumentIsList(1)

        return catchingOopExceptions {
            val arguments = (second as List).toArray()

            val type = when {
                first is TypeRef -> {
                    first
                }
                first is Atom -> {
                    typeFactory.typeRefFromName(first.value)
                }
                first.isDealiasingExpression -> {
                    findRefFromAlias(first as Struct) as? TypeRef
                }
                else -> {
                    ensuringArgumentIsTypeRef(0)
                    return Substitution.failed()
                }
            }

            val objectReference = type?.create(*arguments)?.asObjectRef()

            objectReference?.mguWith(third) ?: Substitution.failed()
        }
    }
}
