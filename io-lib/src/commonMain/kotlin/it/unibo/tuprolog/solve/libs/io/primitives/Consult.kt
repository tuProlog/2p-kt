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

/**
 * Non-ISO, tuProlog-specific predicate: `consult/1` fetches the text pointed at by the atom argument (parsed into a
 * [it.unibo.tuprolog.solve.libs.io.Url] via [it.unibo.tuprolog.solve.libs.io.Url.Companion.of], so both proper URLs
 * and bare filesystem paths work) and loads it as a theory, exactly as [SetTheory] does for an inline source string.
 *
 * ```prolog
 * ?- consult('theory.pl').
 * ?- consult('https://example.com/theory.pl').
 * ```
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if it is bound but not an atom.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`url`) if it is an atom that does not parse into a valid [Url].
 * @throws it.unibo.tuprolog.solve.exception.error.SystemError if the resource cannot be fetched (e.g. missing file, unreachable host).
 * @throws it.unibo.tuprolog.solve.exception.error.SyntaxError if the fetched text is not a well-formed theory.
 */
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
