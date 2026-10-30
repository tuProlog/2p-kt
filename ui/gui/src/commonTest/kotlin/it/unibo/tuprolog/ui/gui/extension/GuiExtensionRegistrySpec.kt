package it.unibo.tuprolog.ui.gui.extension

import io.kotest.core.spec.style.FunSpec
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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

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
            assertFailsWith<IllegalArgumentException> {
                GuiExtensionRegistry(listOf(extension("a"), extension("a")))
            }
        }

        test("rejects-two-extensions-contributing-the-same-solver-profile") {
            val contributions = GuiContributions(solverProfiles = listOf(profile("shared")))
            assertFailsWith<IllegalArgumentException> {
                GuiExtensionRegistry(listOf(extension("a", contributions), extension("b", contributions)))
            }
        }

        test("rejects-two-extensions-contributing-the-same-feature-or-command") {
            val feature = FeatureDescriptor(FeatureId("f"), "F", FeaturePlacement(SemanticRegion.RESULTS))
            assertFailsWith<IllegalArgumentException> {
                GuiExtensionRegistry(
                    listOf(
                        extension("a", GuiContributions(features = listOf(feature))),
                        extension("b", GuiContributions(features = listOf(feature))),
                    ),
                )
            }
            val command = CommandDescriptor(CommandId("c"), "C")
            assertFailsWith<IllegalArgumentException> {
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
            assertEquals(ExtensionId("a"), registry.find(ExtensionId("a"))?.id)
            assertNull(registry.find(ExtensionId("missing")))
        }
    })
