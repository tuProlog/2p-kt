package it.unibo.tuprolog.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException
import it.unibo.tuprolog.parser.exceptions.UnexpectedTokenException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.StructureNode
import it.unibo.tuprolog.parser.tree.SyntaxKind

class PublicApiContractSpec :
    FunSpec({
        fun chunksOf(
            text: String,
            chunkSize: Int,
        ): TextChunkSource {
            var index = 0
            return TextChunkSource {
                if (index >= text.length) {
                    null
                } else {
                    val end = (index + chunkSize).coerceAtMost(text.length)
                    text.substring(index, end).also { index = end }
                }
            }
        }

        test("source-text-origin-is-preserved-by-lexing") {
            val origin = SourcePosition(offset = 100, line = 7, column = 11)
            val input = testLexer.lex(SourceText("foo\nbar", "fragment", origin))
            val tokens = input.significantTokens()

            input.source.id shouldBe "fragment"
            tokens[0].span.start shouldBe origin
            tokens[0].span.endExclusive shouldBe SourcePosition(103, 7, 14)
            tokens[1].span.start shouldBe SourcePosition(104, 8, 0)
            tokens[1].span.endExclusive shouldBe SourcePosition(107, 8, 3)
        }

        test("source-text-and-streaming-source-agree-at-every-cr-lf-offset") {
            val text = "a\r\nb\rc\nd"
            val materialized = SourceText(text)
            val streamed = testLexer.lex(chunksOf(text, 1)).materialize().source

            for (offset in 0..text.length) {
                withClue("offset $offset") { streamed.positionAt(offset) shouldBe materialized.positionAt(offset) }
            }
        }

        test("session-parses-multiple-terms-incrementally") {
            val session = testParser.openSession(lex("first. second. third"))

            (
                session
                    .parseNextTerm()
                    .shouldNotBeNull()
                    .root
                    .shouldBeInstanceOf<StructureNode>()
                    .functor
            ) shouldBe
                "first"
            (
                session
                    .parseNextTerm()
                    .shouldNotBeNull()
                    .root
                    .shouldBeInstanceOf<StructureNode>()
                    .functor
            ) shouldBe
                "second"
            (
                session
                    .parseNextTerm()
                    .shouldNotBeNull()
                    .root
                    .shouldBeInstanceOf<StructureNode>()
                    .functor
            ) shouldBe
                "third"
            session.isAtEnd.shouldBeTrue()
            session.parseNextTerm().shouldBeNull()
        }

        test("session-term-requires-a-separator-unless-at-end-of-input") {
            val session = testParser.openSession(lex("first second."))
            shouldThrow<UnexpectedTokenException> { session.parseNextTerm() }
            session.currentPosition shouldBe SourcePosition(0, 0, 0)
        }

        test("session-term-uses-mutable-operator-table") {
            val session = testParser.openSession(lex("a ++ b. c ++ d."))
            session.operators.define("++", Associativity.YFX, 500)

            val first =
                session
                    .parseNextTerm()
                    .shouldNotBeNull()
                    .root
                    .shouldBeInstanceOf<OperatorExpressionNode>()
            first.operator.definition.name shouldBe "++"
            session.operators.removeAll("++")
            shouldThrow<UnexpectedTokenException> { session.parseNextTerm() }
        }

        test("parser-entry-points-have-distinct-contracts") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))

            testParser.parseTerm(lex("a.")).root.kind shouldBe SyntaxKind.STRUCTURE
            shouldThrow<UnexpectedTokenException> { testParser.parseTerm(lex("a + b"), operators) }
            testParser.parseExpression(lex("a + b."), operators).root.kind shouldBe SyntaxKind.INFIX_OPERATOR_EXPRESSION
            testParser.parseClause(lex("a + b."), operators).root.kind shouldBe SyntaxKind.CLAUSE
            (
                testParser
                    .parseTheory(lex("a. b."))
                    .root.clauses.size
            ) shouldBe 2
        }

        test("empty-theory-is-valid-but-empty-term-is-not") {
            (
                testParser
                    .parseTheory(lex("  % comment\n"))
                    .root.clauses
                    .isEmpty()
            ).shouldBeTrue()
            shouldThrow<PrologSyntaxException> { testParser.parseExpression(lex("  % comment\n")) }
        }
    })
