package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import kotlin.math.sign

private data class OrderCase(
    val id: String,
    val first: Long,
    val second: Long,
)

private val orderCases =
    listOf(
        OrderCase("close-indices", 1, 2),
        OrderCase("equal-indices", 3, 3),
        OrderCase("delta-of-two-to-the-31", 1L shl 31, 0),
        OrderCase("delta-of-two-to-the-32", 1L shl 32, 0),
        OrderCase("extreme-indices", Long.MIN_VALUE, Long.MAX_VALUE),
    )

class LongIndexedSpec :
    FunSpec({
        withTests(nameFn = { "compare-to/${it.id}" }, orderCases) { case ->
            val first = LongIndexed.of(case.first, "a")
            val second = LongIndexed.of(case.second, "b")
            first.compareTo(second).sign shouldBe case.first.compareTo(case.second).sign
            second.compareTo(first).sign shouldBe case.second.compareTo(case.first).sign
        }
    })
