package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.ClausesParser

/**
 * Non-ISO, tuProlog-specific predicate: `set_theory/1` parses the atom argument as Prolog source text (using
 * [it.unibo.tuprolog.theory.parsing.ClausesParser] with the context's current operators) and *replaces* the
 * solver's static and dynamic knowledge base, operators and flags with what that text defines. See [setTheory] for
 * the shared implementation (also used, with `append = true`, by [Consult] to load a theory fetched from a
 * [it.unibo.tuprolog.solve.libs.io.Url]).
 *
 * ```prolog
 * ?- set_theory('parent(tom, bob). parent(bob, ann).').
 * ```
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if it is bound but not an atom.
 * @throws it.unibo.tuprolog.solve.exception.error.SyntaxError if it is not well-formed Prolog source text.
 */
object SetTheory : UnaryPredicate.NonBacktrackable<ExecutionContext>("set_theory") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsAtom(0)
        return setTheory(first.castTo<Atom>().value, append = false)
    }

    /**
     * Parses [text] as Prolog source, then either [append]s its clauses/operators/flags to the solver's current
     * ones (used by [Consult]) or, if not [append], replaces them wholesale (used by [SetTheory] itself, which
     * always calls this with `append = false`).
     * @throws it.unibo.tuprolog.solve.exception.error.SyntaxError if [text] is not well-formed Prolog source text.
     */
    fun Solve.Request<ExecutionContext>.setTheory(
        text: String,
        append: Boolean = true,
    ): Solve.Response {
        try {
            val theory = ClausesParser.withOperators(context.operators).parseTheory(text)
            val solver =
                context.createMutableSolver(
                    staticKb = Theory.empty(context.unificator),
                    dynamicKb = MutableTheory.empty(context.unificator),
                )
            solver.loadStaticKb(theory)
            return replySuccess {
                if (append) {
                    addStaticClauses(solver.staticKb)
                    addDynamicClauses(solver.dynamicKb)
                } else {
                    resetStaticKb(solver.staticKb)
                    resetDynamicKb(solver.dynamicKb)
                }
                setOperators(solver.operators)
                setFlags(solver.flags)
            }
        } catch (e: ParseException) {
            throw SyntaxError.whileParsingClauses(
                context,
                text,
                e.clauseIndex,
                e.line,
                e.column,
                e.message
                    ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    ?: "<no detail provided>",
            )
        }
    }
}
