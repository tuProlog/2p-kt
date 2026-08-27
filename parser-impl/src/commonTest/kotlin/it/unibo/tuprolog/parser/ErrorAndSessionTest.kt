package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.exceptions.MissingClauseTerminatorException
import it.unibo.tuprolog.parser.exceptions.MissingOperatorOperandException
import it.unibo.tuprolog.parser.exceptions.NestingLimitExceededException
import it.unibo.tuprolog.parser.exceptions.SyntaxErrorCode
import it.unibo.tuprolog.parser.exceptions.UnexpectedTokenException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.tokens.Token
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ErrorAndSessionTest {
    @Test
    fun unexpectedTokensExposeStructuredDiagnosticData() {
        val error =
            assertFailsWith<UnexpectedTokenException> {
                parseTerm("f()")
            }
        assertEquals(SyntaxErrorCode.UNEXPECTED_TOKEN, error.code)
        assertEquals(")", error.offendingText)
        assertTrue(error.expected.any { "argument" in it.description })
        assertTrue(error.rulePath.contains("arguments"))
    }

    @Test
    fun nonPrefixOperatorsAtExpressionStartReportAMissingLeftOperand() {
        val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
        val error =
            assertFailsWith<MissingOperatorOperandException> {
                parseExpression("+ a", operators)
            }
        assertEquals("left", error.side)
    }

    @Test
    fun missingOperatorOperandsHaveTheirOwnException() {
        val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
        val error =
            assertFailsWith<MissingOperatorOperandException> {
                parseExpression("a +", operators)
            }
        assertEquals("right", error.side)
        assertEquals(Associativity.YFX, error.definition.specifier)
        assertEquals(SyntaxErrorCode.MISSING_OPERATOR_OPERAND, error.code)
    }

    @Test
    fun clausesRequireATerminatingFullStop() {
        val error =
            assertFailsWith<MissingClauseTerminatorException> {
                testParser.parseClause(lex("a"))
            }
        assertEquals(SyntaxErrorCode.MISSING_CLAUSE_TERMINATOR, error.code)
        assertEquals(1, error.span.start.offset)
    }

    @Test
    fun diagnosticsRetainLineAndColumnAfterTrivia() {
        val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
        val error =
            assertFailsWith<MissingOperatorOperandException> {
                parseExpression("a\n  + % no rhs\n", operators)
            }
        assertEquals(1, error.span.start.line)
        assertEquals(2, error.span.start.column)
    }

    @Test
    fun nestingDepthIsControlledByAParserOption() {
        val parser = PrologParser.default(ParserOptions(maximumNestingDepth = 3))
        val error =
            assertFailsWith<NestingLimitExceededException> {
                parser.parseExpression(lex("((((a))))"))
            }
        assertEquals(3, error.maximumDepth)
    }

    @Test
    fun theoryParsingUsesOneFixedOperatorTable() {
        val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
        val theory = testParser.parseTheory(lex("a + b.\nc + d."), operators).root
        assertEquals(2, theory.clauses.size)
        assertTrue(theory.clauses.all { it.expression is OperatorExpressionNode })
    }

    @Test
    fun anOpDirectiveCanDriveTheFollowingClause() {
        val input = lex(":- op(500, yfx, ++).\na ++ b.")
        val session =
            testParser.openSession(
                input,
                OperatorTables.of(op(":-", Associativity.FX, 1200)),
            )

        val directive = session.parseNextClause()!!.root.expression
        val directiveOperator = assertIs<OperatorExpressionNode>(directive)
        assertEquals(":-", directiveOperator.operator.definition.name)
        val declaration = assertIs<StructureNode>(directiveOperator.rightOperand)
        assertEquals("op", declaration.functor)
        assertEquals(3, declaration.arguments.size)
        assertEquals("++", assertIs<StructureNode>(declaration.arguments[2]).functor)

        session.operators.define("++", Associativity.YFX, 500)
        val following = session.parseNextClause()!!.root.expression
        assertEquals("++", assertIs<OperatorExpressionNode>(following).operator.definition.name)
        assertTrue(session.isAtEnd)
    }

    @Test
    fun parseSessionsObserveOperatorMutationsWithoutRelexing() {
        val input = lex("first.\na ++ b.\nc ++ d.")
        val originalTokenKinds = input.tokens.map(Token::kind)
        val session = testParser.openSession(input)

        val first = assertNotNull(session.parseNextClause())
        assertEquals("first", structureNode(first.root.expression).functor)

        session.operators.define("++", Associativity.YFX, 500)
        assertIs<OperatorExpressionNode>(session.parseNextClause()!!.root.expression)

        session.operators.removeAll("++")
        assertFailsWith<MissingClauseTerminatorException> {
            session.parseNextClause()
        }

        assertEquals(originalTokenKinds, input.tokens.map(Token::kind))
    }

    @Test
    fun failedSessionParsingRestoresTheCursor() {
        val session = testParser.openSession(lex("a + ."), OperatorTables.of(op("+", Associativity.YFX, 500)))
        val before = session.currentPosition
        assertFailsWith<MissingOperatorOperandException> { session.parseNextClause() }
        assertEquals(before, session.currentPosition)
        assertFailsWith<MissingOperatorOperandException> { session.parseNextClause() }
        assertEquals(before, session.currentPosition)
    }

    @Test
    fun sessionReportsEndOfInputAfterTheLastClause() {
        val session = testParser.openSession(lex("a."))
        assertFalse(session.isAtEnd)
        assertNotNull(session.parseNextClause())
        assertTrue(session.isAtEnd)
        assertNull(session.parseNextClause())
    }

    @Test
    fun nodeSpansEncloseAllChildren() {
        val operators =
            OperatorTables.of(
                op("+", Associativity.YFX, 500),
                op("*", Associativity.YFX, 400),
            )
        val root = testParser.parseExpression(lex("a + f(b * c)"), operators).root

        fun verify(node: SyntaxNode) {
            for (child in node.children) {
                assertTrue(child.span.start.offset >= node.span.start.offset)
                assertTrue(child.span.endExclusive.offset <= node.span.endExclusive.offset)
                verify(child)
            }
        }
        verify(root)
    }

    @Test
    fun sourceSpansExcludeTrailingTriviaAndOptionalFullStops() {
        val tree = testParser.parseExpression(lex("a + b.   "), OperatorTables.of(op("+", Associativity.YFX, 500)))
        assertEquals(5, tree.root.span.endExclusive.offset)
        assertEquals(
            "a + b",
            tree.source.text.substring(tree.root.span.start.offset, tree.root.span.endExclusive.offset),
        )
    }
}
