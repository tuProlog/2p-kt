package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.unify.testutils.EngineCase

/** A context, an operation to perform in it, and the expected outcome, all built in one [Scope]. */
private class ContextCase(
    val id: String,
    val run: Scope.(EngineCase) -> Pair<Substitution, Substitution>,
)

private val contextCases =
    listOf(
        ContextCase("empty-context") { engine ->
            engine(Substitution.empty()).mgu(varOf("X"), atomOf("a")) to substitutionOf("X" to atomOf("a"))
        },
        ContextCase("compatible-context-is-part-of-the-result") { engine ->
            engine(substitutionOf("X" to atomOf("a"))).mgu(varOf("Y"), atomOf("b")) to
                substitutionOf("X" to atomOf("a"), "Y" to atomOf("b"))
        },
        ContextCase("context-bindings-are-dereferenced") { engine ->
            engine(substitutionOf("X" to atomOf("a"))).mgu(varOf("Y"), varOf("X")) to
                substitutionOf("X" to atomOf("a"), "Y" to atomOf("a"))
        },
        ContextCase("context-bindings-are-refined") { engine ->
            engine(substitutionOf("X" to structOf("f", varOf("Y")))).mgu(varOf("Y"), atomOf("a")) to
                substitutionOf("X" to structOf("f", atomOf("a")), "Y" to atomOf("a"))
        },
        ContextCase("conflicting-context") { engine ->
            engine(substitutionOf("X" to atomOf("a"))).mgu(varOf("X"), atomOf("b")) to Substitution.failed()
        },
        ContextCase("failed-context") { engine ->
            engine(Substitution.failed()).mgu(varOf("X"), atomOf("a")) to Substitution.failed()
        },
        ContextCase("variable-chain-in-context") { engine ->
            engine(substitutionOf("X" to varOf("Y"))).mgu(varOf("Y"), atomOf("a")) to
                substitutionOf("X" to atomOf("a"), "Y" to atomOf("a"))
        },
        ContextCase("non-idempotent-context-is-normalised") { engine ->
            val context =
                Substitution.unifier(
                    mapOf(varOf("X") to structOf("f", varOf("Y")), varOf("Y") to atomOf("a")),
                )
            engine(context).mgu(varOf("Z"), atomOf("b")) to
                substitutionOf("X" to structOf("f", atomOf("a")), "Y" to atomOf("a"), "Z" to atomOf("b"))
        },
        ContextCase("merge-in-context") { engine ->
            engine(substitutionOf("X" to atomOf("a")))
                .merge(substitutionOf("Y" to varOf("X")), substitutionOf("Z" to varOf("Y"))) to
                substitutionOf("X" to atomOf("a"), "Y" to atomOf("a"), "Z" to atomOf("a"))
        },
        ContextCase("merge-conflicting-with-context") { engine ->
            engine(
                substitutionOf("X" to atomOf("a")),
            ).merge(substitutionOf("X" to atomOf("b")), Substitution.empty()) to
                Substitution.failed()
        },
    )

private val cases = EngineCase.strict.flatMap { engine -> contextCases.map { engine to it } }

/** Every operation behaves as if [Unificator.context]'s bindings were established before it starts. */
class UnificationContextSpec :
    FunSpec({
        withTests(nameFn = { (engine, case) -> "${engine.id}/${case.id}" }, cases) { (engine, case) ->
            val (actual, expected) = Scope.empty().(case.run)(engine)
            actual shouldBe expected
        }

        withTests(nameFn = { "${it.id}/instances-can-be-reused" }, EngineCase.strict) { engine ->
            Scope.empty {
                val unificator = engine(substitutionOf("C" to atomOf("c")))
                unificator.mgu(varOf("X"), atomOf("a")) shouldBe substitutionOf("C" to atomOf("c"), "X" to atomOf("a"))
                unificator.mgu(varOf("X"), atomOf("b")) shouldBe substitutionOf("C" to atomOf("c"), "X" to atomOf("b"))
                unificator.mgu(atomOf("a"), atomOf("b")) shouldBe Substitution.failed()
                unificator.mgu(varOf("X"), varOf("C")) shouldBe substitutionOf("C" to atomOf("c"), "X" to atomOf("c"))
            }
        }
    })
