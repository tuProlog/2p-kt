package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.allSupertypes
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import it.unibo.tuprolog.solve.primitive.Solve.Request
import it.unibo.tuprolog.solve.primitive.Solve.Response
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import kotlin.reflect.KClass

/**
 * `cast(?Term, ?Type, ?ObjectRef)`: converts `Term` into an instance of `Type` (a
 * [it.unibo.tuprolog.solve.libs.oop.TypeRef], a type-name atom, or a `$Alias` expression
 * resolving to a [it.unibo.tuprolog.solve.libs.oop.TypeRef]), unifying `ObjectRef` with an
 * [it.unibo.tuprolog.solve.libs.oop.ObjectRef] wrapping the result. Backs the `as`/2 operator
 * (`X as 'java.lang.Long'`), used to disambiguate overload resolution when the automatically
 * inferred type is not the intended one -- e.g. forcing an integer literal to be converted to a
 * `Long` rather than the default `Int`.
 *
 * When `Type` is left unbound, this predicate backtracks over every type `Term` could be
 * converted into (see [it.unibo.tuprolog.solve.libs.oop.TermToObjectConverter.admissibleTypes])
 * and all of their supertypes, binding both `Type` and `ObjectRef` on each solution.
 *
 * Fails (rather than throwing) if `Term` cannot be converted into an instance of `Type`.
 */
object Cast : TernaryRelation<ExecutionContext>("cast") {
    override fun Request<ExecutionContext>.computeAll(
        first: Term,
        second: Term,
        third: Term,
    ): Sequence<Response> =
        catchingOopExceptions {
            when (second) {
                is Struct -> {
                    ensuringArgumentIsTypeRef(1)
                    val type = getArgumentAsTypeRef(1)
                    try {
                        sequenceOf(replyWith(cast(first, type?.type, third)))
                    } catch (_: TermToObjectConversionException) {
                        sequenceOf(replyFail())
                    }
                }
                is Var -> {
                    termToObjectConverter
                        .admissibleTypes(first)
                        .asSequence()
                        .flatMap { it.allSupertypes(false) }
                        .distinct()
                        .map { cast(first, it, third) + mgu(second, TypeRef.of(it)) }
                        .map { replyWith(it) }
                }
                else -> {
                    ensuringArgumentIsTypeRef(1)
                    sequenceOf(replyFail())
                }
            }
        }

    private fun Request<ExecutionContext>.cast(
        term: Term,
        type: KClass<*>?,
        result: Term,
    ): Substitution =
        if (type == null) {
            Substitution.failed()
        } else {
            val casted = termToObjectConverter.convertInto(type, term)
            mgu(result, ObjectRef.of(casted))
        }
}
