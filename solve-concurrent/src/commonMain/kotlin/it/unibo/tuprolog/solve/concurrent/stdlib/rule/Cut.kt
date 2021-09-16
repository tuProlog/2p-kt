package it.unibo.tuprolog.solve.concurrent.stdlib.rule

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

object Cut : RuleWrapper<ExecutionContext>("!", 0)
