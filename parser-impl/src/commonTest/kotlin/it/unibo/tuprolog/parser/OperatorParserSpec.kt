package it.unibo.tuprolog.parser

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.parser.exceptions.AmbiguousOperatorUseException
import it.unibo.tuprolog.parser.exceptions.OperatorPriorityException
import it.unibo.tuprolog.parser.exceptions.PrologParsingException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.tree.NumberNode
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.OperatorRole
import it.unibo.tuprolog.parser.tree.ParenthesizedExpressionNode
import it.unibo.tuprolog.parser.tree.SemanticRole
import it.unibo.tuprolog.parser.tree.StructureNode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class OperatorParserSpec :
    FunSpec({
        test("fx-rejects-an-unparenthesized-same-priority-operand") {
            val operators = OperatorTables.of(op("pre", Associativity.FX, 200))
            assertFailsWith<PrologParsingException> {
                parseExpression("pre pre a", operators)
            }
        }

        test("fy-accepts-same-priority-prefix-nesting") {
            val operators = OperatorTables.of(op("pre", Associativity.FY, 200))
            val root = operatorNode(parseExpression("pre pre a", operators))
            assertIs<OperatorExpressionNode>(root.rightOperand)
        }

        test("xf-rejects-same-priority-postfix-chaining") {
            val operators = OperatorTables.of(op("post", Associativity.XF, 200))
            assertFailsWith<OperatorPriorityException> {
                parseExpression("a post post", operators)
            }
        }

        test("yf-accepts-same-priority-postfix-chaining") {
            val operators = OperatorTables.of(op("post", Associativity.YF, 200))
            val root = operatorNode(parseExpression("a post post", operators))
            assertIs<OperatorExpressionNode>(root.leftOperand)
        }

        test("xfx-is-non-associative") {
            val operators = OperatorTables.of(op("=", Associativity.XFX, 700))
            assertFailsWith<OperatorPriorityException> {
                parseExpression("a = b = c", operators)
            }
        }

        test("xfy-associates-to-the-right") {
            val operators = OperatorTables.of(op("^", Associativity.XFY, 200))
            val root = operatorNode(parseExpression("a ^ b ^ c", operators))
            assertEquals("^", root.operator.definition.name)
            assertIs<StructureNode>(root.leftOperand)
            assertIs<OperatorExpressionNode>(root.rightOperand)
        }

        test("yfx-associates-to-the-left") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
            val root = operatorNode(parseExpression("a + b + c", operators))
            assertIs<OperatorExpressionNode>(root.leftOperand)
            assertIs<StructureNode>(root.rightOperand)
        }

        test("lower-numeric-priority-binds-more-strongly") {
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

        test("parentheses-override-operator-priority") {
            val operators =
                OperatorTables.of(
                    op("+", Associativity.YFX, 500),
                    op("*", Associativity.YFX, 400),
                )
            val root = operatorNode(parseExpression("(a + b) * c", operators))
            assertEquals("*", root.operator.definition.name)
            assertIs<ParenthesizedExpressionNode>(root.leftOperand)
        }

        test("signed-numbers-take-precedence-over-prefix-operator-interpretation") {
            val operators = OperatorTables.of(op("-", Associativity.FY, 200))
            assertIs<NumberNode>(parseExpression("- 1", operators))
            val prefix = operatorNode(parseExpression("- X", operators))
            assertEquals(OperatorRole.PREFIX, prefix.operator.role)
        }

        test("infix-wins-over-postfix-when-a-right-operand-can-start") {
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

        test("multiple-applicable-infix-definitions-are-rejected-by-default") {
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

        test("legacy-policy-uses-the-antlr-branch-order") {
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

        test("comma-and-pipe-can-be-operators-at-top-level") {
            val comma = OperatorTables.of(op(",", Associativity.XFY, 1000))
            assertEquals(",", operatorNode(parseExpression("a, b", comma)).operator.definition.name)

            val pipe = OperatorTables.of(op("|", Associativity.XFY, 1100))
            assertEquals("|", operatorNode(parseExpression("a | b", pipe)).operator.definition.name)
        }

        test("operator-nodes-expose-priority-and-semantic-role") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
            val tree = testParser.parseExpression(lex("a + b"), operators)
            val root = operatorNode(tree.root)
            assertEquals(500, root.priority)
            val token = tree.tokens[root.operator.tokenId]
            assertEquals("+", tree.lexedSource.textOf(token))
            assertEquals(SemanticRole.INFIX_OPERATOR, tree.semanticToken(token.id)?.role)
        }
    })
