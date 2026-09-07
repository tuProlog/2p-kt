# Getting started with 2P-Kt in JavaScript

This tutorial takes you from an empty Node.js project to a working Prolog knowledge base: you'll declare a small
family tree, ask who Abraham is an ancestor of, and understand the answers 2P-Kt gives back.

2P-Kt is written in Kotlin and compiled to JavaScript via Kotlin/JS. Most of its API keeps its Kotlin package
structure in the compiled output rather than being flattened for JS, and member names are kept stable with
`@JsName` (Kotlin doesn't allow overloading in JS, so without it names like `solve` would come out mangled). One
part of the API *is* flattened and exported cleanly for direct JS/TS use: the concrete solver factories
(`ClassicSolverFactory` and friends), each annotated `@JsExport` for exactly this purpose — this tutorial builds
around that entry point rather than the `Solver.prolog` shortcut you'd use from Kotlin or Java (which resolves
the classic engine reflectively, a mechanism aimed at same-platform Kotlin/JVM code rather than plain JS).

## 1. Add the dependency

You need `solve-classic` (the SLD-resolution engine) and `parser-theory` (to read Prolog source text into a
`Theory`):

```json
{
  "dependencies": {
    "@tuprolog/solve-classic": "^2P_VERSION",
    "@tuprolog/parser-theory": "^2P_VERSION"
  }
}
```

See [Add 2P-Kt as a dependency](../how-to/add-2pkt-as-a-dependency.md) for background on the npm packages.

## 2. Write the knowledge base as Prolog text

The simplest way to get a `Theory` is to write it as ordinary Prolog source and parse it with `ClausesParser`:

```js
const { it: coreIt } = require("@tuprolog/parser-theory");
const { ClausesParser } = coreIt.unibo.tuprolog.theory.parsing;

const source = `
parent(abraham, isaac).
parent(isaac, jacob).
parent(jacob, joseph).
ancestor(X, Y) :- parent(X, Y).
ancestor(X, Y) :- parent(X, Z), ancestor(Z, Y).
`;

const theory = ClausesParser.withDefaultOperators().parseTheory(source);
```

Note the nested `it.unibo.tuprolog...` path: since `ClausesParser` isn't one of the explicitly-`@JsExport`ed
types, it surfaces under a namespace object mirroring its original Kotlin package rather than as a flat named
export. If a given 2P-Kt release changes this, your bundler/editor's autocomplete on the required module — or the
package's shipped `.d.ts` file — will show you the current shape.

## 3. Build a solver

`ClassicSolverFactory` is exported directly and cleanly — no nested path needed. It exposes the same
`SolverFactory` builder used across every 2P-Kt host language:

```js
const { ClassicSolverFactory } = require("@tuprolog/solve-classic");

const solver = ClassicSolverFactory.newBuilder()
  .staticKb(theory)
  .buildMutable();
```

Default predicates (`is/2`, comparisons, list built-ins, ...) are loaded automatically unless you call
`.noBuiltins()` on the builder first.

## 4. Build the query

```js
const { Struct, Atom, Var } = coreIt.unibo.tuprolog.core;

const query = Struct.of("ancestor", Atom.of("abraham"), Var.of("X"));
```

(`@tuprolog/parser-theory` depends on `@tuprolog/core` and re-exposes it transitively as `it.unibo.tuprolog.core`
on the same required module — or `require("@tuprolog/core")` directly if you'd rather keep the two separate.)

## 5. Run it and read the solutions

`solveList(goal)` eagerly collects every solution; Kotlin's `List` compiles to a JS array-like object you can
iterate with a plain `for...of`:

```js
const solutions = solver.solveList(query);
for (const solution of solutions) {
  if (solution.isYes) {
    console.log(solution.substitution.getByName("X"));
  }
}
```

Running this logs `isaac`, `jacob` and `joseph` — one line per way `ancestor(abraham, X)` can be proven true.
`solution.isYes` reads as a plain property here (Kotlin/JS compiles a Kotlin `val` to a native JS getter, unlike
the JVM/Java build where the same property becomes a method call, `isYes()`); `Solution.No` and `Solution.Halt`
are the other two cases a solution can be in — see [Solver API](../reference/solver-api.md#solution).

## Next steps

- [Solver API](../reference/solver-api.md) documents `Solver`, `SolveOptions` (timeouts, solution limits, eager
  vs. lazy) and `Library`/`Runtime` in full.
- [Term hierarchy](../reference/term-hierarchy.md) and [Default predicates](../reference/default-predicates.md)
  cover the rest of the term-construction and standard-library surface — every type there follows the same
  nested-namespace convention shown above unless it's one of the explicitly-exported solver factories.
- [How-to guides](../how-to/index.md) has task-oriented recipes once you're past the basics.
