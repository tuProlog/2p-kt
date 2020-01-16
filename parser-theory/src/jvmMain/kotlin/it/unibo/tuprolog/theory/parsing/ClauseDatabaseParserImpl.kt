package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.ClauseDatabase

class ClauseDatabaseParserImpl(override val defaultOperatorSet: OperatorSet) : ClauseDatabaseParser {
    override fun ClauseDatabase.Companion.parse(input: String, operators: OperatorSet): ClauseDatabase {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}