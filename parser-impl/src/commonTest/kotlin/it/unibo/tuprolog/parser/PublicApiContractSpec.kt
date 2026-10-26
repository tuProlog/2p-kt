package it.unibo.tuprolog.parser

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException
import it.unibo.tuprolog.parser.exceptions.UnexpectedTokenException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.StructureNode
import it.unibo.tuprolog.parser.tree.SyntaxKind
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

            assertEquals("fragment", input.source.id)
            assertEquals(origin, tokens[0].span.start)
            assertEquals(SourcePosition(103, 7, 14), tokens[0].span.endExclusive)
            assertEquals(SourcePosition(104, 8, 0), tokens[1].span.start)
            assertEquals(SourcePosition(107, 8, 3), tokens[1].span.endExclusive)
        }

        test("source-text-and-streaming-source-agree-at-every-cr-lf-offset") {
            val text = "a\r\nb\rc\nd"
            val materialized = SourceText(text)
            val streamed = testLexer.lex(chunksOf(text, 1)).materialize().source

            for (offset in 0..text.length) {
                assertEquals(materialized.positionAt(offset), streamed.positionAt(offset), "offset $offset")
            }
        }

        test("session-parses-multiple-terms-incrementally") {
            val session = testParser.openSession(lex("first. second. third"))

            assertEquals("first", assertIs<StructureNode>(assertNotNull(session.parseNextTerm()).root).functor)
            assertEquals("second", assertIs<StructureNode>(assertNotNull(session.parseNextTerm()).root).functor)
            assertEquals("third", assertIs<StructureNode>(assertNotNull(session.parseNextTerm()).root).functor)
            assertTrue(session.isAtEnd)
            assertNull(session.parseNextTerm())
        }

        test("session-term-requires-a-separator-unless-at-end-of-input") {
            val session = testParser.openSession(lex("first second."))
            assertFailsWith<UnexpectedTokenException> { session.parseNextTerm() }
            assertEquals(SourcePosition(0, 0, 0), session.currentPosition)
        }

        test("session-term-uses-mutable-operator-table") {
            val session = testParser.openSession(lex("a ++ b. c ++ d."))
            session.operators.define("++", Associativity.YFX, 500)

            val first = assertIs<OperatorExpressionNode>(assertNotNull(session.parseNextTerm()).root)
            assertEquals("++", first.operator.definition.name)
            session.operators.removeAll("++")
            assertFailsWith<UnexpectedTokenException> { session.parseNextTerm() }
        }

        test("parser-entry-points-have-distinct-contracts") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))

            assertEquals(SyntaxKind.STRUCTURE, testParser.parseTerm(lex("a.")).root.kind)
            assertFailsWith<UnexpectedTokenException> { testParser.parseTerm(lex("a + b"), operators) }
            assertEquals(
                SyntaxKind.INFIX_OPERATOR_EXPRESSION,
                testParser.parseExpression(lex("a + b."), operators).root.kind,
            )
            assertEquals(SyntaxKind.CLAUSE, testParser.parseClause(lex("a + b."), operators).root.kind)
            assertEquals(
                2,
                testParser
                    .parseTheory(lex("a. b."))
                    .root.clauses.size,
            )
        }

        test("empty-theory-is-valid-but-empty-term-is-not") {
            assertTrue(
                testParser
                    .parseTheory(lex("  % comment\n"))
                    .root.clauses
                    .isEmpty(),
            )
            assertFailsWith<PrologSyntaxException> { testParser.parseExpression(lex("  % comment\n")) }
        }
    })
