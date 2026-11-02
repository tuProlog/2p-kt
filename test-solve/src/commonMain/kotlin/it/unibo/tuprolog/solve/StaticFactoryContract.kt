@file:Suppress("ConstPropertyName", "ktlint:standard:property-naming")

package it.unibo.tuprolog.solve

import io.kotest.assertions.fail
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import it.unibo.tuprolog.Info

/** Conformance tests for `Solver.prolog`/`Solver.problog`, the static factory properties on [Solver]'s companion object that locate a concrete [SolverFactory] implementation at runtime (via a service-loading mechanism) without the caller needing a compile-time dependency on the implementing module. */
fun staticFactoryContract(expectations: Expectations) =
    funSpec {
        // If [Expectations.prologShouldWork], tests that `Solver.prolog` resolves to
        // `it.unibo.tuprolog.solve.prolog.PrologSolver`; otherwise tests that the lookup is unavailable.
        test("static-factory/static-solver-factory-for-prolog") {
            if (expectations.prologShouldWork) {
                testStaticSolverFactoryShouldWork(prologSolverClass) { Solver.prolog }
            } else {
                testStaticFactoryShouldFail(prologName) { Solver.prolog }
            }
        }

        // Same as the case above, but for `Solver.problog` / [Expectations.problogShouldWork],
        // expecting `it.unibo.tuprolog.solve.problog.ProblogSolver`.
        test("static-factory/static-solver-factory-for-problog") {
            if (expectations.problogShouldWork) {
                testStaticSolverFactoryShouldWork(problogSolverClass) { Solver.problog }
            } else {
                testStaticFactoryShouldFail(problogName) { Solver.problog }
            }
        }
    }

private const val prologSolverClass = "it.unibo.tuprolog.solve.prolog.PrologSolver"
private const val problogSolverClass = "it.unibo.tuprolog.solve.problog.ProblogSolver"
private const val prologName = "prolog"
private const val problogName = "problog"

private fun testStaticSolverFactoryShouldWork(
    `class`: String,
    factory: () -> SolverFactory,
) {
    factory().shouldNotBeNull()
    val solver = factory().solverOf()
    solver.shouldNotBeNull()
    assertClassNameIs(solver::class, `class`)
    factory().newBuilder().shouldNotBeNull()
    factory().newBuilder() shouldNotBeSameInstanceAs factory().newBuilder()
    val solver2 = factory().newBuilder().build()
    solver2.shouldNotBeNull()
    assertClassNameIs(solver2::class, `class`)
}

private fun testStaticFactoryShouldFail(
    name: String,
    factory: () -> SolverFactory,
) {
    try {
        // this may either fail or not on JS, depending on how the tests are launched
        // while it should always fail on JVM
        // ...
        factory()
        if (Info.PLATFORM.isJava) {
            fail("Solver.$name should not be available at runtime here")
        }
    } catch (e: IllegalStateException) {
        // ... in any case, if it fails, an IllegalStateException should be thrown
        e.message?.startsWith("No viable implementation for SolverFactory") shouldBe true
    }
}
