package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * A class implementing a visitor that will evaluate expression terms according to context loaded functions
 *
 * No additional check is implemented at this level
 *
 * @param context the context in which the evaluation should happen
 *
 * @author Enrico
 */
class ExpressionEvaluator(private val context: ExecutionContext) : AbstractEvaluator<Term>(context)
