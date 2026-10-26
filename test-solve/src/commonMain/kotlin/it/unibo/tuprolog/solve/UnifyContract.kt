package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.funSpec

/**
 * Kotest contract for `'='/2`, to be `include`d by every solver's spec. It currently delegates to
 * [TestUnify.prototype] (Stage 3a of the Kotest migration, #1017); cases whose id is in [disabled] are
 * registered but not run.
 */
fun unifyContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = funSpec {
    val prototype = TestUnify.prototype(solverFactory)

    fun case(
        id: String,
        body: TestUnify.() -> Unit,
    ) = test(id).config(enabled = id !in disabled) { prototype.body() }

    case("unify/number-with-same-number") { testNumberUnify() }
    case("unify/variable-with-number") { testNumberXUnify() }
    case("unify/variable-with-variable") { testXYUnify() }
    case("unify/conjunction-binding-shared-variable") { testDoubleUnify() }
    case("unify/compounds-binding-both-sides") { testFDefUnify() }
    case("unify/different-numbers-fail") { testDiffNumberUnify() }
    case("unify/integer-with-real-fails") { testDecNumberUnify() }
    case("unify/different-functors-fail") { testGUnifyFX() }
    case("unify/different-arities-fail") { testFUnify() }
    case("unify/different-arities-with-repeated-variables-fail") { testFMultipleTermUnify() }
    case("unify/chained-variable-bindings") { testMultipleTermUnify() }
}
