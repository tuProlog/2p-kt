package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

/** Base class for implementing relation between possibly not instantiated [Term]s */
abstract class TermRelation<E : ExecutionContext>(operator: String) : BinaryRelation<E>(operator) {

    override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> =
        sequenceOf(request.computeSingleResponse())

    /** Base class for implementing a relation *without side effects* between [Term]s */
    abstract class WithoutSideEffects<E : ExecutionContext>(operator: String) : TermRelation<E>(operator) {

        override fun Solve.Request<E>.computeSingleResponse(): Solve.Response =
            replyWith(relationWithoutSideEffects(arguments[0], arguments[1]))
    }

    /** Base class for implementing a relation *with side effects* between [Term]s */
    abstract class WithSideEffects<E : ExecutionContext>(operator: String) : TermRelation<E>(operator) {

        override fun Solve.Request<E>.computeSingleResponse(): Solve.Response =
            when (val effects: Substitution = relationWithSideEffects(arguments[0], arguments[1])) {
                is Substitution.Unifier -> replySuccess(effects)
                else -> replyFail()
            }
    }
}
