package it.unibo.tuprolog.ui.gui.solver

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

private fun profile(id: String) =
    SolverProfile(
        id = SolverProfileId(id),
        displayName = id,
        capabilities = SolverCapabilities.EMPTY,
        factory = SolverSessionFactory { error("not used") },
    )

class SolverProfileRegistrySpec :
    FunSpec({
        test("rejects-an-empty-profile-list") {
            assertFailsWith<IllegalArgumentException> { SolverProfileRegistry(emptyList()) }
        }

        test("rejects-duplicate-profile-ids") {
            assertFailsWith<IllegalArgumentException> {
                SolverProfileRegistry(listOf(profile("a"), profile("a")))
            }
        }

        test("find-and-require-look-up-by-id-while-plus-adds-without-overwriting") {
            val registry = SolverProfileRegistry(listOf(profile("b"), profile("a")))
            assertEquals(SolverProfileId("a"), registry.find(SolverProfileId("a"))?.id)
            assertNull(registry.find(SolverProfileId("missing")))
            assertFailsWith<IllegalStateException> { registry.require(SolverProfileId("missing")) }
            // all() is sorted by display name.
            assertEquals(listOf("a", "b"), registry.all().map { it.displayName })

            val extended = registry.plus(listOf(profile("c")))
            assertEquals(3, extended.all().size)
            assertFailsWith<IllegalArgumentException> { registry.plus(listOf(profile("a"))) }
        }
    })
