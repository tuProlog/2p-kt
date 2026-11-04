package it.unibo.tuprolog.utils.time

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private data class InvalidPolicyCase(
    val id: String,
    val build: () -> AdaptiveSamplingPolicy,
)

class AdaptiveSamplingPolicySpec :
    FunSpec({
        withTests(
            nameFn = { "rejects/${it.id}" },
            listOf(
                InvalidPolicyCase("zero-sampling-period") { AdaptiveSamplingPolicy(samplingPeriod = Duration.ZERO) },
                InvalidPolicyCase("zero-budget-fraction") { AdaptiveSamplingPolicy(budgetFraction = 0.0) },
                InvalidPolicyCase("budget-fraction-above-one") { AdaptiveSamplingPolicy(budgetFraction = 1.5) },
                InvalidPolicyCase("zero-min-leaps") { AdaptiveSamplingPolicy(minLeaps = 0) },
                InvalidPolicyCase("max-below-min-leaps") { AdaptiveSamplingPolicy(minLeaps = 8, maxLeaps = 4) },
                InvalidPolicyCase("zero-growth-factor") { AdaptiveSamplingPolicy(maxGrowthFactor = 0) },
                InvalidPolicyCase("zero-smoothing") { AdaptiveSamplingPolicy(smoothing = 0.0) },
                InvalidPolicyCase("negative-sampling-period") {
                    AdaptiveSamplingPolicy(samplingPeriod = (-1).milliseconds)
                },
            ),
        ) { case ->
            shouldThrow<IllegalArgumentException> { case.build() }
        }
    })
