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
    override val help: String =
        """
        `consult(+Source)`
        
        Loads the Prolog theory found at `Source`, an atom holding either a URL (e.g. `'https://example.com/theory.pl'`) or a plain absolute or relative file path, parsing it with the current operators. This tuProlog-specific predicate adds the loaded clauses to the knowledge base without removing any existing clause (so consulting the same source twice duplicates its clauses), with clauses of predicates declared dynamic going to the dynamic knowledge base and all others to the static one; operators and flags set by the theory's directives take effect as well. It raises an instantiation error if `Source` is unbound, a type error (`atom`) if it is not an atom, a type error (`url`) if it is not a valid URL or path, a system error if the resource cannot be read, and a syntax error if its content is not a well-formed theory.

        **Examples**

        ```prolog
        % with /path/to/family.pl containing `parent(tom, bob).`
        ?- consult('/path/to/family.pl'), parent(tom, X).
        X = bob.

        ?- consult(F).
        throws error(instantiation_error, _).

        ?- consult(42).
        throws error(type_error(atom, 42), _).

        ?- consult('/no/such/theory.pl').
        throws error(system_error, _).
        ```
        """.trimIndent()

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
