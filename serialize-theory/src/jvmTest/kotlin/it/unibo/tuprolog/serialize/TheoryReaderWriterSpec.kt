package it.unibo.tuprolog.serialize

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import java.io.StringReader
import java.io.StringWriter

/** The JVM's [java.io.Writer]- and [java.io.Reader]-based theory serializers. */
class TheoryReaderWriterSpec :
    FunSpec({
        withTests(nameFn = { "round-trip/${it.subType}" }, textualMimeTypes) { mimeType ->
            val theories = theoryCases.map { it.theory() }
            val writer = StringWriter()
            WritingTheorySerializer.of(mimeType).serializeMany(writer, theories)
            val read = ReadingTheoryDeserializer.of(mimeType).deserializeMany(StringReader(writer.toString())).toList()
            read shouldHaveSize theories.size
            theories.zip(read).forEach { (expected, actual) ->
                representationsAreEqual(expected, actual).shouldBeTrue()
            }
        }
    })
