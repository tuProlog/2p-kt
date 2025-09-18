package it.unibo.tuprolog.solve.libs.io

import kotlin.test.assertEquals

private val TERMINATOR = "\r?\n".toRegex()

internal fun String.trimAndCorrectLineTermination(lineTerminator: String = "\n"): String =
    trim().replace(TERMINATOR, lineTerminator)

fun assertSameLines(
    expected: String,
    actual: String,
) = assertEquals(expected.trimAndCorrectLineTermination(), actual.trimAndCorrectLineTermination())
