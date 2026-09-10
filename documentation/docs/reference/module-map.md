# Module map

2P-Kt is split into 30 Gradle/Kotlin-Multiplatform modules (per `settings.gradle.kts`), each publishing its own
Maven/npm artifact. See [Add 2P-Kt as a dependency](../how-to/add-2pkt-as-a-dependency.md) for how to import one.
This page documents what each module is for and how they depend on one another.

## Modules

| Module | Purpose | Depends on (main) |
|---|---|---|
| `utils` | Dependency-free general-purpose utilities shared by every other module: cursors, caches (LRU), graphs, tagging, type-inspection helpers. | — |
| `core` | The `Term` type hierarchy (atoms, numbers, variables, structures, clauses...), `Scope`, term visitors/comparators/formatters — 2P-Kt's logic-data model. See [Term hierarchy](term-hierarchy.md). | `utils` |
| `unify` | Unification algorithms and the `Unificator`/`Substitution` API over `Term`s. | `core` |
| `theory` | In-memory clause databases (`Theory`): listed and indexed implementations, the latter backed by a RETE-like discrimination network. | `core`, `unify` |
| `datalog` | Static well-formedness checks restricting a `Theory` to the (function-free, safely-negated) Datalog subset. | `theory` |
| `bdd` | A standalone Binary Decision Diagram implementation, used by the probabilistic-logic modules. | — |
| `dsl-core` | Core building blocks of the Kotlin DSL for logic programming: `LogicProgrammingScope`, `Termificator` (term "reification" from Kotlin values). | `core` |
| `dsl-unify` | Adds unification-related sugar (`LogicProgrammingScopeWithUnification`) to the DSL. | `dsl-core`, `unify` |
| `dsl-theory` | Adds theory/clause-database-building sugar (`LogicProgrammingScopeWithTheories`) to the DSL. | `dsl-unify`, `theory` |
| `dsl-solve` | Adds resolution-driving sugar (`LogicProgrammingScopeWithResolution`) to the DSL, i.e. querying a `Solver` from the DSL. | `dsl-theory`, `solve` |
| `solve` | The platform-agnostic goal-resolution API: `Solver`, `Solution`, `SolveOptions`, `Library`/`Runtime`, I/O `Channel`s. Defines no resolution algorithm itself. See [Solver API](solver-api.md). | `core`, `unify`, `theory` |
| `solve-classic` | A classic, SLD-resolution-based `Solver` implementation (`Solver.prolog`). | `solve`, `dsl-theory` |
| `solve-streams` | An experimental, `Sequence`-based `Solver` implementation (`Solver.streams`); not production-ready. | `solve`, `dsl-theory` |
| `solve-concurrent` | A coroutine-based, parallelizing `Solver` implementation (`Solver.concurrent`). | `solve`, `dsl-theory` |
| `test-solve` | Reusable, solver-agnostic ISO-Prolog test suites, shared as test-only dependencies by every `solve` implementation. | `solve`, `dsl-theory` *(test-only, from `commonTest`)* |
| `test-dsl` | Reusable test suites for the `dsl-core` DSL. | `dsl-core` *(test-only, from `commonTest`)* |
| `parser-core` | Public entry point wiring the low-level lexer/parser to `core` terms (single-term/single-clause parsing). | `core`, `parser-impl` |
| `parser-impl` | The actual Prolog lexer and Pratt-parser implementation: lazy, streaming, ANTLR-free, with runtime-configurable operators. See its [module README](https://github.com/tuProlog/2p-kt/blob/master/parser-impl/README.md). | — |
| `parser-theory` | Parsing of whole theories/clause databases, with operator-table (`op/3`) support. | `core`, `unify`, `theory`, `parser-core`, `parser-impl` |
| `solve-plp` | Probabilistic-logic-programming extensions bridging `solve` and `bdd`. | `solve`, `bdd` |
| `solve-problog` | A ProbLog-style probabilistic solver: annotated disjunctions, evidence, explanations, built on top of `solve-classic`/`solve-plp`/`bdd`. | `bdd`, `solve-classic`, `solve-plp` |
| `serialize-core` | A generic term (de)serialization framework, e.g. to/from JSON/YAML/XML (via Jackson on the JVM). | `core` |
| `serialize-theory` | Serialization of whole `Theory` instances, built on `serialize-core`. | `theory`, `serialize-core` |
| `repl` | A `clikt`-based command-line REPL/CLI for 2P-Kt. | `core`, `oop-lib`, `io-lib`, `solve-classic`, `parser-theory` |
| `oop-lib` | A `Library` bridging Prolog terms and JVM/Kotlin objects (reflection-based object/term conversion, method/constructor invocation). | `solve` |
| `io-lib` | A `Library` implementing the ISO Prolog I/O predicates (streams, `open/3`, `read_term/2`, `write*`, ...). | `solve`, `parser-theory` |
| `ide-plp` | The probabilistic-logic (ProbLog) extension of the `ide` desktop application, including Graphviz-based rendering of explanations. | `ide`, `solve-problog` |
| `ide` | A desktop IDE/GUI for editing and running 2P-Kt theories. | `io-lib`, `oop-lib`, `parser-theory`, `solve-classic` |
| `examples` | Runnable sample programs demonstrating the other modules. | `solve-classic`, `solve-concurrent`, `solve-problog`, `dsl-theory`, `parser-theory`, `io-lib`, `oop-lib` |
| `full` | An umbrella/aggregator module: depends on every other module (except the test-support and `examples` ones), for a single "batteries-included" artifact. | *(all of the above)* |

## Dependency graph

![2P-Kt module map](../assets/diagrams/project-map.svg)

Arrows point from a module to the module(s) it depends on. `test-solve` and `test-dsl` are only ever pulled in as
`commonTest` dependencies (reusable test suites), so they never appear in another module's published runtime
dependencies, even though they *are* declared in its `build.gradle.kts`.

Dependencies are resolved transitively by Gradle/npm: importing `theory` automatically pulls in `unify` and `core`;
importing `solve-classic` pulls in `solve`, `dsl-theory`, `theory`, `unify`, and `core`; and so on.
