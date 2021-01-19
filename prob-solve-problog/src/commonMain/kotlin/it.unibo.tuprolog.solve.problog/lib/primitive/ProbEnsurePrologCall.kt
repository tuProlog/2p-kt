package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.problog.lib.ProblogLib

/** This primitive is called by predicates that need to solve simple Prolog goals. Usually, those goals
 * cannot be solved due to absence of related predicates in the knowledge base. By first checking that
 * a Prolog predicate is safe to be called, we avoid useless Prolog solver steps and we don't spawn
 * the error/warning channel with "predicate not found" alerts.
 *
 * @author Jason Dellaluce
 * */
internal object ProbEnsurePrologCall : UnaryPredicate.NonBacktrackable<ExecutionContext>(
    "${ProblogLib.PREDICATE_PREFIX}_ensure_call"
) {
    private val ignoredPredicates = listOf("\\+", "not")

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsStruct(0)

        val signature = (first as Struct).extractSignature()
        val shouldCall = signature.name !in ignoredPredicates && (
            first is Truth ||
                signature in context.libraries ||
                signature.toIndicator() in context.staticKb ||
                signature.toIndicator() in context.dynamicKb
            )
        return replyWith(shouldCall)
    }
}
