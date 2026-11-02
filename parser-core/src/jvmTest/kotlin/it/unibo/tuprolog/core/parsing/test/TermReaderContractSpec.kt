package it.unibo.tuprolog.core.parsing.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.TermReader
import java.io.Reader

class TermReaderContractSpec :
    FunSpec({
        class CountingReader(
            private val input: String,
        ) : Reader() {
            var charactersRead: Int = 0
                private set

            override fun read(
                target: CharArray,
                offset: Int,
                length: Int,
            ): Int {
                if (charactersRead >= input.length) return -1
                val count = length.coerceAtMost(input.length - charactersRead)
                input.toCharArray(target, offset, charactersRead, charactersRead + count)
                charactersRead += count
                return count
            }

            override fun close() = Unit
        }

        class GeneratedTermReader(
            private val termCount: Int,
        ) : Reader() {
            private var nextTerm = 0
            private var pending = ""
            private var pendingOffset = 0

            var generatedTerms = 0
                private set

            @Suppress("ReturnCount")
            override fun read(
                target: CharArray,
                offset: Int,
                length: Int,
            ): Int {
                if (length == 0) return 0
                if (pendingOffset == pending.length) {
                    if (nextTerm == termCount) return -1
                    pending = "item(${nextTerm++}).\n"
                    pendingOffset = 0
                    generatedTerms++
                }
                val count = length.coerceAtMost(pending.length - pendingOffset)
                pending.toCharArray(target, offset, pendingOffset, pendingOffset + count)
                pendingOffset += count
                return count
            }

            override fun close() = Unit
        }

        test("reads-multiple-terms-from-one-reader") {
            val terms = TermReader.withNoOperator().readTerms("first. second. third").toList()
            (terms.map { it.shouldBeInstanceOf<Struct>().functor }) shouldBe listOf("first", "second", "third")
        }

        test("custom-operators-are-propagated-to-the-parse-session") {
            val operators = OperatorSet(Operator("++", Specifier.YFX, 500))
            val term = TermReader.withNoOperator().readTerms("a ++ b.", operators).single()
            val structure = term.shouldBeInstanceOf<Struct>()
            structure.functor shouldBe "++"
            structure.arity shouldBe 2
        }

        test("returned-sequence-does-not-read-before-iteration") {
            val reader = CountingReader("first. second.")
            val terms = TermReader.withNoOperator().readTerms(reader)
            reader.charactersRead shouldBe 0

            val iterator = terms.iterator()
            reader.charactersRead shouldBe 0
            (iterator.next().shouldBeInstanceOf<Struct>().functor) shouldBe "first"
            (reader.charactersRead > 0) shouldBe true
        }

        test("reads-a-very-long-lazily-generated-input") {
            val termCount = 50_000
            val reader = GeneratedTermReader(termCount)
            val terms = TermReader.withNoOperator().readTerms(reader)

            var parsed = 0
            for (term in terms) {
                val structure = term.shouldBeInstanceOf<Struct>()
                structure.functor shouldBe "item"
                structure[0].toString() shouldBe parsed.toString()
                parsed++
            }

            parsed shouldBe termCount
            reader.generatedTerms shouldBe termCount
        }
    })
