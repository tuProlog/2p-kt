package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.exceptions.AmbiguousOperatorUseException
import it.unibo.tuprolog.parser.exceptions.OperatorPriorityException
import it.unibo.tuprolog.parser.exceptions.PrologParsingException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTables
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class OperatorParserTest {
    @Test
    fun fxRejectsAnUnparenthesizedSamePriorityOperand() {
        val operators = OperatorTables.of(op("pre", Associativity.FX, 200))
        assertFailsWith<PrologParsingException> {
            parseExpression("pre pre a", operators)
        }
    }

    @Test
    fun fyAcceptsSamePriorityPrefixNesting() {
        val operators = OperatorTables.of(op("pre", Associativity.FY, 200))
        val root = operatorNode(parseExpression("pre pre a", operators))
        assertIs<OperatorExpressionNode>(root.rightOperand)
    }

    @Test
    fun xfRejectsSamePriorityPostfixChaining() {
        val operators = OperatorTables.of(op("post", Associativity.XF, 200))
        assertFailsWith<OperatorPriorityException> {
            parseExpression("a post post", operators)
        }
    }

    @Test
    fun yfAcceptsSamePriorityPostfixChaining() {
        val operators = OperatorTables.of(op("post", Associativity.YF, 200))
        val root = operatorNode(parseExpression("a post post", operators))
        assertIs<OperatorExpressionNode>(root.leftOperand)
    }

    @Test
    fun xfxIsNonAssociative() {
        val operators = OperatorTables.of(op("=", Associativity.XFX, 700))
        assertFailsWith<OperatorPriorityException> {
            parseExpression("a = b = c", operators)
        }
    }

    @Test
    fun xfyAssociatesToTheRight() {
        val operators = OperatorTables.of(op("^", Associativity.XFY, 200))
        val root = operatorNode(parseExpression("a ^ b ^ c", operators))
        assertEquals("^", root.operator.definition.name)
        assertIs<StructureNode>(root.leftOperand)
        assertIs<OperatorExpressionNode>(root.rightOperand)
    }

    @Test
    fun yfxAssociatesToTheLeft() {
        val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
        val root = operatorNode(parseExpression("a + b + c", operators))
        assertIs<OperatorExpressionNode>(root.leftOperand)
        assertIs<StructureNode>(root.rightOperand)
    }

    @Test
    fun lowerNumericPriorityBindsMoreStrongly() {
        val operators =
            OperatorTables.of(
                op("+", Associativity.YFX, 500),
                op("*", Associativity.YFX, 400),
            )
        val root = operatorNode(parseExpression("a + b * c", operators))
        assertEquals("+", root.operator.definition.name)
        assertEquals("*", operatorNode(root.rightOperand!!).operator.definition.name)

        val inverse = operatorNode(parseExpression("a * b + c", operators))
        assertEquals("+", inverse.operator.definition.name)
        assertEquals("*", operatorNode(inverse.leftOperand!!).operator.definition.name)
    }

    @Test
    fun parenthesesOverrideOperatorPriority() {
        val operators =
            OperatorTables.of(
                op("+", Associativity.YFX, 500),
                op("*", Associativity.YFX, 400),
            )
        val root = operatorNode(parseExpression("(a + b) * c", operators))
        assertEquals("*", root.operator.definition.name)
        assertIs<ParenthesizedExpressionNode>(root.leftOperand)
    }

    @Test
    fun signedNumbersTakePrecedenceOverPrefixOperatorInterpretation() {
        val operators = OperatorTables.of(op("-", Associativity.FY, 200))
        assertIs<NumberNode>(parseExpression("- 1", operators))
        val prefix = operatorNode(parseExpression("- X", operators))
        assertEquals(OperatorRole.PREFIX, prefix.operator.role)
    }

    @Test
    fun infixWinsOverPostfixWhenARightOperandCanStart() {
        val operators =
            OperatorTables.of(
                op("op", Associativity.YF, 500),
                op("op", Associativity.YFX, 500),
            )
        val infix = operatorNode(parseExpression("a op b", operators))
        assertEquals(OperatorRole.INFIX, infix.operator.role)

        val postfix = operatorNode(parseExpression("a op", operators))
        assertEquals(OperatorRole.POSTFIX, postfix.operator.role)
        assertNull(postfix.rightOperand)
    }

    @Test
    fun multipleApplicableInfixDefinitionsAreRejectedByDefault() {
        val operators =
            OperatorTables.of(
                op("op", Associativity.XFY, 500),
                op("op", Associativity.YFX, 500),
            )
        val error =
            assertFailsWith<AmbiguousOperatorUseException> {
                parseExpression("a op b", operators)
            }
        assertEquals(2, error.candidates.size)
    }

    @Test
    fun legacyPolicyUsesTheAntlrBranchOrder() {
        val parser =
            PrologParser.default(
                ParserOptions(ambiguityPolicy = OperatorAmbiguityPolicy.LEGACY_ORDER),
            )
        val operators =
            OperatorTables.of(
                op("op", Associativity.XFY, 500),
                op("op", Associativity.YFX, 500),
            )
        val root = operatorNode(parser.parseExpression(lex("a op b"), operators).root)
        assertEquals(Associativity.YFX, root.operator.definition.specifier)
    }

    @Test
    fun commaAndPipeCanBeOperatorsAtTopLevel() {
        val comma = OperatorTables.of(op(",", Associativity.XFY, 1000))
        assertEquals(",", operatorNode(parseExpression("a, b", comma)).operator.definition.name)

        val pipe = OperatorTables.of(op("|", Associativity.XFY, 1100))
        assertEquals("|", operatorNode(parseExpression("a | b", pipe)).operator.definition.name)
    }

    @Test
    fun operatorNodesExposePriorityAndSemanticRole() {
        val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
        val tree = testParser.parseExpression(lex("a + b"), operators)
        val root = operatorNode(tree.root)
        assertEquals(500, root.priority)
        val token = tree.tokens[root.operator.tokenId]
        assertEquals("+", tree.lexedSource.textOf(token))
        assertEquals(SemanticRole.INFIX_OPERATOR, tree.semanticToken(token.id)?.role)
    }
}
