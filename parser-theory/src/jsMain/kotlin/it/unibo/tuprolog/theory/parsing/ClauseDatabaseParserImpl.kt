package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.PrologParserFactory
import it.unibo.tuprolog.core.parsing.PrologVisitor
import it.unibo.tuprolog.theory.ClauseDatabase

class ClauseDatabaseParserImpl(override val defaultOperatorSet: OperatorSet) : ClauseDatabaseParser {

    override fun parseClauseDatabase(input: String, operators: OperatorSet): ClauseDatabase {
        return PrologParserFactory.parseClauses(input, operators)
            .asSequence()
            .map { it.accept<Term>(PrologVisitor()) }
            .map { it as Clause }
            .let { ClauseDatabase.of(it) }
    }
}