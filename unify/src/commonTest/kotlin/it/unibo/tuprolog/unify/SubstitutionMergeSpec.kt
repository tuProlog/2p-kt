package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.unify.Unificator.Companion.mergeWith
import it.unibo.tuprolog.unify.testutils.EngineCase
import it.unibo.tuprolog.unify.testutils.WorklistUnificators

/** Two substitutions to merge, and the expected outcome, all built in one [Scope]. */
private class MergeCase(
    val id: String,
    val build: Scope.() -> Triple<Substitution, Substitution, Substitution>,
)

private val mergeCases =
    listOf(
        MergeCase("disjoint") {
            Triple(
                substitutionOf("X" to atomOf("a")),
                substitutionOf("Y" to atomOf("b")),
                substitutionOf(
                    "X" to atomOf("a"),
                    "Y" to atomOf("b"),
                ),
            )
        },
        MergeCase("compatible-overlap") {
            Triple(
                substitutionOf("X" to structOf("f", varOf("Y"))),
                substitutionOf("X" to structOf("f", atomOf("a"))),
                substitutionOf("X" to structOf("f", atomOf("a")), "Y" to atomOf("a")),
            )
        },
        MergeCase("equal-overlap") {
            Triple(
                substitutionOf("X" to atomOf("a")),
                substitutionOf("X" to atomOf("a")),
                substitutionOf(
                    "X" to atomOf("a"),
                ),
            )
        },
        MergeCase("incompatible-overlap") {
            Triple(substitutionOf("X" to atomOf("a")), substitutionOf("X" to atomOf("b")), Substitution.failed())
        },
        MergeCase("second-binds-variables-of-the-first") {
            Triple(
                substitutionOf("X" to structOf("f", varOf("Y"))),
                substitutionOf("Y" to atomOf("a")),
                substitutionOf("X" to structOf("f", atomOf("a")), "Y" to atomOf("a")),
            )
        },
        MergeCase("variable-chains") {
            Triple(
                substitutionOf("X" to varOf("Y")),
                substitutionOf("Y" to varOf("Z"), "Z" to atomOf("a")),
                substitutionOf("X" to atomOf("a"), "Y" to atomOf("a"), "Z" to atomOf("a")),
            )
        },
        MergeCase(
            "failed-first",
        ) { Triple(Substitution.failed(), substitutionOf("X" to atomOf("a")), Substitution.failed()) },
        MergeCase(
            "failed-second",
        ) { Triple(substitutionOf("X" to atomOf("a")), Substitution.failed(), Substitution.failed()) },
        MergeCase("occurs-check") {
            Triple(
                substitutionOf("X" to structOf("f", varOf("Y"))),
                substitutionOf("Y" to structOf("g", varOf("X"))),
                Substitution.failed(),
            )
        },
    )

private val cases = EngineCase.strict.flatMap { engine -> mergeCases.map { engine to it } }

class SubstitutionMergeSpec :
    FunSpec({
        withTests(nameFn = { "substitution-merge-never-fails/bind-${it.lowercase()}" }, listOf("X", "Y", "Z")) { v ->
            Scope.empty {
                val base = unifierOf("X" to varOf("Y"), "Y" to varOf("Z"))
                val a = atomOf("a")
                val assignment = unifierOf(v to a)
                base.mergeWith(assignment) shouldBe unifierOf("X" to a, "Y" to a, "Z" to a)
                assignment.mergeWith(base) shouldBe unifierOf("X" to a, "Y" to a, "Z" to a)
            }
        }

        withTests(nameFn = { (engine, case) -> "${engine.id}/${case.id}" }, cases) { (engine, case) ->
            val (first, second, expected) = Scope.empty().(case.build)()
            engine().merge(first, second) shouldBe expected
        }

        test("worklist/result-is-idempotent-also-without-occurs-check") {
            Scope.empty {
                val first = substitutionOf("Y" to atomOf("a"))
                val second = substitutionOf("X" to structOf("f", varOf("Y")))
                WorklistUnificators.strict().merge(first, second, false) shouldBe
                    substitutionOf("Y" to atomOf("a"), "X" to structOf("f", atomOf("a")))
            }
        }

        test("worklist/cyclic-merge-without-occurs-check") {
            Scope.empty {
                val first = substitutionOf("X" to structOf("f", varOf("Y")))
                val second = substitutionOf("Y" to structOf("g", varOf("X")))
                // Y = g(X) = g(f(Y)), and X = f(Y) = f(g(f(Y))): a rational solution, whose cycles stop at Y
                val y = structOf("g", structOf("f", varOf("Y")))
                WorklistUnificators.strict().merge(first, second, false) shouldBe
                    substitutionOf("X" to structOf("f", y), "Y" to y)
            }
        }
    })
