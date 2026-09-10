package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

/**
 * `assign(+Ref, +PropertyName, +Value)`: assigns `Value` to the mutable property named
 * `PropertyName` (an [it.unibo.tuprolog.core.Atom]) on `Ref` -- an
 * [it.unibo.tuprolog.solve.libs.oop.ObjectRef], [it.unibo.tuprolog.solve.libs.oop.TypeRef], or
 * `$Alias` expression -- resolving `Value` itself as a `$Alias` expression first, if it looks like
 * one. Succeeds deterministically if the assignment was performed.
 *
 * This is what the fluent `:=`/2 syntax lowers to for property writes (see
 * [it.unibo.tuprolog.solve.libs.oop.rules.ColonEquals.Assignment]): `Obj.name := joe` compiles down
 * to `assign(Obj, name, joe)`.
 *
 * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.PropertyAssignmentException if `Ref` has no
 * mutable property named `PropertyName` accepting a value compatible with `Value`.
 */
object Assign : TernaryRelation.Predicative<ExecutionContext>("assign") {
    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
        third: Term,
    ): Boolean {
        ensuringArgumentIsAtom(0)
        ensuringArgumentIsAtom(1)

        return catchingOopExceptions {
            val ref =
                when (first) {
                    is Ref -> first
                    else -> findRefFromAlias(first as Atom)
                }

            val value =
                if (match(third, DEALIASING_TEMPLATE)) {
                    findRefFromAlias(third as Struct)
                } else {
                    third
                }

            ref.assign(termToObjectConverter, (second as Atom).value, value)
        }
    }
}
