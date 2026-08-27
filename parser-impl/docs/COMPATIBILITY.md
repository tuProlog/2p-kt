# Compatibility decisions

This implementation preserves the semantic core of the JVM/ANTLR parser while deliberately avoiding several implementation artifacts.

## Preserved

- priorities range from 1 through 1200, with lower numbers binding more strongly
- top-level expressions are parsed at priority 1200
- all seven specifiers are supported: `fx`, `fy`, `xf`, `yf`, `xfx`, `xfy`, and `yfx`
- `x` requires an operand priority strictly below the operator priority
- `y` permits an operand priority equal to the operator priority
- one operator name may have several simultaneous specifiers
- signed numeric syntax is recognized before prefix-operator syntax
- comma is disabled as an operator in argument and block item contexts
- comma and pipe are disabled as operators in list item contexts
- parentheses restart expression parsing at full priority and are atomic to the surrounding expression
- single-quoted text may be a functor; double-quoted text is zero-arity syntax
- `true`, `false`, `fail`, and cut retain their special roles
- operator syntax can change between clauses through a mutable parse session

## Intentional changes

### State-independent lexing

The ANTLR lexer changed `ATOM` into `OPERATOR` according to mutable runtime state. The new lexer emits lexical-form tokens such as `WORD_ATOM` and `GRAPHIC_ATOM`. Operator interpretation belongs to the parser.

This means already-tokenized input remains valid after an `op/3`-equivalent update.

### Lossless trivia

Whitespace and comments are retained as tokens instead of being skipped. Full-stop tokens contain only the period; following layout remains separate trivia.

### Structural empty forms

`[]` and `{}` are recognized from delimiter pairs rather than composite lexer tokens. The resulting CST still classifies them as empty-list and empty-block structures.

### Exact source model

Coordinates are zero-based and spans are end-exclusive. Offsets use Kotlin `String` indexing, hence UTF-16 code units.

### Ambiguity policy

Multiple applicable operator definitions are rejected by default. The old grammar implicitly chose according to alternative order. `OperatorAmbiguityPolicy.LEGACY_ORDER` is available for migration.

When a name has both an applicable infix and postfix definition, infix syntax is selected if a right operand can start; postfix syntax is the fallback otherwise.

### Operator removal

The public mutable table can remove one `(name, specifier)` pair or every definition for a name. This is finer-grained than the old `removeOperator(name)` operation.

### Invalid legacy grammar path

The apparently optional closing parenthesis in the ANTLR operator-functor application rule is not preserved. Every opening parenthesis must be closed.

### Numeric representation

Integer text is preserved as radix plus digits instead of being converted to `Long`. This avoids introducing a JVM-only or bounded numeric representation. A later term-mapping layer may select an arbitrary-precision multiplatform library.

## Not implemented at this layer

- execution of `op/3` directives
- a standard predefined operator table
- conversion from CST nodes to the tuProlog term hierarchy
- syntax-error recovery and multiple-diagnostic collection
- nested block comments
