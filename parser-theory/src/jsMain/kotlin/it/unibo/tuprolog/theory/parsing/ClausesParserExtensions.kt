package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.operators.OperatorSet

actual fun clausesParserWithOperators(operators: OperatorSet): ClausesParser = ClausesParserImpl(operators)
