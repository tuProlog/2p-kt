package it.unibo.tuprolog.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.parser.exceptions.PrologParsingException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.PatternNode
import it.unibo.tuprolog.parser.tree.SemanticRole
import it.unibo.tuprolog.parser.tree.SyntaxKind
import it.unibo.tuprolog.parser.tree.VariableNode

/** A variable followed by an argument list, e.g. `P(X, Y)`, is a [PatternNode]. */
class PatternSyntaxSpec :
    FunSpec({
        test("variable-with-arguments-is-a-pattern") {
            val node = parseTerm("P(X, a)").shouldBeInstanceOf<PatternNode>()
            node.kind shouldBe SyntaxKind.PATTERN
            node.functor.name shouldBe "P"
            node.arguments.size shouldBe 2
            node.span.start.offset shouldBe 0
            node.span.endExclusive.offset shouldBe 7
        }

        test("variable-alone-is-a-variable") {
            parseTerm("P").shouldBeInstanceOf<VariableNode>()
        }

        test("anonymous-variable-functor") {
            parseTerm("_(a)").shouldBeInstanceOf<PatternNode>().functor.isAnonymous shouldBe true
        }

        test("empty-arguments-are-rejected") {
            shouldThrow<PrologParsingException> { parseTerm("P()") }
        }

        test("nested-patterns") {
            val node = parseTerm("P(Q(X))").shouldBeInstanceOf<PatternNode>()
            node.arguments.single().shouldBeInstanceOf<PatternNode>()
        }

        test("pattern-as-operand") {
            val operators = OperatorTables.of(op(":-", Associativity.XFX, 1200))
            val root = parseExpression("P(X) :- Q(X)", operators).shouldBeInstanceOf<OperatorExpressionNode>()
            root.leftOperand.shouldBeInstanceOf<PatternNode>()
            root.rightOperand.shouldBeInstanceOf<PatternNode>()
        }

        test("functor-token-role") {
            val tree = testParser.parseTerm(lex("P(x)"), OperatorTables.empty())
            val functor = tree.root.shouldBeInstanceOf<PatternNode>().functor
            tree.semanticTokens.single { it.tokenId == functor.tokenId }.role shouldBe SemanticRole.FUNCTOR_VARIABLE
        }
    })
