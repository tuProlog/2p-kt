package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.exceptions.PrologParsingException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.tokens.TokenKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ParserTermTest {
    @Test
    fun parsesSignedIntegersEvenWithTriviaAfterTheSign() {
        val node = assertIs<NumberNode>(parseTerm("- /* sign */ 0xff"))
        assertEquals(NumberKind.HEX_INTEGER, node.numberKind)
        assertEquals(-1, node.sign)
        assertEquals(16, node.radix)
        assertEquals("ff", node.digits)
        assertEquals(0, node.span.start.offset)
        assertEquals(17, node.span.endExclusive.offset)
    }

    @Test
    fun parsesRealNumbers() {
        val node = assertIs<NumberNode>(parseTerm("+1.25e-2"))
        assertEquals(NumberKind.REAL, node.numberKind)
        assertEquals(SyntaxKind.REAL, node.kind)
        assertEquals(1, node.sign)
        assertEquals("1.25e-2", node.digits)
    }

    @Test
    fun distinguishesAnonymousAndNamedVariables() {
        assertTrue(assertIs<VariableNode>(parseTerm("_")).isAnonymous)
        assertFalse(assertIs<VariableNode>(parseTerm("_X")).isAnonymous)
    }

    @Test
    fun parsesOrdinaryAndQuotedStructures() {
        val atom = assertIs<StructureNode>(parseTerm("foo"))
        assertEquals(StructureKind.ORDINARY, atom.structureKind)
        assertEquals("foo", atom.functor)
        assertTrue(atom.arguments.isEmpty())

        val structure = assertIs<StructureNode>(parseTerm("'a b'(X, 1)"))
        assertEquals(StructureKind.SINGLE_QUOTED, structure.structureKind)
        assertEquals("a b", structure.functor)
        assertEquals(2, structure.arguments.size)
    }

    @Test
    fun doubleQuotedTextIsAZeroArityStructureOnly() {
        val node = assertIs<StructureNode>(parseTerm("\"text\""))
        assertEquals(StructureKind.DOUBLE_QUOTED, node.structureKind)
        assertEquals("text", node.functor)
        assertFailsWith<PrologParsingException> { parseTerm("\"text\"(x)") }
    }

    @Test
    fun parsesTruthCutAndEmptyForms() {
        for (truth in listOf("true", "false", "fail")) {
            val node = assertIs<StructureNode>(parseTerm(truth))
            assertEquals(StructureKind.TRUTH, node.structureKind)
            assertEquals(truth, node.functor)
        }
        assertEquals(StructureKind.CUT, assertIs<StructureNode>(parseTerm("!")).structureKind)
        assertEquals(StructureKind.EMPTY_LIST, assertIs<StructureNode>(parseTerm("[ ]")).structureKind)
        assertEquals(StructureKind.EMPTY_BLOCK, assertIs<StructureNode>(parseTerm("{\n}")).structureKind)
    }

    @Test
    fun parsesProperAndImproperLists() {
        val proper = assertIs<ListNode>(parseTerm("[a, b, c]"))
        assertEquals(3, proper.items.size)
        assertNull(proper.tail)

        val improper = assertIs<ListNode>(parseTerm("[a, b | Tail]"))
        assertEquals(2, improper.items.size)
        assertIs<VariableNode>(improper.tail)
    }

    @Test
    fun parsesNonEmptyBlocks() {
        val block = assertIs<BlockNode>(parseTerm("{a, f(X), 1}"))
        assertEquals(3, block.items.size)
    }

    @Test
    fun parenthesizedExpressionsAreAtomicToTheirSurroundings() {
        val operators = OperatorTables.of(op("=", Associativity.XFX, 700))
        val node = assertIs<ParenthesizedExpressionNode>(parseTerm("(a = b)", operators))
        assertEquals(0, node.priority)
        assertEquals(700, node.expression.priority)
    }

    @Test
    fun operatorsCanBeUsedAsExplicitZeroArityFunctors() {
        val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
        val node = assertIs<StructureNode>(parseTerm("(+)", operators))
        assertEquals(StructureKind.EXPLICIT_OPERATOR, node.structureKind)
        assertEquals("+", node.functor)
    }

    @Test
    fun nonPrefixOperatorsCanBeUsedAsFunctorApplications() {
        val operators = OperatorTables.of(op("rel", Associativity.XFX, 700))
        val node = assertIs<StructureNode>(parseTerm("rel(a, b)", operators))
        assertEquals("rel", node.functor)
        assertEquals(2, node.arguments.size)
    }

    @Test
    fun aPrefixOperatorFollowedByParenthesesIsParsedAsPrefixSyntax() {
        val operators = OperatorTables.of(op("not", Associativity.FY, 900))
        val node = operatorNode(parseExpression("not(a)", operators))
        assertEquals(OperatorRole.PREFIX, node.operator.role)
        assertIs<ParenthesizedExpressionNode>(node.rightOperand)
    }

    @Test
    fun argumentCommasRemainDelimitersWhenCommaIsAnOperator() {
        val operators = OperatorTables.of(op(",", Associativity.XFY, 1000))
        val node = assertIs<StructureNode>(parseTerm("f(a, b)", operators))
        assertEquals(2, node.arguments.size)
        assertTrue(node.arguments.none { it is OperatorExpressionNode })
    }

    @Test
    fun parenthesizedCommaExpressionsRemainAvailableInsideArguments() {
        val operators = OperatorTables.of(op(",", Associativity.XFY, 1000))
        val node = assertIs<StructureNode>(parseTerm("f((a, b))", operators))
        assertEquals(1, node.arguments.size)
        val parenthesized = assertIs<ParenthesizedExpressionNode>(node.arguments.single())
        assertIs<OperatorExpressionNode>(parenthesized.expression)
    }

    @Test
    fun semanticTokensDistinguishFunctorsFromArgumentDelimiters() {
        val tree = testParser.parseTerm(lex("f(a, b)"))
        val significant = tree.lexedSource.significantTokens()
        val f = significant.first { tree.lexedSource.textOf(it) == "f" }
        val comma = significant.first { it.kind == TokenKind.COMMA }
        assertEquals(SemanticRole.FUNCTOR, tree.semanticToken(f.id)?.role)
        assertEquals(SemanticRole.ARGUMENT_DELIMITER, tree.semanticToken(comma.id)?.role)
    }
}
