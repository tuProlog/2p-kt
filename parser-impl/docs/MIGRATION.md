# Migration from the JVM/ANTLR parser

## API mapping

| Legacy operation | Multiplatform equivalent |
|---|---|
| construct `PrologLexer(CharStream)` | `PrologLexers.default().lex(SourceText(text))` |
| construct generated `PrologParser` | `PrologParsers.default()` |
| `addOperator(name, assoc, priority)` | `MutableOperatorTable.define(name, specifier, priority)` |
| `removeOperator(name)` | `MutableOperatorTable.removeAll(name)` |
| `singletonTerm()` | `parseTerm(lexedSource, operators)` |
| `singletonExpression()` | `parseExpression(lexedSource, operators)` |
| `clause()` | `parseClause(lexedSource, operators)` |
| `theory()` | `parseTheory(lexedSource, operators)` |
| parse clauses while mutating operators | `openSession(...).parseNextClause()` |
| ANTLR token line/column | `Token.span.start.line` and `.column` |
| ANTLR context flags | typed CST interfaces and enum metadata |

## Suggested integration sequence

1. Add this module without removing the existing parser
2. Map both parsers' results into a small normalized representation
3. Run the existing parser corpus through both implementations on the JVM
4. Classify differences using `COMPATIBILITY.md`
5. Introduce a CST-to-term mapper
6. Introduce an `op/3` directive processor around `PrologParseSession`
7. Switch JVM consumers
8. Enable JS and Native consumers
9. Remove the ANTLR runtime dependency

## Directive processing boundary

A source reader should follow this shape:

```kotlin
val session = parser.openSession(lexedSource, initialOperators)

while (!session.isAtEnd) {
    val clause = session.parseNextClause() ?: break
    emit(clause)

    val declaration = recognizeOperatorDirective(clause.root)
    if (declaration != null) {
        applyOperatorDirective(declaration, session.operators)
    }
}
```

The operator table must not be mutated concurrently while one clause is being parsed. Mutation between `parseNextClause` calls is supported.
