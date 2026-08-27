# Prolog Parser for Kotlin Multiplatform

A common-Kotlin lexer and parser for Prolog syntax with runtime-configurable operators.

The implementation has no runtime dependency beyond the Kotlin standard library. The test suite uses `kotlin-test`.

## Design

The parser is split into two public service interfaces:

- `PrologLexer`, implemented internally by `RegexPrologLexer`
- `PrologParser`, implemented internally by `PrattPrologParser`

Lexing is deliberately independent of the operator table. A word or graphic token is classified by lexical form, while the parser decides whether its occurrence is an atom, functor, prefix operator, infix operator, or postfix operator. This allows an entire source to be lexed once and then parsed clause by clause while the operator table changes.

The parser combines:

- predictive recursive descent for terms, structures, lists, blocks, clauses, and theories
- Pratt-style precedence climbing for dynamic operator expressions
- direct implementation of the Prolog `x` and `y` operand constraints
- no speculative grammar backtracking

## Source coordinates

All tokens and nodes carry:

- UTF-16 offset into the original Kotlin `String`
- zero-based line
- zero-based column
- end-exclusive source spans
- end-exclusive token ranges

Whitespace and comments are retained as trivia tokens. Concatenating every non-EOF token's source slice reconstructs the input exactly.

## Basic usage

```kotlin
import it.unibo.tuprolog.parser.*

val lexer = PrologLexers.default()
val parser = PrologParsers.default()

val source = lexer.lex(SourceText("a + b * c."))
val operators = OperatorTables.of(
    OperatorDefinition("+", OperatorSpecifier.YFX, 500),
    OperatorDefinition("*", OperatorSpecifier.YFX, 400),
)

val clause = parser.parseClause(source, operators)
println(clause.root.expression)
```

## Dynamic operators between clauses

```kotlin
val input = lexer.lex(
    SourceText(
        """
        first.
        a ++ b.
        """.trimIndent(),
    ),
)

val session = parser.openSession(input)

val first = session.parseNextClause()

// A future directive processor may derive this update from an op/3 directive.
session.operators.define("++", OperatorSpecifier.YFX, 500)

val second = session.parseNextClause()
```

The low-level parser does not execute `op/3` directives. The source reader or Prolog runtime is responsible for recognizing syntax-affecting directives and mutating `session.operators` between `parseNextClause` calls.

## Operator ambiguity

The default policy rejects multiple applicable definitions for the same occurrence:

```kotlin
val parser = PrologParsers.default(
    ParserOptions(ambiguityPolicy = OperatorAmbiguityPolicy.REJECT),
)
```

`LEGACY_ORDER` is available during migration from the ANTLR grammar. It reproduces its branch preference where possible.

## Concrete syntax tree

The result is a typed, immutable CST. It preserves source syntax rather than immediately converting terms to a domain model. Relevant interfaces include:

- `NumberNode`
- `VariableNode`
- `StructureNode`
- `ListNode`
- `BlockNode`
- `ParenthesizedExpressionNode`
- `OperatorExpressionNode`
- `ClauseNode`
- `TheoryNode`

`SyntaxTree.semanticTokens` assigns context-sensitive roles such as `FUNCTOR`, `INFIX_OPERATOR`, `ARGUMENT_DELIMITER`, and `LIST_TAIL_DELIMITER`. This is intended as the basis for semantic syntax coloring.

## Error handling

Lexical and syntactic failures are exceptions rooted at `PrologSyntaxException`. Each exception carries a stable error code, exact source span, offending text, expectations, and grammar rule path.

The initial API is strict and fail-fast. Error recovery is deliberately not exposed yet, but the implementation keeps diagnostic handling localized so a recovering mode can be introduced later.

## Tests

The common test suite covers:

- source coordinates and lossless token partitioning
- all literal classes, escapes, comments, and full-stop contexts
- structures, variables, lists, blocks, cut, and parenthesized expressions
- all seven operator specifiers
- associativity and mixed precedence
- contextual comma and pipe suppression
- semantic token roles
- typed diagnostics
- dynamic parse sessions and cursor rollback
- deterministic property tests for lexing, spans, and generated operator chains

See `docs/COMPATIBILITY.md` and `docs/MIGRATION.md` for migration decisions.
