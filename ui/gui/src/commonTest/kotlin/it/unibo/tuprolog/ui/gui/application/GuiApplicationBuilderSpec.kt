package it.unibo.tuprolog.ui.gui.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverProfile
import it.unibo.tuprolog.ui.gui.solver.SolverSessionFactory

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
            shouldThrow<IllegalArgumentException> { GuiApplicationBuilder().buildConfiguration() }
        }

        test("rejects-duplicate-solver-profile-ids") {
            shouldThrow<IllegalArgumentException> {
                GuiApplicationBuilder()
                    .solverProfile(profile("a"))
                    .solverProfile(profile("a"))
                    .buildConfiguration()
            }
        }

        test("rejects-an-unknown-explicit-default-profile") {
            shouldThrow<IllegalArgumentException> {
                GuiApplicationBuilder()
                    .solverProfile(profile("a"))
                    .defaultSolverProfile(SolverProfileId("missing"))
                    .buildConfiguration()
            }
        }

        test("the-first-registered-profile-becomes-the-default-when-none-is-marked") {
            val configuration = GuiApplicationBuilder().solverProfile(profile("a")).buildConfiguration()
            configuration.workspace.defaultSolverProfileId shouldBe SolverProfileId("a")
        }

        test("rejects-a-non-positive-default-timeout") {
            shouldThrow<IllegalArgumentException> {
                GuiApplicationBuilder().defaultTimeout(kotlin.time.Duration.ZERO)
            }
        }
    })
