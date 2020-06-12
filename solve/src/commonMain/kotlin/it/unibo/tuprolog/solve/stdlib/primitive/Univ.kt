package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

/**
 * Implementation of '=..'/2 predicate
 */
object Univ : TermRelation<ExecutionContext>("=..") {
    override fun Solve.Request<ExecutionContext>.computeSingleResponse(): Solve.Response {
        val functor = arguments[0]
        val list = arguments[1]
        if (functor is Struct && list is Var)
            return replySuccess(Substitution.of(list,
                List.of(listOf(Atom.of(functor.functor)) + functor.argsList)))
        if (functor is Var && list is List && list.size == 0)
            return replyFail()
        if (functor is Var && list is List)
            return replySuccess(Substitution.of(functor,
                Struct.of(
                    (list.toList().take(1).first() as Atom).value,
                    list.toList().drop(1))))
        return replyFail()
    }
}
