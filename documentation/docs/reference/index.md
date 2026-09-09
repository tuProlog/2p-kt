# Reference

Information-oriented technical description of 2P-Kt's modules and APIs.

The full generated API documentation (Dokka, covering every module) is available at
[`/api/`](../../api/index.html).

## At a glance: the `Term` hierarchy

Every piece of logic data in 2P-Kt — atoms, numbers, variables, structures, clauses — is a [`Term`][term-src].
`Term`s are immutable, tree-like data structures:

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Term.kt:1:16"
```

![Term interface](../assets/diagrams/term.svg)

[term-src]: https://github.com/tuProlog/2p-kt/blob/master/core/src/commonMain/kotlin/it/unibo/tuprolog/core/Term.kt

See [Module map](module-map.md) for how the ~30 modules relate to each other.

## Suggested reading order

Reference pages are self-contained lookup material, but they build on each other roughly in this order — from
the big picture down to specific APIs, ending with a couple of project-meta pages:

1. [Module map](module-map.md) — what the ~30 modules are and how they depend on each other.
2. [Term hierarchy](term-hierarchy.md) — the core logic-data model everything else is built on.
3. [Unification API](unification-api.md) — unifying and substituting `Term`s.
4. [Solver API](solver-api.md) — the platform-agnostic goal-resolution API.
5. [Primitives and functions](primitives-and-functions.md) — the building blocks a `Solver` executes.
6. [Default predicates](default-predicates.md) — the standard predicates built from those primitives.
7. [Libraries](libraries.md) — bundling predicates/functions into a pluggable unit.
8. [I/O library](io-lib.md) — the `:io-lib` predicate catalogue and platform caveats, a concrete `Library` example.
9. [Prolog DSL](prolog-dsl.md) — the Kotlin DSL for building terms/theories/queries.
10. [Errors and exceptions](errors-and-exceptions.md) — the exception hierarchy raised during resolution.
11. [CI/CD pipeline](ci-pipeline.md) — project-meta: how 2P-Kt itself is built, tested, and released.
