# Implementation notes

## Lexer

The lexer uses first-character dispatch over a growable chunk buffer. Regular token families are matched with precompiled common `Regex` objects and `matchAt`. A match ending at an unfinished chunk boundary requests another chunk before it is committed. Stateful constructs use explicit scanners:

- comments
- single- and double-quoted text
- escapes
- character-code literals
- graphic atom candidates
- clause-terminating periods

The scanner either emits one token, requests another chunk, or throws. Retrying after another chunk does not mutate its cursor. An EOF token is emitted exactly once.

`LazyLexedSource` maintains absolute token IDs and significant-token indices. `TokenCursor` requests
significant tokens without consulting an input size, which would force EOF. A successful parse
session snapshots the completed clause and advances a commit watermark. With
`RELEASE_COMMITTED`, token records and source characters before that watermark are then discarded.

The current clause remains pinned. This preserves cursor rollback and makes every returned syntax
tree independent from subsequent eviction. Consequently, a configured hard token limit rejects a
single oversized clause rather than evicting data that is still semantically reachable.

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
- complete-tree memory is linear in parsed syntax size
- streaming-session working memory is linear in the current clause plus the input chunk
- parser stack depth follows syntactic nesting and right-associative/prefix nesting

Left-associative and postfix chains are consumed iteratively. `ParserOptions.maximumNestingDepth` converts extreme recursive input into a typed failure.

Operator definitions are cached by name for the duration of one grammar invocation. A parse session creates a fresh grammar invocation for each clause, so table mutations between clauses are observed.

## Thread safety

`PrologLexer` and `PrologParser` instances are stateless and reusable. A `LexedSource`,
`MutableOperatorTable`, and parse session are stateful and are not designed for concurrent access.
