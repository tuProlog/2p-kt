package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.PrologExpressionVisitor
import it.unibo.tuprolog.core.parsing.PrologParserFactory
import it.unibo.tuprolog.theory.ClauseDatabase

class ClauseDatabaseParserImpl(override val defaultOperatorSet: OperatorSet) : ClauseDatabaseParser {

    override fun ClauseDatabase.Companion.parse(input: String, operators: OperatorSet): ClauseDatabase {
        val clauseDatabase = ClauseDatabase.empty()
        PrologParserFactory.instance.parseClauses(input, operators).forEach {
            clauseDatabase.plus(it.accept(PrologExpressionVisitor.instance))
        }
        return clauseDatabase
    }
}