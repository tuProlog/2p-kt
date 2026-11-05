package it.unibo.tuprolog.solve.flags

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.function.realMathContext
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.RoundingMode

class FlagStoreSpec :
    FunSpec({
        test("of(vararg NotableFlag) keeps every distinct flag, not just the last one") {
            val store = FlagStore.of(Unknown, MaxArity, DoubleQuotes, LastCallOptimization, TrackVariables)
            store.size shouldBe 5
            store[Unknown.name] shouldBe Unknown.defaultValue
            store[MaxArity.name] shouldBe MaxArity.defaultValue
            store[DoubleQuotes.name] shouldBe DoubleQuotes.defaultValue
            store[LastCallOptimization.name] shouldBe LastCallOptimization.defaultValue
            store[TrackVariables.name] shouldBe TrackVariables.defaultValue
        }

        test("DEFAULT contains every notable flag at its own default value") {
            FlagStore.DEFAULT.size shouldBe 12
            FlagStore.DEFAULT[Unknown.name] shouldBe Unknown.defaultValue
            FlagStore.DEFAULT[Bounded.name] shouldBe Bounded.defaultValue
            FlagStore.DEFAULT[TrackVariables.name] shouldBe TrackVariables.defaultValue
            FlagStore.DEFAULT[ShowWildCardVariablesInSolutions.name] shouldBe
                ShowWildCardVariablesInSolutions.defaultValue
            FlagStore.DEFAULT[UniqueSolutions.name] shouldBe UniqueSolutions.defaultValue
            FlagStore.DEFAULT[GroundQueriesHaveBooleanSolution.name] shouldBe
                GroundQueriesHaveBooleanSolution.defaultValue
            FlagStore.DEFAULT[RealPrecisionMode.name] shouldBe RealPrecisionMode.UNLIMITED_WITH_FALLBACK
        }

        test("real-math-context/default-is-decimal64") {
            FlagStore.DEFAULT.realMathContext shouldBe MathContext.DECIMAL64
        }

        test("real-math-context/follows-the-flags") {
            val store =
                FlagStore.DEFAULT
                    .set(
                        RealPrecisionDigits,
                        Integer.of(4),
                    ).set(RealRoundingMode, RealRoundingMode.DOWN)
            store.realMathContext shouldBe MathContext(4, RoundingMode.DOWN)
        }

        test("real-math-context/unset-flags-take-their-defaults") {
            FlagStore.EMPTY.realMathContext shouldBe MathContext.DECIMAL64
        }

        test("the new solution-presentation flags default to leaving behaviour unaffected") {
            ShowWildCardVariablesInSolutions.defaultValue shouldBe ShowWildCardVariablesInSolutions.ON
            UniqueSolutions.defaultValue shouldBe UniqueSolutions.OFF
            GroundQueriesHaveBooleanSolution.defaultValue shouldBe GroundQueriesHaveBooleanSolution.OFF
        }
    })
