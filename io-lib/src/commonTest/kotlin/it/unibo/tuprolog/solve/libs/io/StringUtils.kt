package it.unibo.tuprolog.solve.libs.io

import io.kotest.matchers.shouldBe

private val TERMINATOR = "\r?\n".toRegex()

internal fun String.trimAndCorrectLineTermination(lineTerminator: String = "\n"): String =
    trim().replace(TERMINATOR, lineTerminator)

fun assertSameLines(
    expected: String,
    actual: String,
) = actual.trimAndCorrectLineTermination() shouldBe expected.trimAndCorrectLineTermination()
