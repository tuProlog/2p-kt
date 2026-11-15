package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.dsl.theory.logicProgramming

/**
 * Built-ins inspecting terms treat patterns (compounds whose functor is a variable, e.g. `P(a)`) as compound terms whose
 * functor is that variable.
 */
fun patternDataContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("pattern-data/compound") { "compound"(varOf("P")("a")) yields { listOf(it.yes()) } }

    case("pattern-data/callable") { "callable"(varOf("P")("a")) yields { listOf(it.yes()) } }

    case("pattern-data/not-ground") { "ground"(varOf("P")("a")) yields { listOf(it.no()) } }

    case("pattern-data/functor") {
        functor(varOf("P")("a", "b"), "F", "N") yields { listOf(it.yes("F" to "P", "N" to 2)) }
    }

    case("pattern-data/univ") {
        (varOf("P")("a", "b") univ "L") yields { listOf(it.yes("L" to logicListOf("P", "a", "b"))) }
    }

    case("pattern-data/arg") { arg(2, varOf("P")("a", "b"), "X") yields { listOf(it.yes("X" to "b")) } }

    case("pattern-data/unifies-with-struct") {
        (varOf("P")("a", "Y") eq "f"("X", "b")) yields { listOf(it.yes("P" to "f", "X" to "a", "Y" to "b")) }
    }

    scenario("pattern-data/copy-term") {
        logicProgramming {
            val solution = solverFactory.solverWithDefaultBuiltins().solveOnce(copy_term(varOf("P")("a"), "C"))
            solution.shouldBeInstanceOf<Solution.Yes>()
            val copy = solution.substitution[varOf("C")]
            copy.shouldBeInstanceOf<Pattern>()
            (copy.functor == varOf("P")) shouldBe false
        }
    }
}
