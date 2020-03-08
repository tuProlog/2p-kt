package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.operators.OperatorSet

actual fun clauseDbParserWithOperators(operators: OperatorSet): ClauseDatabaseParser {
    return ClauseDatabaseParserImpl(operators)
}
