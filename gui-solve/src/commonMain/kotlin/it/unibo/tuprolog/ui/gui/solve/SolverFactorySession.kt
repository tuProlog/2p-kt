package it.unibo.tuprolog.ui.gui.solve

import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.core.parsing.parseAsTerm
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.flags.TrackVariables.ON
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.libs.oop.OOPLib
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot
import it.unibo.tuprolog.ui.gui.presentation.WarningPresentation
import it.unibo.tuprolog.ui.gui.solver.ResolutionCursor
import it.unibo.tuprolog.ui.gui.solver.ResolutionRequest
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverSession
import it.unibo.tuprolog.ui.gui.solver.SolverSessionCreationRequest
import it.unibo.tuprolog.ui.gui.solver.SolverSignal
import kotlinx.coroutines.yield

/** [SolverSession] backed by any [SolverFactory]; no toolkit dependency. */
internal class SolverFactorySession(
    private val factory: SolverFactory,
    private val creationRequest: SolverSessionCreationRequest,
    capabilities: Set<String>,
    private val solutionFeatures: (Solution) -> Map<FeatureId, Map<String, FeatureValue>>,
) : SolverSession {
    // ponytail: unsynchronized signal buffer. Callbacks fire synchronously within the driving
    // coroutine's own call stack (never a genuinely concurrent thread), so a lock isn't needed here;
    // revisit with a kotlinx.atomicfu-backed queue if a solver adapter starts calling back cross-thread.
    private val pendingSignals = mutableListOf<SolverSignal>()
    private var solver = newSolver()

    override val id = SolverSessionId("solve-${creationRequest.pageId.value}-${creationRequest.documentRevision}")
    override val capabilities = SolverCapabilities(capabilities)
    override val snapshot: SolverInspectionSnapshot
        get() = solver.inspectionSnapshot()

    override suspend fun openResolution(request: ResolutionRequest): ResolutionCursor {
        val query =
            request.query
                .trim()
                .removeSuffix(".")
                .parseAsStruct(solver.operators)
        val options =
            if (request.timeout.inWholeMilliseconds == 0L) {
                SolveOptions.allLazily()
            } else {
                SolveOptions.allLazilyWithTimeout(request.timeout.inWholeMilliseconds)
            }
        val solutions = solver.solve(query, options).iterator()
        return object : ResolutionCursor {
            override suspend fun next(): ResolutionStep {
                yield()
                return if (!solutions.hasNext()) {
                    ResolutionStep.End(signals = drainSignals())
                } else {
                    val solution = solutions.next()
                    solution.toStep(request.query, drainSignals(), solutionFeatures(solution))
                }
            }

            override suspend fun cancel() = Unit
        }
    }

    override suspend fun reset(): SolverInspectionSnapshot {
        solver = newSolver()
        return snapshot
    }

    override suspend fun close() = Unit

    private fun newSolver(): MutableSolver {
        var builder =
            factory
                .newBuilder()
                .runtime(Runtime.of(OOPLib, IOLib))
                .flag(TrackVariables) { ON }
                .standardInput(InputChannel.of(creationRequest.stdin))
                .standardOutput(OutputChannel.of { signal(SolverSignal.Stdout(it)) })
                .standardError(OutputChannel.of { signal(SolverSignal.Stderr(it)) })
                .warnings(
                    OutputChannel.of {
                        signal(
                            SolverSignal.Warning(
                                WarningPresentation(
                                    it.message.orEmpty(),
                                    it.logicStackTrace.map(Any::toString),
                                ),
                            ),
                        )
                    },
                )
        for ((name, value) in creationRequest.options) {
            builder = runCatching { builder.flag(name to value.parseAsTerm()) }.getOrDefault(builder)
        }
        return builder
            .buildMutable()
            .also { it.loadStaticKb(creationRequest.sourceText.parseAsTheory(it.operators)) }
    }

    private fun signal(signal: SolverSignal) {
        pendingSignals += signal
    }

    private fun drainSignals(): List<SolverSignal> =
        (pendingSignals.toList() + SolverSignal.Inspection(snapshot)).also { pendingSignals.clear() }
}
