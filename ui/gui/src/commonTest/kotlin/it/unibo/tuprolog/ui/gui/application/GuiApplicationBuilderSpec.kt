package it.unibo.tuprolog.ui.gui.application

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverProfile
import it.unibo.tuprolog.ui.gui.solver.SolverSessionFactory
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private fun profile(id: String) =
    SolverProfile(
        id = SolverProfileId(id),
        displayName = id,
        capabilities = SolverCapabilities.EMPTY,
        factory = SolverSessionFactory { error("not used") },
    )

class GuiApplicationBuilderSpec :
    FunSpec({
        test("rejects-building-without-any-solver-profile") {
            assertFailsWith<IllegalArgumentException> { GuiApplicationBuilder().buildConfiguration() }
        }

        test("rejects-duplicate-solver-profile-ids") {
            assertFailsWith<IllegalArgumentException> {
                GuiApplicationBuilder()
                    .solverProfile(profile("a"))
                    .solverProfile(profile("a"))
                    .buildConfiguration()
            }
        }

        test("rejects-an-unknown-explicit-default-profile") {
            assertFailsWith<IllegalArgumentException> {
                GuiApplicationBuilder()
                    .solverProfile(profile("a"))
                    .defaultSolverProfile(SolverProfileId("missing"))
                    .buildConfiguration()
            }
        }

        test("the-first-registered-profile-becomes-the-default-when-none-is-marked") {
            val configuration = GuiApplicationBuilder().solverProfile(profile("a")).buildConfiguration()
            assertEquals(SolverProfileId("a"), configuration.workspace.defaultSolverProfileId)
        }

        test("rejects-a-non-positive-default-timeout") {
            assertFailsWith<IllegalArgumentException> {
                GuiApplicationBuilder().defaultTimeout(kotlin.time.Duration.ZERO)
            }
        }
    })
