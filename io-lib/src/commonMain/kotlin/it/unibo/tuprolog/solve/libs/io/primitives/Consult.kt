package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.Url
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import it.unibo.tuprolog.solve.libs.io.primitives.SetTheory.setTheory
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Consult : UnaryPredicate.NonBacktrackable<ExecutionContext>("consult") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsInstantiated(0)
        ensuringArgumentIsAtom(0)
        val urlString = (first as Atom).value
        try {
            val url = Url.of(urlString)
            val text = url.readAsText()
            return setTheory(text)
        } catch (e: InvalidUrlException) {
            throw e.toLogicError(context, signature, first, 0)
        } catch (e: IOException) {
            throw e.toLogicError(context)
        }
    }
}
