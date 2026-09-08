# Default predicates

2P-Kt bundles a standard library of predicates, arithmetic functions and control rules as a single `Library`,
`CommonBuiltins` (package `it.unibo.tuprolog.solve.stdlib`, module `:solve`), aliased `"prolog.lang"`:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/stdlib/CommonBuiltins.kt:10:25"
```

It aggregates three independently-defined sets, each documented below. Any `SolverFactory` will include it when
built via `solverWithDefaultBuiltins(...)` / `mutableSolverWithDefaultBuiltins(...)` (see
[Solver API](solver-api.md#obtaining-a-solver)); `defaultBuiltins` on the factory *is* `CommonBuiltins`.

## Primitives (`CommonPrimitives`)

Native (Kotlin-implemented) predicates, each a `PrimitiveWrapper` (see
[Primitives and functions](primitives-and-functions.md)), collected in
`it.unibo.tuprolog.solve.stdlib.primitive`. Grouped by purpose:

| Category | Predicates |
|---|---|
| Type checking | `atom/1`, `atomic/1`, `callable/1`, `compound/1`, `float/1`, `integer/1`, `natural/1`, `number/1`, `nonvar/1`, `var/1`, `ground/1` |
| Term comparison (standard order) | `@</2`, `@=</2`, `@>/2`, `@>=/2`, `==/2`, `\==/2`, `=/2` (`unifies_with`), `\=/2` (`not_unifiable_with`) |
| Arithmetic comparison & evaluation | `is/2`, `=:=/2`, `=\=/2`, `</2`, `=</2`, `>/2`, `>=/2` |
| Term construction/inspection | `functor/3`, `arg/3`, `=../2` (univ), `copy_term/2` |
| Atom/number/string manipulation | `atom_chars/2`, `atom_codes/2`, `atom_concat/3`, `atom_length/2`, `char_code/2`, `number_chars/2`, `number_codes/2`, `sub_atom/5`, `nl/0` |
| Solution collection | `findall/3`, `bagof/3`, `setof/3` |
| Knowledge-base manipulation | `assert/1`, `asserta/1`, `assertz/1`, `retract/1`, `retractall/1`, `abolish/1`, `clause/2` |
| Flags & operators | `current_prolog_flag/2` (primitive form), `set_prolog_flag/2` (primitive form), `op/3`, `current_op/3` |
| Custom key-value storage | `get_ephemeral/2`/`set_ephemeral/2`, `get_persistent/2`/`set_persistent/2`, `get_durable/2`/`set_durable/2` — three tiers of `SideEffect`-backed context data with different lifetimes |
| Control & misc | `halt/0`, `halt/1`, `repeat/0`, `sleep/1`, `ensure_executable/1`, `write/1`, `between/3`, `reverse/2` |

## Functions (`CommonFunctions`)

Arithmetic functions used by `is/2` and the arithmetic-comparison primitives, each a `FunctionWrapper` (typically a
`MathFunction`), in `it.unibo.tuprolog.solve.stdlib.function`:

| Category | Functions |
|---|---|
| Basic arithmetic | `+/2`, `-/2`, `-/1`, `*/2`, `//2` (float division), `///2` (integer division), `mod/2`, `rem/2` |
| Rounding/parts | `round/1`, `ceiling/1`, `floor/1`, `truncate/1`, `float_integer_part/1`, `float_fractional_part/1`, `float/1` |
| Powers & roots | `**/2` (exponentiation), `sqrt/1`, `exp/1`, `log/1` |
| Trigonometry | `sin/1`, `cos/1`, `atan/1` |
| Sign & magnitude | `abs/1`, `sign/1` |
| Bitwise | `/\/2`, `\//2`, `\ /1` (complement), `<</2`, `>>/2` |

## Rules (`CommonRules`)

Control constructs and a couple of list predicates defined as actual Prolog clauses (built through `RuleWrapper`,
then loaded as `Clause`s into the library's theory), in `it.unibo.tuprolog.solve.stdlib.rule`:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/stdlib/CommonRules.kt:15:31"
```

- `\+/1` (`Not`) and `->/2` (`Arrow`, if-then) and `;/2` (`Semicolon`, both if-then-else and disjunction);
- `member/2` and `append/3`, each with a base and a recursive clause;
- `once/1`;
- `set_prolog_flag/2` / `current_prolog_flag/2` (the rule-level wrappers around the flag primitives).

Unlike primitives and functions, these are ordinary Prolog clauses — inspectable with `clause/2` like any other
user-defined predicate, and overridable by loading a static knowledge base that redefines them.
