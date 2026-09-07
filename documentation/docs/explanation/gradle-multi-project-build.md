# Why a ~30-Module Gradle Build

Open `settings.gradle.kts` and you will find over thirty `include(...)` calls, one per Gradle sub-project:

```kotlin
--8<-- "settings.gradle.kts:57:87"
```

That is a lot of modules for one library. This page explains the tradeoff behind that choice, and how the
build avoids each of those thirty-odd modules turning into thirty-odd copies of the same boilerplate.

## The tradeoff: fine granularity vs. one big module

The alternative to this structure is the one most small-to-medium libraries pick: a handful of modules, or even
just one. That has real advantages — fewer `build.gradle.kts` files to maintain, no risk of getting the
dependency graph between modules wrong, faster initial setup. 2P-Kt deliberately doesn't do that, for reasons
that map directly onto the module list itself:

- **Consumers should be able to depend on exactly what they need.** A user who only wants to *represent* and
  *unify* logic terms — no parsing, no resolution, no I/O — can depend on `:core` and `:unify` alone. A user
  embedding a full Prolog engine depends on `:solve-classic` (or `:solve-streams`) and transitively pulls in
  everything underneath. A single-module build forces every consumer to accept the whole dependency footprint
  (and the whole set of transitive third-party dependencies) no matter how little of it they use. This matters
  concretely on non-JVM Kotlin targets, where binary size and unnecessary transitive dependencies are a real
  cost, not just a hypothetical one.
- **Alternative implementations need to be genuine siblings, not `if` branches.** `:solve-classic` and
  `:solve-streams` are two different resolution strategies behind the same `Solver` interface (see
  [Solver design](solver-design.md)); `:parser-core`/`:parser-impl`/`:parser-theory` separate concerns that
  happen to have been implemented with completely different underlying tech at different times (see
  [Parsing architecture](parsing-architecture.md)) without ever breaking their public API. Module boundaries are
  what make "swap the implementation, keep the interface" enforceable at compile time rather than just a
  convention: a `:solve-streams` file cannot accidentally reach into `:solve-classic`-internal state, because
  there is no dependency edge between them to reach through.
- **The dependency graph mirrors the conceptual layering.** `:core` (terms) → `:unify` (unification) →
  `:theory` (clause storage) → `:solve` (generic resolution) → `:solve-classic`/`:solve-streams` (concrete
  strategies) is not an arbitrary split; it is the actual layering of concepts the [Explanation](index.md)
  section as a whole describes. Each `:dsl-*` and `:serialize-*` module mirrors this same layering one level up
  (a DSL/serializer for terms, one for unification, one for theories, one for solving), which is only a
  sane thing to maintain because Gradle enforces that e.g. `:dsl-theory` cannot compile without `:theory`, and
  cannot leak `:theory`-unaware code into `:dsl-core`.

The cost of this choice is real and not hand-waved away: thirty-odd `build.gradle.kts` files is thirty-odd
places for configuration to drift, unless something actively prevents that.

## How the boilerplate is actually kept down

That "something" is a convention-plugin suite, `io.github.gciatto.kt-mpp`, applied from the root
`build.gradle.kts` via a small DSL block:

```kotlin
--8<-- "build.gradle.kts:18:56"
```

Instead of every module's `build.gradle.kts` repeating "this is a Kotlin-multiplatform project, wire up the
linter, wire up documentation generation, wire up the bug-finder, wire up dependency-version checks, publish to
Maven", `multiProjectHelper` defines a handful of **project templates** once (`ktProjectTemplate`,
`jvmProjectTemplate`, `jsProjectTemplate`, `otherProjectTemplate`, each a set of plugin IDs) and applies the
right template to every included project automatically, based on a project's declared kind
(`defaultProjectType = ProjectType.KOTLIN`, with explicit overrides via `jvmProjects(...)` for the JVM-only
`:examples`/`:ide`/`:ide-plp` and `otherProjects(...)` for the non-Kotlin `:documentation` module). Individual
modules' `build.gradle.kts` files stay close to empty — they declare their own inter-module dependencies and
little else — because the shared concerns (linting, docs, versioning, bug-finding, multiplatform target
wiring) are template-level decisions made exactly once, at the root.

This is the actual answer to "why doesn't a 30-module build fall apart under its own configuration weight": the
module count buys the dependency-isolation and alternative-implementation benefits above, and the templated,
one-root-block plugin setup is what keeps the *per-module* maintenance cost close to what a single-module build
would have.
