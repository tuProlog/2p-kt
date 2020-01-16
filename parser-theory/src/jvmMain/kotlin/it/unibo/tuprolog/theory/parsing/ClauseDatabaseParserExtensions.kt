package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet

actual fun ClauseDatabaseParser.Companion.withNoOperator(): ClauseDatabaseParser =
    ClauseDatabaseParserImpl(OperatorSet.EMPTY)

actual fun ClauseDatabaseParser.Companion.withStandardOperators(): ClauseDatabaseParser =
    ClauseDatabaseParserImpl(OperatorSet.DEFAULT)

actual fun ClauseDatabaseParser.Companion.withOperators(operators: OperatorSet): ClauseDatabaseParser =
    ClauseDatabaseParserImpl(operators)

actual fun ClauseDatabaseParser.Companion.withOperators(vararg operators: Operator): ClauseDatabaseParser =
    ClauseDatabaseParserImpl(OperatorSet(operators.asSequence()))