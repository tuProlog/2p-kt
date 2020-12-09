package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.libs.io.Url
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Consult : UnaryPredicate<ExecutionContext>("consult") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> {
        ensuringArgumentIsAtom(0)
        val urlString = (first as Atom).value
        try {
            val url = Url.of(urlString)
            return sequence {
                url.readAsTextAsync { text, error ->
                    when {
                        error != null -> yield(replyException(ExistenceError.forResource(context, url.toString())))
                    }
                }
            }
        } catch (e: InvalidUrlException) {
            throw e.toPrologError(context, signature, first, 0)
        }
    }
}