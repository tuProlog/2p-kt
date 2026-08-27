package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.tokens.TokenKind
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PropertyTest {
    @Test
    fun generatedLexableInputsArePartitionedWithoutGaps() {
        val random = Random(0x5eed)
        val atoms =
            listOf(
                "foo",
                "X",
                "_",
                "123",
                "0xff",
                "1.25",
                "'quoted atom'",
                "\"text\"",
                "+",
                "->",
                "[a,b|T]",
                "{a,b}",
                "% comment\n",
                "/* block */",
            )

        repeat(100) {
            val sourceText =
                buildString {
                    repeat(random.nextInt(1, 40)) {
                        append(atoms[random.nextInt(atoms.size)])
                        append(if (random.nextBoolean()) ' ' else '\n')
                    }
                }
            val source = lex(sourceText)
            val nonEof = source.tokens.filter { it.kind != TokenKind.END_OF_INPUT }
            assertEquals(
                0,
                nonEof
                    .first()
                    .span.start.offset,
            )
            assertEquals(
                sourceText.length,
                nonEof
                    .last()
                    .span.endExclusive.offset,
            )
            nonEof.zipWithNext().forEach { (left, right) ->
                assertEquals(left.span.endExclusive.offset, right.span.start.offset)
            }
            assertEquals(sourceText, nonEof.joinToString("") { source.textOf(it) })
        }
    }

    @Test
    fun generatedYfxChainsAlwaysAssociateLeft() {
        val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
        for (length in 2..40) {
            val source = (0 until length).joinToString(" + ") { "a$it" }
            var node = operatorNode(parseExpression(source, operators))
            var operatorsSeen = 1
            while (node.leftOperand is OperatorExpressionNode) {
                node = operatorNode(node.leftOperand!!)
                operatorsSeen += 1
            }
            assertEquals(length - 1, operatorsSeen)
            assertIs<StructureNode>(node.leftOperand)
        }
    }

    @Test
    fun generatedXfyChainsAlwaysAssociateRight() {
        val operators = OperatorTables.of(op("^", Associativity.XFY, 200))
        for (length in 2..40) {
            val source = (0 until length).joinToString(" ^ ") { "a$it" }
            var node = operatorNode(parseExpression(source, operators))
            var operatorsSeen = 1
            while (node.rightOperand is OperatorExpressionNode) {
                node = operatorNode(node.rightOperand!!)
                operatorsSeen += 1
            }
            assertEquals(length - 1, operatorsSeen)
            assertIs<StructureNode>(node.rightOperand)
        }
    }

    @Test
    fun parsingIsDeterministicForAFixedOperatorEnvironment() {
        val operators =
            OperatorTables.of(
                op("+", Associativity.YFX, 500),
                op("*", Associativity.YFX, 400),
                op("^", Associativity.XFY, 200),
                op("not", Associativity.FY, 900),
            )
        val samples =
            listOf(
                "a + b * c",
                "not a + b",
                "a ^ b ^ c + d",
                "f((a + b), [c * d | T])",
            )

        for (sample in samples) {
            val first = testParser.parseExpression(lex(sample), operators)
            val second = testParser.parseExpression(lex(sample), operators)
            assertEquals(signature(first.root), signature(second.root))
            assertEquals(first.semanticTokens, second.semanticTokens)
        }
    }

    @Test
    fun everyGeneratedNodeSpanEnclosesItsTokenRange() {
        val operators =
            OperatorTables.of(
                op("+", Associativity.YFX, 500),
                op("*", Associativity.YFX, 400),
            )
        val tree = testParser.parseClause(lex("f([a + b, c * d | T], {x, y})."), operators)

        fun verify(node: SyntaxNode) {
            assertTrue(node.tokenRange.startInclusive <= node.tokenRange.endExclusive)
            if (node.tokenRange.startInclusive < node.tokenRange.endExclusive) {
                val first = tree.tokens[node.tokenRange.startInclusive]
                val last = tree.tokens[node.tokenRange.endExclusive - 1]
                assertEquals(first.span.start, node.span.start)
                assertEquals(last.span.endExclusive, node.span.endExclusive)
            }
            node.children.forEach(::verify)
        }
        verify(tree.root)
    }

    private fun signature(node: SyntaxNode): String =
        when (node) {
            is OperatorExpressionNode ->
                "${node.operator.definition.specifier}:${node.operator.definition.name}(" +
                    listOfNotNull(node.leftOperand, node.rightOperand).joinToString(",", transform = ::signature) +
                    ")"
            is StructureNode -> "${node.structureKind}:${node.functor}(${node.arguments.joinToString(
                ",",
                transform = ::signature,
            )})"
            is VariableNode -> "var:${node.name}"
            is NumberNode -> "number:${node.numberKind}:${node.sign}:${node.digits}"
            is ParenthesizedExpressionNode -> "paren:${signature(node.expression)}"
            is ListNode -> "list:${node.items.joinToString(
                ",",
                transform = ::signature,
            )}|${node.tail?.let(::signature)}"
            is BlockNode -> "block:${node.items.joinToString(",", transform = ::signature)}"
            is ClauseNode -> "clause:${signature(node.expression)}"
            is TheoryNode -> "theory:${node.clauses.joinToString(",", transform = ::signature)}"
            else -> node.kind.name
        }
}
