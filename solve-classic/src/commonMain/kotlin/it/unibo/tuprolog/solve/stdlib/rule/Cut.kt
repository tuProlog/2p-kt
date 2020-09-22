package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

object Cut : RuleWrapper<ExecutionContext>("!", 0)
