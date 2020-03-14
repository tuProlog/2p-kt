package it.unibo.tuprolog.libraries.stdlib.rule

import it.unibo.tuprolog.rule.RuleWrapper
import it.unibo.tuprolog.solve.ExecutionContext

object Cut : RuleWrapper<ExecutionContext>("!", 0)