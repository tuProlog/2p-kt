# Explanation

Understanding-oriented discussion of 2P-Kt's design and the rationale behind it: why things are the way they
are, not just how to use them.

## Suggested reading order

Roughly: why the project exists, then the core data model, then increasingly specific engine internals, ending
with a few build-tooling rationale pages that are useful mostly to contributors:

1. [Project history](project-history.md) — from tuProlog's Java origins to the 2P-Kt rewrite.
2. [Philosophy](philosophy.md) — the design philosophy behind 2P-Kt's modular architecture.
3. [Term model](term-model.md) — why terms are immutable and what that buys you.
4. [Variables and scoping](variables-and-scoping.md) — why `Var` equality/scoping works the way it does.
5. [Clause databases and RETE](clause-db-and-rete.md) — how theories index clauses for fast matching.
6. [Solver design](solver-design.md) — the rationale behind the `Solver`/`Solution` API's shape.
7. [The solve-classic state machine](state-machine.md) — how the classic SLD-resolution engine actually runs.
8. [Parser architecture](parsing-architecture.md) — how Prolog source text becomes terms and theories.
9. [I/O library design](io-lib-design.md) — why `:io-lib`'s `Url`/channel split looks the way it does.
10. [Kotlin Multiplatform](kotlin-multiplatform.md) — why 2P-Kt targets multiple platforms from one codebase.
11. [Gradle multi-project build](gradle-multi-project-build.md) — why ~30 modules instead of one.
12. [Kotlin DSL background](kotlin-dsl-background.md) — why the build itself is written in Kotlin DSL.
