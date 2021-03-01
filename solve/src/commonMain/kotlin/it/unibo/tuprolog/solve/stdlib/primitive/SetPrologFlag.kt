package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object SetPrologFlag : BinaryRelation.NonBacktrackable<ExecutionContext>("set_prolog_flag") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsAtom(0)
        ensuringArgumentIsGround(1)
        val name = (first as Atom).value
        NotableFlag.fromName(name)?.let {
            if (second !in it.admissibleValues) {
                throw DomainError.forFlagValues(context, signature, it.admissibleValues.asIterable(), second, 1)
            }
        }
        return replySuccess {
            setFlag(name, second)
        }
    }
}
