package it.unibo.tuprolog.ui.gui.application

import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverProfile
import it.unibo.tuprolog.ui.gui.solver.SolverSessionFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private fun profile(id: String) =
    SolverProfile(
        id = SolverProfileId(id),
        displayName = id,
        capabilities = SolverCapabilities.EMPTY,
        factory = SolverSessionFactory { error("not used") },
    )

class GuiApplicationBuilderTest {
    @Test
    fun rejectsBuildingWithoutAnySolverProfile() {
        assertFailsWith<IllegalArgumentException> { GuiApplicationBuilder().buildConfiguration() }
    }

    @Test
    fun rejectsDuplicateSolverProfileIds() {
        assertFailsWith<IllegalArgumentException> {
            GuiApplicationBuilder()
                .solverProfile(profile("a"))
                .solverProfile(profile("a"))
                .buildConfiguration()
        }
    }

    @Test
    fun rejectsAnUnknownExplicitDefaultProfile() {
        assertFailsWith<IllegalArgumentException> {
            GuiApplicationBuilder()
                .solverProfile(profile("a"))
                .defaultSolverProfile(SolverProfileId("missing"))
                .buildConfiguration()
        }
    }

    @Test
    fun theFirstRegisteredProfileBecomesTheDefaultWhenNoneIsMarked() {
        val configuration = GuiApplicationBuilder().solverProfile(profile("a")).buildConfiguration()
        assertEquals(SolverProfileId("a"), configuration.workspace.defaultSolverProfileId)
    }

    @Test
    fun rejectsANonPositiveDefaultTimeout() {
        assertFailsWith<IllegalArgumentException> {
            GuiApplicationBuilder().defaultTimeout(kotlin.time.Duration.ZERO)
        }
    }
}
