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

object SetTheory : UnaryPredicate.NonBacktrackable<ExecutionContext>("set_theory") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsAtom(0)
        return setTheory(first.castTo<Atom>().value, append = false)
    }

    fun Solve.Request<ExecutionContext>.setTheory(text: String, append: Boolean = true): Solve.Response {
        try {
            val theory = ClausesParser.withOperators(context.operators).parseTheory(text)
            val solver = context.createMutableSolver(
                staticKb = Theory.empty(context.unificator),
                dynamicKb = MutableTheory.empty(context.unificator)
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
                e.message?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: "<no detail provided>"
            )
        }
    }
}
