package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.presentation.WarningPresentation
import it.unibo.tuprolog.ui.gui.solver.ResolutionCursor
import it.unibo.tuprolog.ui.gui.solver.ResolutionRequest
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep
import it.unibo.tuprolog.ui.gui.solver.SolverSignal
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

internal class TestResolutionCursor(
    private val request: ResolutionRequest,
) : ResolutionCursor {
    private var index = 0
    private var cancelled = false

    override suspend fun next(): ResolutionStep {
        val step = index++
        return when (request.query) {
            "p(X)." ->
                when (step) {
                    0 ->
                        ResolutionStep.Yield(
                            solution = yes("X", "1"),
                            hasMorePotentially = true,
                        )
                    1 ->
                        ResolutionStep.Yield(
                            solution = yes("X", "2"),
                            hasMorePotentially = false,
                        )
                    else -> ResolutionStep.End()
                }
            "q(Y)." ->
                if (step == 0) {
                    ResolutionStep.Yield(yes("Y", "a"), hasMorePotentially = false)
                } else {
                    ResolutionStep.End()
                }
            "output." ->
                if (step == 0) {
                    ResolutionStep.Yield(
                        solution = SolutionPresentation.Yes(request.query),
                        hasMorePotentially = false,
                        signals =
                            listOf(
                                SolverSignal.Stdout("hello\n"),
                                SolverSignal.Warning(WarningPresentation("careful")),
                            ),
                    )
                } else {
                    ResolutionStep.End()
                }
            "slow(X)." -> {
                // Deliberately ignores cooperative cancellation while sleeping. The controller must still
                // reject this result after Stop, source invalidation, or a newer resolution.
                withContext(NonCancellable) { delay(150) }
                ResolutionStep.Yield(yes("X", "stale"), hasMorePotentially = false)
            }
            "slowFail(X)." -> {
                // Deliberately throws after cancellation to verify terminal-state protection.
                withContext(NonCancellable) { delay(150) }
                error("late non-cooperative failure")
            }
            else ->
                if (step == 0) {
                    ResolutionStep.Yield(SolutionPresentation.No(request.query), hasMorePotentially = false)
                } else {
                    ResolutionStep.End()
                }
        }
    }

    override suspend fun cancel() {
        cancelled = true
    }

    private fun yes(
        variable: String,
        value: String,
    ): SolutionPresentation.Yes =
        SolutionPresentation.Yes(
            query = request.query,
            bindings = listOf(BindingPresentation(variable, value)),
        )
}
