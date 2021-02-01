package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.sideffects.SideEffect

internal object ProbSetMode : UnaryPredicate.NonBacktrackable<ExecutionContext>(
    "${ProblogLib.PREDICATE_PREFIX}_set_mode") {

    private val MODE_FLAG = "${this.functor}_flag"
    val PrologMode = Atom.of("prolog")
    val ProblogMode = Atom.of("problog")
    private val supportedModes = setOf(PrologMode, ProblogMode)

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsInstantiated(0)
        ensuringArgumentIsAtom(0)
        return if (first !in supportedModes) {
            replyException(TuPrologRuntimeException("Unsupported mode: $first", context = context))
        } else {
            replySuccess(sideEffects = arrayOf(SideEffect.SetFlags(Pair(MODE_FLAG, first))))
        }
    }

    fun ExecutionContext.isPrologMode(): Boolean {
        return this.flags[MODE_FLAG] == PrologMode
    }

    fun ExecutionContext.isProblogMode(): Boolean {
        return this.flags[MODE_FLAG] == ProblogMode
    }
}
