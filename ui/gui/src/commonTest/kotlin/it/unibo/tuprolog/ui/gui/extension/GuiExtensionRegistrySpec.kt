package it.unibo.tuprolog.ui.gui.extension

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.ui.gui.identity.CommandId
import it.unibo.tuprolog.ui.gui.identity.ExtensionId
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.presentation.FeatureDescriptor
import it.unibo.tuprolog.ui.gui.presentation.FeaturePlacement
import it.unibo.tuprolog.ui.gui.presentation.SemanticRegion
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverProfile
import it.unibo.tuprolog.ui.gui.solver.SolverSessionFactory

private fun extension(
    id: String,
    contributions: GuiContributions = GuiContributions(),
) = object : GuiExtension {
    override val id = ExtensionId(id)
    override val contributions = contributions
}

private fun profile(id: String) =
    SolverProfile(
        id = SolverProfileId(id),
        displayName = id,
        capabilities = SolverCapabilities.EMPTY,
        factory = SolverSessionFactory { error("not used") },
    )

class GuiExtensionRegistrySpec :
    FunSpec({
        test("rejects-two-extensions-with-the-same-id") {
            shouldThrow<IllegalArgumentException> {
                GuiExtensionRegistry(listOf(extension("a"), extension("a")))
            }
        }

        test("rejects-two-extensions-contributing-the-same-solver-profile") {
            val contributions = GuiContributions(solverProfiles = listOf(profile("shared")))
            shouldThrow<IllegalArgumentException> {
                GuiExtensionRegistry(listOf(extension("a", contributions), extension("b", contributions)))
            }
        }

        test("rejects-two-extensions-contributing-the-same-feature-or-command") {
            val feature = FeatureDescriptor(FeatureId("f"), "F", FeaturePlacement(SemanticRegion.RESULTS))
            shouldThrow<IllegalArgumentException> {
                GuiExtensionRegistry(
                    listOf(
                        extension("a", GuiContributions(features = listOf(feature))),
                        extension("b", GuiContributions(features = listOf(feature))),
                    ),
                )
            }
            val command = CommandDescriptor(CommandId("c"), "C")
            shouldThrow<IllegalArgumentException> {
                GuiExtensionRegistry(
                    listOf(
                        extension("a", GuiContributions(commands = listOf(command))),
                        extension("b", GuiContributions(commands = listOf(command))),
                    ),
                )
            }
        }

        test("find-returns-the-extension-by-id-or-null") {
            val registry = GuiExtensionRegistry(listOf(extension("a")))
            registry.find(ExtensionId("a"))?.id shouldBe ExtensionId("a")
            registry.find(ExtensionId("missing")).shouldBeNull()
        }
    })
