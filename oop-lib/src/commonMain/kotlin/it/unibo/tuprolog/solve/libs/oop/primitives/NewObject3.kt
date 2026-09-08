package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

/**
 * `new_object(+Type, +Arguments, ?ObjectRef)`: constructs a new instance of `Type` (a
 * [it.unibo.tuprolog.solve.libs.oop.TypeRef], type-name atom, or `$Alias` expression) by invoking
 * whichever public constructor best matches the Prolog list `Arguments`, unifying `ObjectRef` with
 * an [it.unibo.tuprolog.solve.libs.oop.ObjectRef] wrapping the freshly created instance. See
 * [it.unibo.tuprolog.solve.libs.oop.rules.NewObject2] for the no-arguments `new_object/2` sugar.
 *
 * Example: `new_object('java.util.ArrayList', [], L)` creates an empty `ArrayList`.
 *
 * Fails (rather than throwing) if `Type` is an atom naming a type that cannot be resolved.
 *
 * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.ConstructorInvocationException if `Type`
 * resolves to a type with no public constructor accepting `Arguments`.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if `Type` is neither a
 * [it.unibo.tuprolog.solve.libs.oop.TypeRef], a type-name atom, nor a `$Alias` expression, or
 * `Arguments` is not a list.
 */
object NewObject3 : TernaryRelation.Functional<ExecutionContext>("new_object") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term,
    ): Substitution {
        ensuringArgumentIsStruct(0)
        ensuringArgumentIsList(1)
        return catchingOopExceptions {
            val type = getArgumentAsTypeRef(0)
            val arguments = (second as List).toArray()
            val objectReference = type?.create(termToObjectConverter, *arguments)?.asObjectRef()
            objectReference?.let { mgu(third, it) } ?: Substitution.failed()
        }
    }
}
