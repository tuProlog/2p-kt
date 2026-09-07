# Getting started with 2P-Kt in Java

This tutorial takes you from an empty Java project to a working Prolog knowledge base: you'll declare a small
family tree, ask who Abraham is an ancestor of, and understand the answers 2P-Kt gives back. 2P-Kt is written in
Kotlin, so every step below also notes the small number of places where that shows up from Java call sites.

## 1. Add the dependency

You need two modules: `solve-classic` (the SLD-resolution engine, which transitively pulls in `solve`, `theory`,
`unify` and `core`) and `parser-theory` (to read Prolog source text into a `Theory`). Since a plain Java project
is necessarily JVM-only, use the `-jvm`-suffixed artifacts:

```xml
<dependency>
    <groupId>it.unibo.tuprolog</groupId>
    <artifactId>solve-classic-jvm</artifactId>
    <version>2P_VERSION</version>
</dependency>
<dependency>
    <groupId>it.unibo.tuprolog</groupId>
    <artifactId>parser-theory-jvm</artifactId>
    <version>2P_VERSION</version>
</dependency>
```

See [Add 2P-Kt as a dependency](../how-to/add-2pkt-as-a-dependency.md) for repositories and the Gradle/Maven
equivalents.

## 2. Write the knowledge base as Prolog text

The simplest way to get a `Theory` from Java is to write it as ordinary Prolog source and parse it —
`ClausesParser` does that in one call, no term-by-term construction needed:

```java
import it.unibo.tuprolog.theory.Theory;
import it.unibo.tuprolog.theory.parsing.ClausesParser;

String source =
    "parent(abraham, isaac).\n" +
    "parent(isaac, jacob).\n" +
    "parent(jacob, joseph).\n" +
    "ancestor(X, Y) :- parent(X, Y).\n" +
    "ancestor(X, Y) :- parent(X, Z), ancestor(Z, Y).\n";

Theory theory = ClausesParser.withDefaultOperators().parseTheory(source);
```

`ClausesParser.withDefaultOperators()` is a Kotlin companion-object factory; because 2P-Kt annotates every such
factory `@JvmStatic`, it's callable from Java exactly like a static method, no `Companion.` indirection needed.

## 3. Build a solver

Load `theory` as the solver's static knowledge base via the fluent `SolverBuilder`:

```java
import it.unibo.tuprolog.solve.Solver;
import it.unibo.tuprolog.solve.MutableSolver;

MutableSolver solver = Solver.prolog().newBuilder().staticKb(theory).buildMutable();
```

Two Kotlin-Java interop details here:

- `Solver.prolog` is a `val` on Kotlin's `Solver` companion object; because it's `@JvmStatic`, Java calls it as a
  **method** — `Solver.prolog()`, not `Solver.prolog`.
- `SolverFactory`'s other builder methods (`solverOf(...)`, `solverWithDefaultBuiltins(...)`) take many optional
  Kotlin parameters without `@JvmOverloads`, so Java would have to pass every one of them explicitly. The fluent
  `newBuilder()...build()`/`buildMutable()` path sidesteps that and is the idiomatic entry point from Java.

`buildMutable()` gives you a `MutableSolver`, which you don't strictly need here, but it's what lets you
`assertZ`/`retract` clauses later — see [Solver API](../reference/solver-api.md#mutablesolver). Default
predicates (`is/2`, comparisons, list built-ins, ...) are loaded automatically unless you call `.noBuiltins()`.

## 4. Build the query

```java
import it.unibo.tuprolog.core.Atom;
import it.unibo.tuprolog.core.Struct;
import it.unibo.tuprolog.core.Var;

Struct query = Struct.of("ancestor", Atom.of("abraham"), Var.of("X"));
```

## 5. Run it and read the solutions

`solveList(goal)` eagerly collects every solution into a `java.util.List<Solution>`, which is the easiest thing
to consume from Java:

```java
import it.unibo.tuprolog.solve.Solution;

import java.util.List;

List<Solution> solutions = solver.solveList(query);
for (Solution solution : solutions) {
    if (solution.isYes()) {
        System.out.println(solution.getSubstitution().getByName("X"));
    }
}
```

Running this prints `isaac`, `jacob` and `joseph` — one line per way `ancestor(abraham, X)` can be proven true.
`Solution` is a sealed Kotlin type exposed to Java as three subtypes (`Solution.Yes`/`No`/`Halt`); `isYes()` /
`isNo()` / `isHalt()` (compiled from Kotlin `is...` boolean properties) let you tell them apart without an
`instanceof` chain, and `getSubstitution().getByName("X")` looks a binding up by variable name without needing to
reconstruct the exact `Var` you queried with.

If you don't want every solution materialized at once, `solver.solve(goal)` returns a Kotlin `Sequence<Solution>`
instead of a `List` — but `Sequence` isn't a `java.lang.Iterable`, so Java can't use it in a for-each loop
directly; call `.iterator()` and drive it with a `while` loop instead:

```java
--8<-- "examples/src/test/java/it/unibo/tuprolog/examples/ReadEditSolveJvm.java:30:40"
```

(That snippet is from a different, real example in 2P-Kt's own test suite — same pattern, different query.)

## Next steps

- [Solver API](../reference/solver-api.md) documents `Solver`, `SolveOptions` (timeouts, solution limits, eager
  vs. lazy), `MutableSolver` and `Library`/`Runtime` in full.
- [Term hierarchy](../reference/term-hierarchy.md) and [Default predicates](../reference/default-predicates.md)
  cover the rest of the term-construction and standard-library surface.
- [How-to guides](../how-to/index.md) has task-oriented recipes (custom unificators, IntelliJ setup, ...).
