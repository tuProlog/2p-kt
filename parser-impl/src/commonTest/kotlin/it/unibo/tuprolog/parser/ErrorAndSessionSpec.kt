package it.unibo.tuprolog.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.parser.exceptions.MissingClauseTerminatorException
import it.unibo.tuprolog.parser.exceptions.MissingOperatorOperandException
import it.unibo.tuprolog.parser.exceptions.NestingLimitExceededException
import it.unibo.tuprolog.parser.exceptions.SyntaxErrorCode
import it.unibo.tuprolog.parser.exceptions.UnexpectedEndOfInputException
import it.unibo.tuprolog.parser.exceptions.UnexpectedTokenException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.StructureNode
import it.unibo.tuprolog.parser.tree.SyntaxNode

class ErrorAndSessionSpec :
    FunSpec({
        test("option-safety-limits-must-be-positive") {
            shouldThrow<IllegalArgumentException> { LexerOptions(maximumRetainedTokens = 0) }
            shouldThrow<IllegalArgumentException> { LexerOptions(maximumRetainedTokens = -1) }
            shouldThrow<IllegalArgumentException> { ParserOptions(maximumNestingDepth = 0) }
            shouldThrow<IllegalArgumentException> { ParserOptions(maximumNestingDepth = -1) }
        }

        test("unexpected-tokens-expose-structured-diagnostic-data") {
            val error =
                shouldThrow<UnexpectedTokenException> {
                    parseTerm("f()")
                }
            error.code shouldBe SyntaxErrorCode.UNEXPECTED_TOKEN
            error.offendingText shouldBe ")"
            (error.expected.any { "argument" in it.description }).shouldBeTrue()
            error.rulePath.contains("arguments").shouldBeTrue()
        }

        test("incomplete-structures-report-unexpected-end-of-input") {
            val error = shouldThrow<UnexpectedEndOfInputException> { parseTerm("f(a") }
            error.code shouldBe SyntaxErrorCode.UNEXPECTED_END_OF_INPUT
            error.offendingText shouldBe null
            (error.expected.any { "parenthesis" in it.description }).shouldBeTrue()
            error.rulePath.contains("arguments").shouldBeTrue()
        }

        test("non-prefix-operators-at-expression-start-report-a-missing-left-operand") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
            val error =
                shouldThrow<MissingOperatorOperandException> {
                    parseExpression("+ a", operators)
                }
            error.side shouldBe "left"
        }

        test("missing-operator-operands-have-their-own-exception") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
            val error =
                shouldThrow<MissingOperatorOperandException> {
                    parseExpression("a +", operators)
                }
            error.side shouldBe "right"
            error.definition.specifier shouldBe Associativity.YFX
            error.code shouldBe SyntaxErrorCode.MISSING_OPERATOR_OPERAND
        }

        test("clauses-require-a-terminating-full-stop") {
            val error =
                shouldThrow<MissingClauseTerminatorException> {
                    testParser.parseClause(lex("a"))
                }
            error.code shouldBe SyntaxErrorCode.MISSING_CLAUSE_TERMINATOR
            error.span.start.offset shouldBe 1
        }

        test("diagnostics-retain-line-and-column-after-trivia") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
            val error =
                shouldThrow<MissingOperatorOperandException> {
                    parseExpression("a\n  + % no rhs\n", operators)
                }
            error.span.start.line shouldBe 1
            error.span.start.column shouldBe 2
        }

        test("nesting-depth-is-controlled-by-a-parser-option") {
            val parser = PrologParser.default(ParserOptions(maximumNestingDepth = 3))
            val error =
                shouldThrow<NestingLimitExceededException> {
                    parser.parseExpression(lex("((((a))))"))
                }
            error.maximumDepth shouldBe 3
        }

        test("theory-parsing-uses-one-fixed-operator-table") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
            val theory = testParser.parseTheory(lex("a + b.\nc + d."), operators).root
            theory.clauses.size shouldBe 2
            (theory.clauses.all { it.expression is OperatorExpressionNode }).shouldBeTrue()
        }

        test("an-op-directive-can-drive-the-following-clause") {
            val input = lex(":- op(500, yfx, ++).\na ++ b.")
            val session =
                testParser.openSession(
                    input,
                    OperatorTables.of(op(":-", Associativity.FX, 1200)),
                )

            val directive = session.parseNextClause()!!.root.expression
            val directiveOperator = directive.shouldBeInstanceOf<OperatorExpressionNode>()
            directiveOperator.operator.definition.name shouldBe ":-"
            val declaration = directiveOperator.rightOperand.shouldBeInstanceOf<StructureNode>()
            declaration.functor shouldBe "op"
            declaration.arguments.size shouldBe 3
            (declaration.arguments[2].shouldBeInstanceOf<StructureNode>().functor) shouldBe "++"

            session.operators.define("++", Associativity.YFX, 500)
            val following = session.parseNextClause()!!.root.expression
            (
                following
                    .shouldBeInstanceOf<OperatorExpressionNode>()
                    .operator.definition.name
            ) shouldBe "++"
            session.isAtEnd.shouldBeTrue()
        }

        test("parse-sessions-observe-operator-mutations-without-relexing") {
            val input = lex("first.\na ++ b.\nc ++ d.")
            val originalTokenKinds = input.tokens.map(Token::kind)
            val session = testParser.openSession(input)

            val first = session.parseNextClause().shouldNotBeNull()
            structureNode(first.root.expression).functor shouldBe "first"

            session.operators.define("++", Associativity.YFX, 500)
            session
                .parseNextClause()!!
                .root.expression
                .shouldBeInstanceOf<OperatorExpressionNode>()

            session.operators.removeAll("++")
            shouldThrow<MissingClauseTerminatorException> {
                session.parseNextClause()
            }

            input.tokens.map(Token::kind) shouldBe originalTokenKinds
        }

        test("failed-session-parsing-restores-the-cursor") {
            val session = testParser.openSession(lex("a + ."), OperatorTables.of(op("+", Associativity.YFX, 500)))
            val before = session.currentPosition
            shouldThrow<MissingOperatorOperandException> { session.parseNextClause() }
            session.currentPosition shouldBe before
            shouldThrow<MissingOperatorOperandException> { session.parseNextClause() }
            session.currentPosition shouldBe before
        }

        test("session-reports-end-of-input-after-the-last-clause") {
            val session = testParser.openSession(lex("a."))
            session.isAtEnd.shouldBeFalse()
            session.parseNextClause().shouldNotBeNull()
            session.isAtEnd.shouldBeTrue()
            session.parseNextClause().shouldBeNull()
        }

        test("node-spans-enclose-all-children") {
            val operators =
                OperatorTables.of(
                    op("+", Associativity.YFX, 500),
                    op("*", Associativity.YFX, 400),
                )
            val root = testParser.parseExpression(lex("a + f(b * c)"), operators).root

            fun verify(node: SyntaxNode) {
                for (child in node.children) {
                    (child.span.start.offset >= node.span.start.offset).shouldBeTrue()
                    (child.span.endExclusive.offset <= node.span.endExclusive.offset).shouldBeTrue()
                    verify(child)
                }
            }
            verify(root)
        }

        test("source-spans-exclude-trailing-trivia-and-optional-full-stops") {
            val tree = testParser.parseExpression(lex("a + b.   "), OperatorTables.of(op("+", Associativity.YFX, 500)))
            tree.root.span.endExclusive.offset shouldBe 5
            tree.source.text(tree.root.span) shouldBe "a + b"
        }
    })
