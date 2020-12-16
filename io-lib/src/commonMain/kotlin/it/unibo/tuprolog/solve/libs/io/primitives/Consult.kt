package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.libs.io.Url
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.ClausesParser
import it.unibo.tuprolog.unify.Unificator.Companion.matches

object Consult : UnaryPredicate.NonBacktrackable<ExecutionContext>("consult") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsInstantiated(0)
        ensuringArgumentIsAtom(0)
        val urlString = (first as Atom).value
        try {
            val url = Url.of(urlString)
            val text = url.readAsText()
            val theory = ClausesParser.withOperators(context.operators).parseTheory(text)
            val operators = theory.extractOperators()
            return replySuccess {
                addStaticClauses(theory)
                setOperators(context.operators + operators)
            }
        } catch (e: InvalidUrlException) {
            throw e.toPrologError(context, signature, first, 0)
        } catch (e: IOException) {
            throw e.toPrologError(context)
        } catch (e: ParseException) {
            throw SyntaxError.whileParsingClauses(
                context,
                e.input?.toString() ?: "<no input>",
                e.clauseIndex,
                e.line,
                e.column,
                e.message?.capitalize() ?: "<no detail provided>"
            )
        }
    }

    private fun Theory.extractOperators(): OperatorSet =
        OperatorSet(
            directives.asSequence()
                .filter { it.body matches Operator.TEMPLATE }
                .map { it[0] }
                .filterIsInstance<Struct>()
                .map { Operator.fromTerm(it) }
                .filterNotNull()
        )
}
