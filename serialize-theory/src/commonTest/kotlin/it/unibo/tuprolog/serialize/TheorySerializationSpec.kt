package it.unibo.tuprolog.serialize

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import it.unibo.tuprolog.serialize.ObjectsUtils.deeplyEqual
import it.unibo.tuprolog.serialize.ObjectsUtils.parseAsObject

private val casesByMimeType = theoryCases.flatMap { case -> textualMimeTypes.map { case to it } }

class TheorySerializationSpec :
    FunSpec({
        withTests(nameFn = { (case, mimeType) ->
            "serialize/${case.id}/${mimeType.subType}"
        }, casesByMimeType) { (case, mimeType) ->
            TheorySerializer.of(mimeType).assertSerializationWorks(case.text(mimeType), case.theory())
        }

        withTests(nameFn = { "serialize-many/${it.subType}" }, textualMimeTypes) { mimeType ->
            val serialized = TheorySerializer.of(mimeType).serializeMany(theoryCases.map { it.theory() })
            val expected = theoryCases.map { parseAsObject(it.text(mimeType), mimeType) }
            deeplyEqual(expected, parseAsObject(serialized, mimeType)).shouldBeTrue()
        }
    })
