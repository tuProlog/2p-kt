package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.StructureNode
import it.unibo.tuprolog.parser.tree.TermNode

internal val testLexer: PrologLexer = PrologLexer.default()
internal val testParser: PrologParser = PrologParser.default()

internal fun lex(
    text: String,
    id: String? = "test",
): LexedSource = testLexer.lex(SourceText(text, id))

internal fun parseTerm(
    text: String,
    operators: OperatorTable = OperatorTables.empty(),
): TermNode = testParser.parseTerm(lex(text), operators).root

internal fun parseExpression(
    text: String,
    operators: OperatorTable = OperatorTables.empty(),
): ExpressionNode = testParser.parseExpression(lex(text), operators).root

internal fun op(
    name: String,
    specifier: Associativity,
    priority: Int,
): OperatorDefinition = OperatorDefinition(name, specifier, priority)

internal fun operatorNode(node: ExpressionNode): OperatorExpressionNode =
    node as? OperatorExpressionNode
        ?: error("Expected operator expression, found ${node.kind}")

internal fun structureNode(node: ExpressionNode): StructureNode =
    node as? StructureNode
        ?: error("Expected structure, found ${node.kind}")
