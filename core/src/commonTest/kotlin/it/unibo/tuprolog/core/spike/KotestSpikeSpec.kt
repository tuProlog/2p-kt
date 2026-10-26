package it.unibo.tuprolog.core.spike

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Var
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

// Throwaway Stage 1 spike (#1017): one spec exercising each Kotest feature the migration relies on.

private data class AtomCase(
    val id: String,
    val value: String,
    val expectedLength: Int,
)

fun reusableFactory(prefix: String) =
    funSpec {
        test("$prefix/factory-test") {
            Atom.of(prefix).value shouldBe prefix
        }
    }

private suspend fun withCoroutineTestScopeFailing() {
    delay(60_000)
    error("boom")
}

class KotestSpikeSpec :
    FunSpec({
        test("plain test") {
            Integer.of(1) shouldBe Integer.of(1)
        }

        context("nested context") {
            test("leaf in context") {
                Atom.of("a").isAtom shouldBe true
            }
        }

        context("parameterized") {
            withTests(
                nameFn = { it.id },
                listOf(
                    AtomCase("atom/lowercase", "a", expectedLength = 1),
                    AtomCase("atom/uppercase", "Ab", expectedLength = 2),
                    AtomCase("atom/space", "a b", expectedLength = 3),
                ),
            ) { case ->
                Atom.of(case.value).value.length shouldBe case.expectedLength
            }
        }

        test("fixture built inside the leaf") {
            val x = Var.of("X")
            (x == x) shouldBe true
        }

        test("disabled test").config(enabled = false) {
            error("must not run")
        }

        xtest("x-disabled test") {
            error("must not run")
        }

        test("coroutineTestScope gives virtual time").config(coroutineTestScope = true) {
            val start = TimeSource.Monotonic.markNow()
            delay(60_000)
            (start.elapsedNow() < 5.seconds) shouldBe true
        }

        test("coroutineTestScope failures propagate to the leaf").config(coroutineTestScope = true) {
            shouldThrow<IllegalStateException> {
                withCoroutineTestScopeFailing()
            }
        }

        include(reusableFactory("spike"))
    })
