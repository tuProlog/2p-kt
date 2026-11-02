package it.unibo.tuprolog.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.parser.exceptions.PrologParsingException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.tokens.TokenKind
import it.unibo.tuprolog.parser.tree.BlockNode
import it.unibo.tuprolog.parser.tree.ListNode
import it.unibo.tuprolog.parser.tree.NumberKind
import it.unibo.tuprolog.parser.tree.NumberNode
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.OperatorRole
import it.unibo.tuprolog.parser.tree.ParenthesizedExpressionNode
import it.unibo.tuprolog.parser.tree.SemanticRole
import it.unibo.tuprolog.parser.tree.StructureKind
import it.unibo.tuprolog.parser.tree.StructureNode
import it.unibo.tuprolog.parser.tree.SyntaxKind
import it.unibo.tuprolog.parser.tree.VariableNode

class ParserTermSpec :
    FunSpec({
        test("parses-signed-integers-even-with-trivia-after-the-sign") {
            val node = parseTerm("- /* sign */ 0xff").shouldBeInstanceOf<NumberNode>()
            node.numberKind shouldBe NumberKind.HEX_INTEGER
            node.sign shouldBe -1
            node.radix shouldBe 16
            node.digits shouldBe "ff"
            node.span.start.offset shouldBe 0
            node.span.endExclusive.offset shouldBe 17
        }

        test("parses-real-numbers") {
            val node = parseTerm("+1.25e-2").shouldBeInstanceOf<NumberNode>()
            node.numberKind shouldBe NumberKind.REAL
            node.kind shouldBe SyntaxKind.REAL
            node.sign shouldBe 1
            node.digits shouldBe "1.25e-2"
        }

        test("distinguishes-anonymous-and-named-variables") {
            (parseTerm("_").shouldBeInstanceOf<VariableNode>().isAnonymous).shouldBeTrue()
            (parseTerm("_X").shouldBeInstanceOf<VariableNode>().isAnonymous).shouldBeFalse()
        }

        test("parses-ordinary-and-quoted-structures") {
            val atom = parseTerm("foo").shouldBeInstanceOf<StructureNode>()
            atom.structureKind shouldBe StructureKind.ORDINARY
            atom.functor shouldBe "foo"
            atom.arguments.isEmpty().shouldBeTrue()

            val structure = parseTerm("'a b'(X, 1)").shouldBeInstanceOf<StructureNode>()
            structure.structureKind shouldBe StructureKind.SINGLE_QUOTED
            structure.functor shouldBe "a b"
            structure.arguments.size shouldBe 2
        }

        test("double-quoted-text-is-a-zero-arity-structure-only") {
            val node = parseTerm("\"text\"").shouldBeInstanceOf<StructureNode>()
            node.structureKind shouldBe StructureKind.DOUBLE_QUOTED
            node.functor shouldBe "text"
            shouldThrow<PrologParsingException> { parseTerm("\"text\"(x)") }
        }

        withTests(nameFn = { "parses-truth/$it" }, listOf("true", "false", "fail")) { truth ->
            val node = parseTerm(truth).shouldBeInstanceOf<StructureNode>()
            node.structureKind shouldBe StructureKind.TRUTH
            node.functor shouldBe truth
        }

        test("parses-cut-and-empty-forms") {
            (parseTerm("!").shouldBeInstanceOf<StructureNode>().structureKind) shouldBe StructureKind.CUT
            (parseTerm("[ ]").shouldBeInstanceOf<StructureNode>().structureKind) shouldBe StructureKind.EMPTY_LIST
            (parseTerm("{\n}").shouldBeInstanceOf<StructureNode>().structureKind) shouldBe StructureKind.EMPTY_BLOCK
        }

        test("parses-proper-and-improper-lists") {
            val proper = parseTerm("[a, b, c]").shouldBeInstanceOf<ListNode>()
            proper.items.size shouldBe 3
            proper.tail.shouldBeNull()

            val improper = parseTerm("[a, b | Tail]").shouldBeInstanceOf<ListNode>()
            improper.items.size shouldBe 2
            improper.tail.shouldBeInstanceOf<VariableNode>()
        }

        test("parses-non-empty-blocks") {
            val block = parseTerm("{a, f(X), 1}").shouldBeInstanceOf<BlockNode>()
            block.items.size shouldBe 3
        }

        test("parenthesized-expressions-are-atomic-to-their-surroundings") {
            val operators = OperatorTables.of(op("=", Associativity.XFX, 700))
            val node = parseTerm("(a = b)", operators).shouldBeInstanceOf<ParenthesizedExpressionNode>()
            node.priority shouldBe 0
            node.expression.priority shouldBe 700
        }

        test("operators-can-be-used-as-explicit-zero-arity-functors") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
            val node = parseTerm("(+)", operators).shouldBeInstanceOf<StructureNode>()
            node.structureKind shouldBe StructureKind.EXPLICIT_OPERATOR
            node.functor shouldBe "+"
        }

        test("non-prefix-operators-can-be-used-as-functor-applications") {
            val operators = OperatorTables.of(op("rel", Associativity.XFX, 700))
            val node = parseTerm("rel(a, b)", operators).shouldBeInstanceOf<StructureNode>()
            node.functor shouldBe "rel"
            node.arguments.size shouldBe 2
        }

        test("a-prefix-operator-followed-by-parentheses-is-parsed-as-prefix-syntax") {
            val operators = OperatorTables.of(op("not", Associativity.FY, 900))
            val node = operatorNode(parseExpression("not(a)", operators))
            node.operator.role shouldBe OperatorRole.PREFIX
            node.rightOperand.shouldBeInstanceOf<ParenthesizedExpressionNode>()
        }

        test("argument-commas-remain-delimiters-when-comma-is-an-operator") {
            val operators = OperatorTables.of(op(",", Associativity.XFY, 1000))
            val node = parseTerm("f(a, b)", operators).shouldBeInstanceOf<StructureNode>()
            node.arguments.size shouldBe 2
            (node.arguments.none { it is OperatorExpressionNode }).shouldBeTrue()
        }

        test("parenthesized-comma-expressions-remain-available-inside-arguments") {
            val operators = OperatorTables.of(op(",", Associativity.XFY, 1000))
            val node = parseTerm("f((a, b))", operators).shouldBeInstanceOf<StructureNode>()
            node.arguments.size shouldBe 1
            val parenthesized = node.arguments.single().shouldBeInstanceOf<ParenthesizedExpressionNode>()
            parenthesized.expression.shouldBeInstanceOf<OperatorExpressionNode>()
        }

        test("semantic-tokens-distinguish-functors-from-argument-delimiters") {
            val tree = testParser.parseTerm(lex("f(a, b)"))
            val significant = tree.lexedSource.significantTokens()
            val f = significant.first { tree.lexedSource.textOf(it) == "f" }
            val comma = significant.first { it.kind == TokenKind.COMMA }
            tree.semanticToken(f.id)?.role shouldBe SemanticRole.FUNCTOR
            tree.semanticToken(comma.id)?.role shouldBe SemanticRole.ARGUMENT_DELIMITER
        }
    })
