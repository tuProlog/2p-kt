package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.testutils.WorklistUnificators

/** Two terms to unify with occurs-check disabled, the expected outcome, and whether occurs-check would reject them. */
private class CyclicCase(
    val id: String,
    val rejectedByOccursCheck: Boolean,
    val build: Scope.() -> Triple<Term, Term, Substitution>,
)

private val cyclicCases =
    listOf(
        CyclicCase("self-reference", true) {
            Triple(varOf("X"), structOf("f", varOf("X")), substitutionOf("X" to structOf("f", varOf("X"))))
        },
        CyclicCase("mutual-reference", true) {
            Triple(
                structOf("f", varOf("X"), varOf("Y")),
                structOf("f", structOf("f", varOf("Y")), structOf("g", varOf("X"))),
                substitutionOf("X" to structOf("f", structOf("g", varOf("X"))), "Y" to structOf("g", varOf("X"))),
            )
        },
        CyclicCase("cycle-through-variable-chain", true) {
            Triple(
                structOf("f", varOf("X"), varOf("Y")),
                structOf("f", varOf("Y"), structOf("g", varOf("X"))),
                substitutionOf("X" to structOf("g", varOf("X")), "Y" to structOf("g", varOf("X"))),
            )
        },
        CyclicCase("two-rational-terms", true) {
            Triple(
                structOf("f", varOf("X"), varOf("Y"), varOf("X")),
                structOf("f", structOf("f", varOf("X")), structOf("f", varOf("Y")), varOf("Y")),
                substitutionOf("X" to structOf("f", varOf("X")), "Y" to structOf("f", varOf("Y"))),
            )
        },
        CyclicCase("two-different-rational-terms", true) {
            Triple(
                structOf("f", varOf("X"), varOf("Y"), varOf("X")),
                structOf("f", structOf("f", varOf("X")), structOf("f", structOf("g", varOf("Y"))), varOf("Y")),
                Substitution.failed(),
            )
        },
    )

/** With occurs-check disabled, cyclic bindings are allowed, and unification terminates on cyclic terms. */
class CyclicUnificationSpec :
    FunSpec({
        withTests(nameFn = { "without-occurs-check/${it.id}" }, cyclicCases) { case ->
            val (left, right, expected) = Scope.empty().(case.build)()
            WorklistUnificators.strict().mgu(left, right, false) shouldBe expected
        }

        withTests(nameFn = { "with-occurs-check/${it.id}" }, cyclicCases.filter { it.rejectedByOccursCheck }) { case ->
            val (left, right, _) = Scope.empty().(case.build)()
            WorklistUnificators.strict().mgu(left, right, true) shouldBe Substitution.failed()
        }

        test("long-cycle-against-short-cycle") {
            Scope.empty {
                val n = 1_000
                // X0 = f(X1), ..., Xn-1 = f(X0), unified with Y = f(Y)
                val chain = (0 until n).map { varOf("X$it") to structOf("f", varOf("X${(it + 1) % n}")) }
                val context =
                    WorklistUnificators.strict().mgu(
                        tupleOf(chain.map { it.first }),
                        tupleOf(chain.map { it.second }),
                        false,
                    )
                val unificator = WorklistUnificators.strict(context)
                val y = varOf("Y")
                unificator.match(structOf("g", y, y), structOf("g", structOf("f", y), varOf("X0")), false) shouldBe true
            }
        }

        test("cyclic-context-is-rejected-with-occurs-check-only") {
            Scope.empty {
                val context = substitutionOf("X" to structOf("f", varOf("X")))
                val unificator = WorklistUnificators.strict(context)
                unificator.mgu(varOf("Y"), atomOf("a"), true) shouldBe Substitution.failed()
                unificator.mgu(varOf("Y"), atomOf("a"), false) shouldBe
                    substitutionOf("X" to structOf("f", varOf("X")), "Y" to atomOf("a"))
            }
        }
    })
