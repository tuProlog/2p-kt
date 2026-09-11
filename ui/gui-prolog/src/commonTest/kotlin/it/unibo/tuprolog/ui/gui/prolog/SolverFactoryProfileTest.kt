package it.unibo.tuprolog.ui.gui.prolog

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.solver.ResolutionRequest
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverSessionCreationRequest
import it.unibo.tuprolog.ui.gui.solver.SolverSignal
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class SolverFactoryProfileTest {
    @Test
    fun solverProfileExposesAndRefreshesInspectorState() =
        runTest {
            val profile = solverFactoryProfile(Solver.prolog, SolverProfileId("test"), "Test")
            val pageId = PageId("page")
            val session =
                profile.factory.create(
                    SolverSessionCreationRequest(
                        pageId = pageId,
                        documentId = DocumentId("document"),
                        sourceText = "p(a).",
                        documentRevision = 1,
                        profileId = profile.id,
                        options = emptyMap(),
                        stdin = "",
                    ),
                )

            assertTrue(SolverCapabilities.OPERATORS_INSPECTION in session.capabilities)
            assertTrue(session.snapshot.operators.isNotEmpty())
            assertTrue(session.snapshot.flags.isNotEmpty())
            assertTrue(session.snapshot.libraries.isNotEmpty())
            assertTrue(session.snapshot.staticKnowledgeBase.contains("p(a)"))

            val step =
                session.openResolution(ResolutionRequest(pageId, "p(X).", 1.seconds, emptyMap(), "")).next()
            assertIs<ResolutionStep.Yield>(step)
            assertTrue(step.signals.isNotEmpty())

            val outputStep =
                session.openResolution(ResolutionRequest(pageId, "write(hello).", 1.seconds, emptyMap(), "")).next()
            assertTrue(outputStep.signals.filterIsInstance<SolverSignal.Stdout>().any { "hello" in it.text })
        }
}
