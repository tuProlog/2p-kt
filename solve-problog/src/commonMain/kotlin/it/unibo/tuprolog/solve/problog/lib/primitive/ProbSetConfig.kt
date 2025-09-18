package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.isProbabilistic
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetConfig.toSolveOptions
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.utils.setTag

internal object ProbSetConfig : UnaryPredicate.NonBacktrackable<ExecutionContext>(
    "${ProblogLib.PREDICATE_PREFIX}_set_config",
) {
    private val CONFIG_FLAG = "${this.functor}_flag"
    private val CONFIG_TERM_NAME = "${this.functor}_term"
    private val CONFIG_TERM_TAG = "it.unibo.tuprolog.solve.probability${this.functor}_tag"

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsInstantiated(0)
        ensuringArgumentIsAtom(0)
        return replySuccess(sideEffects = arrayOf(SideEffect.SetFlags(Pair(CONFIG_FLAG, first))))
    }

    fun Term.toSolveOptions(): SolveOptions = this.getTag(CONFIG_TERM_TAG) ?: SolveOptions.DEFAULT

    fun SolveOptions.toProbConfigTerm(): Term = Atom.of(CONFIG_TERM_NAME).setTag(CONFIG_TERM_TAG, this)

    fun ExecutionContext.getSolverOptions(): SolveOptions =
        this.flags[CONFIG_FLAG]?.toSolveOptions() ?: SolveOptions.DEFAULT

    fun ExecutionContext.isPrologMode(): Boolean = !this.getSolverOptions().isProbabilistic
}
