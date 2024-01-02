package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.operators.OperatorSet

actual fun termParserWithOperators(
    operators: OperatorSet,
    scope: Scope,
): TermParser = TermParserImpl(scope, operators)
