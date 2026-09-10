# Getting started with 2P-Kt in Kotlin

This tutorial takes you from an empty Kotlin project to a working Prolog knowledge base: you'll declare a small
family tree, ask who Abraham is an ancestor of, and understand the answers 2P-Kt gives back. It uses 2P-Kt's
Kotlin DSL throughout, since that's the idiomatic way to drive 2P-Kt from Kotlin.

## 1. Add the dependency

You need two modules: `dsl-solve` (the DSL, which pulls in `dsl-theory`, `dsl-unify`, `dsl-core` and the
solver-agnostic `solve` API transitively) and `solve-classic` (the actual SLD-resolution engine that the DSL
resolves to at runtime — `dsl-solve` deliberately doesn't depend on any concrete solver, so you have to add one
yourself):

```kotlin
// build.gradle.kts
dependencies {
    implementation("it.unibo.tuprolog", "dsl-solve", "2P_VERSION")
    implementation("it.unibo.tuprolog", "solve-classic", "2P_VERSION")
}
```

See [Add 2P-Kt as a dependency](../how-to/add-2pkt-as-a-dependency.md) for repositories, Maven coordinates and
JVM-only setups.

## 2. Open a Prolog scope

Everything in the DSL happens inside a `prolog { ... }` block: it hands you a receiver (a `LogicProgrammingScope`)
that knows how to turn plain Kotlin values into `Term`s, assemble clauses, and drive a `Solver.prolog` instance.

```kotlin
import it.unibo.tuprolog.dsl.solve.prolog

fun main() {
    prolog {
        // knowledge base and queries go here
    }
}
```

## 3. Build a knowledge base

Inside the scope, any `String` becomes a `Struct` when you "call" it with arguments, and `fact { }` / `rule { }`
turn an expression into a `Clause`. `staticKb(...)` loads a list of clauses into the solver's static theory:

```kotlin
prolog {
    staticKb(
        fact { "parent"("abraham", "isaac") },
        fact { "parent"("isaac", "jacob") },
        fact { "parent"("jacob", "joseph") },
    )
}
```

Rules read almost like Prolog itself: `` `if` `` builds a `Rule` (`head :- body`), and `and` builds a conjunction
(`,`). Add two rules for `ancestor/2` — one base case, one recursive case — right next to the facts:

```kotlin
prolog {
    staticKb(
        fact { "parent"("abraham", "isaac") },
        fact { "parent"("isaac", "jacob") },
        fact { "parent"("jacob", "joseph") },
        rule { "ancestor"("X", "Y") `if` "parent"("X", "Y") },
        rule {
            "ancestor"("X", "Y") `if` (
                "parent"("X", "Z") and "ancestor"("Z", "Y")
            )
        },
    )
}
```

`"X"`, `"Y"`, `"Z"` are plain Kotlin strings here, but because they start with an uppercase letter the DSL's
`Termificator` turns them into Prolog `Var`s wherever a `Term` is expected — lowercase strings like `"abraham"`
become `Atom`s instead.

## 4. Run a query

`solve(goal)` drives resolution and returns a lazy `Sequence<Solution>`, one element per solution found:

```kotlin
prolog {
    // ...staticKb as above...

    for (solution in solve("ancestor"("abraham", "X"))) {
        if (solution is Solution.Yes) {
            println(solution.substitution["X"])
        }
    }
}
```

(`import it.unibo.tuprolog.solve.Solution` for the `is Solution.Yes` check.) Running this prints `isaac`, `jacob`
and `joseph`, in that order — one line per way `ancestor(abraham, X)` can be proven true.

## 5. Read the solutions

Every `Solution` is one of three sealed subtypes:

- `Solution.Yes` — the goal succeeded; `substitution` holds the variable bindings, `solvedQuery` the instantiated
  goal;
- `Solution.No` — the goal failed; no bindings;
- `Solution.Halt` — resolution was aborted by an exception (`halt/1`, an uncaught error, a timeout...).

Inside the scope, `substitution["X"]` is DSL sugar for looking a variable up by name without constructing a `Var`
yourself. Outside a DSL scope you'd write `solution.substitution.getByName("X")` instead — see
[Solver API](../reference/solver-api.md#solution) for the full `Solution`/`Substitution` surface, and
[Reuse variables with Scope](../how-to/reuse-variables-with-scope.md) for why 2P-Kt sometimes renames your
variables (e.g. `X` into `X_2`) when you build terms outside of one shared scope like this one.

## Putting it all together

The exact program you just built (matching output included) lives in 2P-Kt's own test suite:

```kotlin
--8<-- "dsl-solve/src/commonTest/kotlin/it/unibo/tuprolog/dsl/solve/TestPrologWithResolution.kt:10:23"
```

```kotlin
--8<-- "dsl-solve/src/commonTest/kotlin/it/unibo/tuprolog/dsl/solve/TestPrologWithResolution.kt:27:31"
```

## Going further

- The DSL is sugar over a lower-level, constructor-based API (`Struct.of(...)`, `Var.of(...)`, `Theory.of(...)`,
  `Solver.prolog.solverWithDefaultBuiltins(...)`) that works the same way without any of the `dsl-*` modules — see
  [Solver API](../reference/solver-api.md) and [Term hierarchy](../reference/term-hierarchy.md).
- If you'd rather keep your knowledge base as plain Prolog source (e.g. in a `.pl` file) instead of Kotlin code,
  parse it with `ClausesParser`/`ClausesReader` — see [Parsing architecture](../explanation/parsing-architecture.md).
- [Prolog DSL](../reference/prolog-dsl.md) documents the full DSL surface (unification operators, arithmetic,
  `theoryOf`, and more); [Kotlin DSL background](../explanation/kotlin-dsl-background.md) explains why it's
  layered the way it is.
- [How-to guides](../how-to/index.md) cover task-oriented recipes (custom unificators, IntelliJ setup, ...) once
  you're past the basics.
