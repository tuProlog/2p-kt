package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverProfile
import it.unibo.tuprolog.ui.gui.solver.SolverSessionCreationRequest

internal class TestSolverFactory(
    defaultOperators: List<OperatorPresentation> = emptyList(),
) {
    private var counter: Int = 0
    val creationRequests = mutableListOf<SolverSessionCreationRequest>()

    val profile =
        SolverProfile(
            id = testProfileId,
            displayName = "Test solver",
            capabilities =
                SolverCapabilities(
                    setOf(
                        SolverCapabilities.CANCELLATION,
                        SolverCapabilities.FLAGS_INSPECTION,
                    ),
                ),
            defaultOperators = defaultOperators,
            factory = { request ->
                creationRequests += request
                TestSolverSession(SolverSessionId("test-session-${++counter}"))
            },
        )
}
