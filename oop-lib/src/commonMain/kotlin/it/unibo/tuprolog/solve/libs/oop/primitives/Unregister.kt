package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * `unregister(+Alias)`: removes every [it.unibo.tuprolog.solve.libs.oop.rules.Alias] fact
 * matching the ground alias term `Alias` from the current solver's static knowledge base,
 * undoing a previous [Register].
 *
 * Fails if `Alias` is not currently registered.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if `Alias` is unbound or not ground.
 */
object Unregister : UnaryPredicate.NonBacktrackable<ExecutionContext>("unregister") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsStruct(0)
        if (!first.isGround) {
            throw InstantiationError.forArgument(context, signature, first.variables.first(), 0)
        }
        val pattern = Struct.of(Alias.FUNCTOR, first, Var.anonymous())
        val toBeRemoved = context.staticKb[pattern].filter { it.head[0] == first }.toList()
        return if (toBeRemoved.isEmpty()) {
            replyFail()
        } else {
            replySuccess {
                removeStaticClauses(toBeRemoved)
            }
        }
    }
}
