package it.unibo.tuprolog.ui.gui.solver

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId

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
            shouldThrow<IllegalArgumentException> { SolverProfileRegistry(emptyList()) }
        }

        test("rejects-duplicate-profile-ids") {
            shouldThrow<IllegalArgumentException> {
                SolverProfileRegistry(listOf(profile("a"), profile("a")))
            }
        }

        test("find-and-require-look-up-by-id-while-plus-adds-without-overwriting") {
            val registry = SolverProfileRegistry(listOf(profile("b"), profile("a")))
            registry.find(SolverProfileId("a"))?.id shouldBe SolverProfileId("a")
            registry.find(SolverProfileId("missing")).shouldBeNull()
            shouldThrow<IllegalStateException> { registry.require(SolverProfileId("missing")) }
            // all() is sorted by display name.
            (registry.all().map { it.displayName }) shouldBe listOf("a", "b")

            val extended = registry.plus(listOf(profile("c")))
            extended.all().size shouldBe 3
            shouldThrow<IllegalArgumentException> { registry.plus(listOf(profile("a"))) }
        }
    })
