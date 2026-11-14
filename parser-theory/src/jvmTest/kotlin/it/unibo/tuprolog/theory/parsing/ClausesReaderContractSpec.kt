package it.unibo.tuprolog.theory.parsing

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.parser.exceptions.SourceReadException
import it.unibo.tuprolog.theory.Theory
import java.io.ByteArrayInputStream
import java.io.Reader
import java.io.StringReader

class ClausesReaderContractSpec :
    FunSpec({
        fun stream(text: String) = ByteArrayInputStream(text.encodeToByteArray())

        fun Iterable<Clause>.singleHead(): Struct = (single() as Fact).head.castToStruct()

        fun Sequence<Clause>.singleHead(): Struct = (single() as Fact).head.castToStruct()

        fun Theory.singleHead(): Struct = (single() as Fact).head.castToStruct()

        class GeneratedClauseReader(
            private val clauseCount: Int,
        ) : Reader() {
            private var nextClause = 0
            private var pending = ""
            private var pendingOffset = 0

            var generatedClauses = 0
                private set

            @Suppress("ReturnCount")
            override fun read(
                target: CharArray,
                offset: Int,
                length: Int,
            ): Int {
                if (length == 0) return 0
                if (pendingOffset == pending.length) {
                    if (nextClause == clauseCount) return -1
                    pending = "c(${nextClause++}).\n"
                    pendingOffset = 0
                    generatedClauses++
                }
                val count = length.coerceAtMost(pending.length - pendingOffset)
                pending.toCharArray(target, offset, pendingOffset, pendingOffset + count)
                pendingOffset += count
                return count
            }

            override fun close() = Unit
        }

        test("reader-factories-and-all-input-overloads-delegate-correctly") {
            val customOperator = Operator("++", Specifier.YFX, 500)
            val custom = OperatorSet(customOperator)
            val reader = ClausesReader.withNoOperator()

            reader.defaultOperatorSet shouldBe OperatorSet.EMPTY
            ClausesReader.withStandardOperators().defaultOperatorSet shouldBe OperatorSet.STANDARD
            ClausesReader.withDefaultOperators().defaultOperatorSet shouldBe OperatorSet.DEFAULT
            ClausesReader.withOperators(custom).defaultOperatorSet shouldBe custom
            ClausesReader.withOperators(customOperator).defaultOperatorSet shouldBe custom

            reader.readClauses(StringReader("a.")).singleHead().functor shouldBe "a"
            reader.readClauses(stream("a.")).singleHead().functor shouldBe "a"
            reader.readClauses(StringReader("a ++ b."), custom).singleHead().functor shouldBe "++"
            reader.readClauses(stream("a ++ b."), custom).singleHead().functor shouldBe "++"
            reader.readClausesLazily(StringReader("a.")).singleHead().functor shouldBe "a"
            reader.readClausesLazily(stream("a.")).singleHead().functor shouldBe "a"
            reader.readClausesLazily(StringReader("a ++ b."), custom).singleHead().functor shouldBe "++"
            reader.readClausesLazily(stream("a ++ b."), custom).singleHead().functor shouldBe "++"
            reader.readTheory(StringReader("a.")).size shouldBe 1L
            reader.readTheory(stream("a.")).size shouldBe 1L
            reader.readTheory(StringReader("a ++ b."), custom).singleHead().functor shouldBe "++"
            reader.readTheory(stream("a ++ b."), custom).singleHead().functor shouldBe "++"
        }

        test("returned-sequence-does-not-read-until-it-is-consumed") {
            val source = GeneratedClauseReader(10)
            val clauses = ClausesReader.withNoOperator().readClausesLazily(source)

            source.generatedClauses shouldBe 0
            val iterator = clauses.iterator()
            source.generatedClauses shouldBe 0
            (iterator.next() as Fact).head.functor shouldBe "c"
            (source.generatedClauses in 1 until 10).shouldBeTrue()
        }

        test("source-failures-are-translated-without-losing-their-cause") {
            val failure = IllegalStateException("reader failed")
            val reader =
                object : Reader() {
                    override fun read(
                        target: CharArray,
                        offset: Int,
                        length: Int,
                    ): Int = throw failure

                    override fun close() = Unit
                }

            val error = shouldThrow<ParseException> { ClausesReader.withNoOperator().readClauses(reader) }
            val sourceError = error.cause.shouldBeInstanceOf<SourceReadException>()
            sourceError.cause shouldBeSameInstanceAs failure
            error.clauseIndex shouldBe 0
            error.line shouldBe (sourceError.span.start.line + 1)
            error.column shouldBe (sourceError.span.start.column + 1)
            error.offendingSymbol shouldBe sourceError.offendingText
            error.message shouldBe sourceError.message
        }

        test("parses-a-very-long-lazily-generated-input") {
            val clauseCount = 50_000
            val source = GeneratedClauseReader(clauseCount)
            val clauses = ClausesReader.withNoOperator().readClausesLazily(source)

            var parsed = 0
            for (clause in clauses) {
                val head = (clause as Fact).head
                head.functor shouldBe "c"
                head[0].castToInteger().value.toLong() shouldBe parsed.toLong()
                parsed++
            }

            parsed shouldBe clauseCount
            source.generatedClauses shouldBe clauseCount
        }
    })
