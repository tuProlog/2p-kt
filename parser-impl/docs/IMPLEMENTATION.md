# Implementation notes

## Lexer

The lexer uses first-character dispatch. Regular token families are matched with precompiled common `Regex` objects and `matchAt`. Stateful constructs use explicit scanners:

- comments
- single- and double-quoted text
- escapes
- character-code literals
- graphic atom candidates
- clause-terminating periods

The lexer must either advance or throw on every iteration. An EOF token is always appended.

## Parser

Structural productions are written as named `rule` blocks using a small internal grammar API:

- `expect`
- `accept`
- `unexpected`
- `nested`
- semantic annotation helpers

Dynamic expressions are parsed by precedence climbing. Each expression node records its root priority. Operand admissibility is computed directly from `OperandConstraint`:

```text
STRICT     operandPriority < operatorPriority
NON_STRICT operandPriority <= operatorPriority
```

The right-hand parse limit follows from the same constraint:

```text
STRICT     operatorPriority - 1
NON_STRICT operatorPriority
```

No static precedence levels are encoded in the grammar.

## Complexity

For an unambiguous operator table:

- lexing is linear in source length
- parsing is linear in significant token count
- tree memory is linear in parsed syntax size
- parser stack depth follows syntactic nesting and right-associative/prefix nesting

Left-associative and postfix chains are consumed iteratively. `ParserOptions.maximumNestingDepth` converts extreme recursive input into a typed failure.

Operator definitions are cached by name for the duration of one grammar invocation. A parse session creates a fresh grammar invocation for each clause, so table mutations between clauses are observed.

## Thread safety

`PrologLexer` and `PrologParser` instances are stateless and reusable. `MutableOperatorTable` and `PrologParseSession` are mutable and are not designed for concurrent mutation.
