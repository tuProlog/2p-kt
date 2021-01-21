package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.primitive.Solve.Request
import it.unibo.tuprolog.solve.primitive.Solve.Response
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import kotlin.reflect.KClass

object Cast : TernaryRelation<ExecutionContext>("cast") {
    override fun Request<ExecutionContext>.computeAll(first: Term, second: Term, third: Term): Sequence<Response> {
        return when (second) {
            is Struct -> {
                val type = getArgumentAsTypeRef(1)
                catchingOopExceptions {
                    sequenceOf(replyWith(cast(first, type?.type, third)))
                }
            }
            is Var -> {
                termToObjectConverter.admissibleTypes(first)
                    .asSequence()
                    .map { cast(first, it, third) + (second mguWith TypeRef.of(it)) }
                    .map { replyWith(it) }
            }
            else -> {
                ensuringArgumentIsStruct(1)
                sequenceOf(replyFail())
            }
        }
    }

    private fun Request<ExecutionContext>.cast(term: Term, type: KClass<*>?, result: Term): Substitution =
        if (type == null) {
            Substitution.failed()
        } else {
            val casted = termToObjectConverter.convertInto(type, term)
            casted?.let { result mguWith ObjectRef.of(it) } ?: Substitution.failed()
        }
}
