package it.unibo.tuprolog.solve.flags

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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
            FlagStore.DEFAULT.size shouldBe 8
            FlagStore.DEFAULT[Unknown.name] shouldBe Unknown.defaultValue
            FlagStore.DEFAULT[TrackVariables.name] shouldBe TrackVariables.defaultValue
            FlagStore.DEFAULT[ShowWildCardVariablesInSolutions.name] shouldBe
                ShowWildCardVariablesInSolutions.defaultValue
            FlagStore.DEFAULT[UniqueSolutions.name] shouldBe UniqueSolutions.defaultValue
            FlagStore.DEFAULT[GroundQueriesHaveBooleanSolution.name] shouldBe
                GroundQueriesHaveBooleanSolution.defaultValue
        }

        test("the new solution-presentation flags default to leaving behaviour unaffected") {
            ShowWildCardVariablesInSolutions.defaultValue shouldBe ShowWildCardVariablesInSolutions.ON
            UniqueSolutions.defaultValue shouldBe UniqueSolutions.OFF
            GroundQueriesHaveBooleanSolution.defaultValue shouldBe GroundQueriesHaveBooleanSolution.OFF
        }
    })
