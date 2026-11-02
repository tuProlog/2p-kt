package it.unibo.tuprolog.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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

class OperatorParserSpec :
    FunSpec({
        test("fx-rejects-an-unparenthesized-same-priority-operand") {
            val operators = OperatorTables.of(op("pre", Associativity.FX, 200))
            shouldThrow<PrologParsingException> {
                parseExpression("pre pre a", operators)
            }
        }

        test("fy-accepts-same-priority-prefix-nesting") {
            val operators = OperatorTables.of(op("pre", Associativity.FY, 200))
            val root = operatorNode(parseExpression("pre pre a", operators))
            root.rightOperand.shouldBeInstanceOf<OperatorExpressionNode>()
        }

        test("xf-rejects-same-priority-postfix-chaining") {
            val operators = OperatorTables.of(op("post", Associativity.XF, 200))
            shouldThrow<OperatorPriorityException> {
                parseExpression("a post post", operators)
            }
        }

        test("yf-accepts-same-priority-postfix-chaining") {
            val operators = OperatorTables.of(op("post", Associativity.YF, 200))
            val root = operatorNode(parseExpression("a post post", operators))
            root.leftOperand.shouldBeInstanceOf<OperatorExpressionNode>()
        }

        test("xfx-is-non-associative") {
            val operators = OperatorTables.of(op("=", Associativity.XFX, 700))
            shouldThrow<OperatorPriorityException> {
                parseExpression("a = b = c", operators)
            }
        }

        test("xfy-associates-to-the-right") {
            val operators = OperatorTables.of(op("^", Associativity.XFY, 200))
            val root = operatorNode(parseExpression("a ^ b ^ c", operators))
            root.operator.definition.name shouldBe "^"
            root.leftOperand.shouldBeInstanceOf<StructureNode>()
            root.rightOperand.shouldBeInstanceOf<OperatorExpressionNode>()
        }

        test("yfx-associates-to-the-left") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
            val root = operatorNode(parseExpression("a + b + c", operators))
            root.leftOperand.shouldBeInstanceOf<OperatorExpressionNode>()
            root.rightOperand.shouldBeInstanceOf<StructureNode>()
        }

        test("lower-numeric-priority-binds-more-strongly") {
            val operators =
                OperatorTables.of(
                    op("+", Associativity.YFX, 500),
                    op("*", Associativity.YFX, 400),
                )
            val root = operatorNode(parseExpression("a + b * c", operators))
            root.operator.definition.name shouldBe "+"
            operatorNode(root.rightOperand!!).operator.definition.name shouldBe "*"

            val inverse = operatorNode(parseExpression("a * b + c", operators))
            inverse.operator.definition.name shouldBe "+"
            operatorNode(inverse.leftOperand!!).operator.definition.name shouldBe "*"
        }

        test("parentheses-override-operator-priority") {
            val operators =
                OperatorTables.of(
                    op("+", Associativity.YFX, 500),
                    op("*", Associativity.YFX, 400),
                )
            val root = operatorNode(parseExpression("(a + b) * c", operators))
            root.operator.definition.name shouldBe "*"
            root.leftOperand.shouldBeInstanceOf<ParenthesizedExpressionNode>()
        }

        test("signed-numbers-take-precedence-over-prefix-operator-interpretation") {
            val operators = OperatorTables.of(op("-", Associativity.FY, 200))
            parseExpression("- 1", operators).shouldBeInstanceOf<NumberNode>()
            val prefix = operatorNode(parseExpression("- X", operators))
            prefix.operator.role shouldBe OperatorRole.PREFIX
        }

        test("infix-wins-over-postfix-when-a-right-operand-can-start") {
            val operators =
                OperatorTables.of(
                    op("op", Associativity.YF, 500),
                    op("op", Associativity.YFX, 500),
                )
            val infix = operatorNode(parseExpression("a op b", operators))
            infix.operator.role shouldBe OperatorRole.INFIX

            val postfix = operatorNode(parseExpression("a op", operators))
            postfix.operator.role shouldBe OperatorRole.POSTFIX
            postfix.rightOperand.shouldBeNull()
        }

        test("multiple-applicable-infix-definitions-are-rejected-by-default") {
            val operators =
                OperatorTables.of(
                    op("op", Associativity.XFY, 500),
                    op("op", Associativity.YFX, 500),
                )
            val error =
                shouldThrow<AmbiguousOperatorUseException> {
                    parseExpression("a op b", operators)
                }
            error.candidates.size shouldBe 2
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
            root.operator.definition.specifier shouldBe Associativity.YFX
        }

        test("comma-and-pipe-can-be-operators-at-top-level") {
            val comma = OperatorTables.of(op(",", Associativity.XFY, 1000))
            operatorNode(parseExpression("a, b", comma)).operator.definition.name shouldBe ","

            val pipe = OperatorTables.of(op("|", Associativity.XFY, 1100))
            operatorNode(parseExpression("a | b", pipe)).operator.definition.name shouldBe "|"
        }

        test("operator-nodes-expose-priority-and-semantic-role") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
            val tree = testParser.parseExpression(lex("a + b"), operators)
            val root = operatorNode(tree.root)
            root.priority shouldBe 500
            val token = tree.tokens[root.operator.tokenId]
            tree.lexedSource.textOf(token) shouldBe "+"
            tree.semanticToken(token.id)?.role shouldBe SemanticRole.INFIX_OPERATOR
        }
    })
