package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

abstract class AbstractGetData(suffix: String) : BinaryRelation<ExecutionContext>("get_$suffix") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term, second: Term): Sequence<Solve.Response> {
        return when (first) {
            is Var ->
                data.entries.asSequence()
                    .filter { (_, v) -> v is Term }
                    .map { (k, v) -> Atom.of(k) to (v as Term) }
                    .map { (k, v) -> mgu(first, k) + mgu(second, v) }
                    .filterIsInstance<Substitution.Unifier>()
                    .map { replyWith(it) } + replyFail()
            is Atom -> when (val value = data[first.value]) {
                is Term -> sequenceOf(replyWith(mgu(second, value)))
                else -> sequenceOf(replyFail())
            }
            else -> {
                ensuringArgumentIsAtom(0)
                emptySequence()
            }
        }
    }

    protected abstract val Solve.Request<ExecutionContext>.data: Map<String, Any>
}
