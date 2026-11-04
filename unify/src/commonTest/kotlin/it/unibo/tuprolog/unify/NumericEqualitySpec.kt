package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.testutils.ReferenceUnificators
import it.unibo.tuprolog.unify.testutils.WorklistUnificators

/** Two numeric terms, and whether strict and naive unification deem them equal. */
private class NumericCase(
    val id: String,
    val lhs: Term,
    val rhs: Term,
    val strict: Boolean,
    val naive: Boolean,
)

private val numericCases =
    listOf(
        NumericCase("same-integers", Integer.of(1), Integer.of(1), strict = true, naive = true),
        NumericCase("different-integers", Integer.of(1), Integer.of(2), strict = false, naive = false),
        NumericCase(
            "big-integers",
            Integer.of("123456789012345678901234567890"),
            Integer.of("123456789012345678901234567890"),
            true,
            true,
        ),
        NumericCase("same-reals", Real.of(1.5), Real.of(1.5), strict = true, naive = true),
        NumericCase("reals-with-trailing-zeros", Real.of("1.5"), Real.of("1.500"), strict = true, naive = true),
        NumericCase("different-reals", Real.of(1.5), Real.of(1.25), strict = false, naive = false),
        NumericCase("integer-and-equal-real", Integer.of(1), Real.of(1.0), strict = false, naive = true),
        NumericCase("real-and-equal-integer", Real.of(1.0), Integer.of(1), strict = false, naive = true),
        NumericCase("integer-and-different-real", Integer.of(1), Real.of(1.5), strict = false, naive = false),
        NumericCase(
            "nested-integer-and-equal-real",
            Struct.of("f", Integer.of(2)),
            Struct.of("f", Real.of(2.0)),
            false,
            true,
        ),
    )

/** An engine, the strategy it implements, and how to build it. */
private class NumericStrategyCase(
    val id: String,
    val naive: Boolean,
    val unificator: () -> Unificator,
)

private val strategies =
    listOf(
        NumericStrategyCase("worklist/strict", false, WorklistUnificators::strict),
        NumericStrategyCase("worklist/naive", true, WorklistUnificators::naive),
        NumericStrategyCase("equational/strict", false, ReferenceUnificators::strict),
        NumericStrategyCase("equational/naive", true, ReferenceUnificators::naive),
        NumericStrategyCase("default/strict", false, Unificator::strict),
        NumericStrategyCase("default/naive", true, Unificator::naive),
    )

private val cases = strategies.flatMap { strategy -> numericCases.map { strategy to it } }

/** Strict unification compares numbers by representation, naive unification by value. */
class NumericEqualitySpec :
    FunSpec({
        withTests(nameFn = { (strategy, case) -> "${strategy.id}/${case.id}" }, cases) { (strategy, case) ->
            strategy.unificator().match(case.lhs, case.rhs) shouldBe if (strategy.naive) case.naive else case.strict
        }
    })
