package it.unibo.tuprolog.serialize

import io.kotest.assertions.withClue
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.PropTestConfig
import io.kotest.property.checkAll
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.testutils.TermArbs

@OptIn(ExperimentalKotest::class)
private val config = PropTestConfig(seed = 1017L, iterations = 300)

/** Deserializing a serialized term gives it back, up to variable identity (names are kept). */
private fun MimeType.assertRoundTrips(term: Term) {
    val serialized = TermSerializer.of(this).serialize(term)
    val deserialized = TermDeserializer.of(this).deserialize(serialized)
    withClue("$term -> $serialized -> $deserialized") {
        deserialized.equals(term, useVarCompleteName = false).shouldBeTrue()
    }
}

private class TermCase(
    val id: String,
    private val build: Scope.() -> Term,
) {
    fun term(): Term = Scope.empty().build()
}

/** Regression examples kept beside the property: reals that print like integers, partial lists, nesting. */
private val regressions =
    listOf(
        TermCase("whole-real") { realOf(1.0) },
        TermCase("negative-real") { realOf(-1.5) },
        TermCase("partial-list") { consOf(intOf(1), varOf("T")) },
        TermCase("empty-list") { emptyLogicList },
        TermCase("nested") { structOf("g", logicListOf(atomOf("a"), varOf("X")), structOf("f", varOf("X"))) },
        TermCase("pattern") { patternOf(varOf("P"), varOf("X"), atomOf("a")) },
        TermCase("nested-patterns") { structOf("f", patternOf(varOf("P"), patternOf(varOf("Q"), varOf("P")))) },
        TermCase("chain-metarule") {
            ruleOf(
                patternOf(varOf("P"), varOf("A"), varOf("B")),
                patternOf(varOf("Q"), varOf("A"), varOf("C")),
                patternOf(varOf("R"), varOf("C"), varOf("B")),
            )
        },
        TermCase("goal-with-pattern") { goalOf(patternOf(varOf("P"), atomOf("a")), atomOf("b")) },
    )

class TermSerializationPropertiesSpec :
    FunSpec({
        for (mimeType in listOf(MimeType.Json, MimeType.Yaml)) {
            val format = mimeType.subType

            test("$format/round-trip") {
                checkAll(config, TermArbs.nonGroundTerms()) { mimeType.assertRoundTrips(it) }
            }

            withTests(nameFn = { "$format/regressions/${it.id}" }, regressions) { case ->
                mimeType.assertRoundTrips(case.term())
            }
        }
    })
