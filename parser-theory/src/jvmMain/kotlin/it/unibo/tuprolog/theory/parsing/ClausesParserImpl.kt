package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.PrologExpressionVisitor
import it.unibo.tuprolog.core.parsing.PrologParserFactory
import it.unibo.tuprolog.core.parsing.toClause

internal class ClausesParserImpl(
    override val defaultOperatorSet: OperatorSet,
) : ClausesParser {
    override fun parseClausesLazily(
        input: String,
        operators: OperatorSet,
    ): Sequence<Clause> =
        PrologParserFactory
            .parseClauses(input, operators)
            .asSequence()
            .map { it.accept(PrologExpressionVisitor()) }
            .map { it.toClause() }
}
