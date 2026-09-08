# Prolog DSL

2P-Kt offers a Kotlin DSL for building terms, theories and queries without going through the textual Prolog parser.
It is layered across four modules, each adding capabilities to a `LogicProgrammingScope`:

| Module | Adds |
|---|---|
| `:dsl-core` | term construction (`LogicProgrammingScope`) |
| `:dsl-unify` | unification operators on top of `:dsl-core` |
| `:dsl-theory` | theory/clause management on top of `:dsl-unify` |
| `:dsl-solve` | goal resolution on top of `:dsl-theory` |

Each module exposes the same three entry-point functions — `logicProgramming { ... }`, its short alias `lp { ... }`,
and (module-specific) `prolog { ... }` — that open a scope, run the given lambda as its receiver, and return the
lambda's result. `:dsl-solve`'s `prolog { }` is the most complete one, since `LogicProgrammingScope` there extends
all the others:

```kotlin
--8<-- "dsl-solve/src/commonMain/kotlin/it/unibo/tuprolog/dsl/solve/DSL.kt:11:29"
```

## Term construction (`:dsl-core`)

`LogicProgrammingScope` (`it.unibo.tuprolog.dsl`) composes several mixins: `MinimalLogicProgrammingScope` (structs,
lists, clauses), `LogicProgrammingScopeWithVariables` (fresh variables), `LogicProgrammingScopeWithOperators`
(infix arithmetic/relational/logical operators), `LogicProgrammingScopeWithSubstitutions`, and
`LogicProgrammingScopeWithPrologStandardLibrary`.

Inside a scope, any `String` can be turned into a `Struct` by "calling" it with arguments, and `Any` (numbers,
strings, terms) is auto-converted to a `Term` via `Termificator`:

```kotlin
"parent"("abraham", "isaac")     // Struct: parent(abraham, isaac)
"ancestor"("X", "Y")             // "X"/"Y" become Vars in the right position, atoms otherwise
```

Other builders: `structOf(functor, *args)`, `logicListOf(*items)`/`logicList(*items, tail = ...)`, `tupleOf(*items)`,
`blockOf(*items)`, `consOf(head, tail)`, `factOf(term)`, `directiveOf(term, *terms)`, and `rule { }` / `fact { }` /
`directive { }` / `clause { }`, which build the corresponding `Clause` from the lambda's result.

`LogicProgrammingScopeWithOperators` supplies infix builders mirroring Prolog operators, e.g. `a equalsTo b` (`=`),
`a greaterThan b` (`>`), `a and b` (`,`), `a or b` (`;`), `a then b` (`->`), `x `is` y`, and `head `if` body`
(builds a `Rule`, i.e. `head :- body`).

## Unification (`:dsl-unify`)

`LogicProgrammingScopeWithUnification` adds the infix/functional unification operators documented in
[Unification API](unification-api.md) directly to the scope: `term1 matches term2`, `match(term1, term2)`,
`term1 unifyWith term2`, `unify(term1, term2)`, `term1 mguWith term2`, `mgu(term1, term2)` — all backed by a
configurable `Unificator` (`LogicProgrammingScope.defaultUnificator`).

```kotlin
--8<-- "dsl-unify/src/commonTest/kotlin/it/unibo/tuprolog/dsl/unify/TestLogicProgrammingScopeWithUnification.kt:61:77"
```

## Theories (`:dsl-theory`)

`LogicProgrammingScopeWithTheories` layers a `TheoryFactory` (`IndexedTheoryFactory` by default) onto the scope, so
`rule { }`/`fact { }` results can be assembled directly into a `Theory` — used by `:dsl-solve` to populate a
solver's static knowledge base without touching `Theory`'s own API (see [Solver API](solver-api.md)).

## Resolution (`:dsl-solve`)

`LogicProgrammingScopeWithResolution` wraps a `Solver` (a `defaultSolver`, built from the `SolverFactory` passed to
`logicProgramming(solverFactory) { }`/`prolog { }`), exposing `staticKb(vararg clauses)` /
`staticKb(theory)` to load facts/rules, and `solve(goal, options = ...)` /`solveOnce`/`solveList` mirroring
[`Solver`](solver-api.md#solver)'s own methods. `prolog { }` specifically defaults to `Solver.prolog` (the classic
SLD solver).

Putting it together — the full DSL surface, in one query:

```kotlin
--8<-- "dsl-solve/src/commonTest/kotlin/it/unibo/tuprolog/dsl/solve/TestPrologWithResolution.kt:9:38"
```

See [Kotlin DSL background](../explanation/kotlin-dsl-background.md) for the rationale behind layering the scope
interfaces this way.
