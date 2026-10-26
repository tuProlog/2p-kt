# 2p-kt — agent notes

General, repo-wide notes. Module-specific notes live in nested `AGENTS.md` files (currently under `ui/`).

## Gradle tasks: known task names, don't rediscover them

This is a large multi-module Kotlin Multiplatform project (`./gradlew projects` lists every module) with the
`io.github.gciatto.kt-mpp.linter`/`documentation`/`bug-finder` plugins applied to essentially every module
(see `build.gradle.kts`'s `multiProjectHelper` block). Running `./gradlew tasks --all` to see what's
available wastes a lot of tokens on a project this size — the task names below (from `.github/workflows/`,
the authoritative CI source) are stable and work both at the root (applies repo-wide) and scoped to one
module with `:<module>:<task>` (e.g. `:core:ktlintCheck`, `:ide-swing:test`):

- **Style**: `ktlintFormat` (auto-fixes), `ktlintCheck` (verifies, no fixes), `detektAll` (every detekt
  task of that module, incl. custom source sets like `ide-swing`'s `detektSwingE2eTest` — plain `detekt` misses
  those, and CI's `check` runs them; see below for the JDK caveat). CI runs `ktlintFormat` first, then
  `ktlintCheck detekt --parallel --continue` (then `check`, which covers `detektAll`), and commits any formatting fix — mirror that order locally so
  you fix formatting before reading style findings that formatting would have silently resolved.
- **Compile without running tests**: `jvmMainClasses`/`jvmTestClasses` (JVM sources), `jsMainClasses`/
  `jsTestClasses` (JS sources) — fast correctness check when you don't need test execution yet.
- **Test**: `jvmTest`, `jsTest`, `verifyFatJars`; the aggregate `check` runs style + tests + everything else
  wired into it. Swing E2E: `:ide-swing:swingE2eTest`, `:ide-plp-swing:swingE2eTest` (needs a real or virtual
  display — `xvfb-run -a` on Linux; see `ui/AGENTS.md` for the macOS Accessibility caveat). Web E2E:
  `:ide-web:browserE2ETest`. Docs: `:documentation:assembleSite`.
- `--parallel` and `--continue` (run everything, collect all failures instead of stopping at the first) are
  used throughout CI and are worth defaulting to locally for the same reason: one invocation instead of a
  fix-rerun-fix loop.

## Keeping Gradle output out of your context

Gradle's default console output (progress headers, ANSI redraws, per-task noise) is expensive to read back
and mostly irrelevant. A few habits that generalize beyond this repo:

- Add `--console=plain` when capturing output non-interactively (a shell tool, a log file) — it skips the
  ANSI progress-bar redraws that otherwise get captured as raw escape-code noise.
- Redirect to a file and grep for the outcome instead of reading the full scrollback, e.g.
  `./gradlew check --console=plain --continue > /tmp/gradle.log 2>&1; grep -B2 -A20 "FAILED\|^e: " /tmp/gradle.log`.
  This matters most for tasks with inherently large output (`jsTest`/`browserE2ETest` webpack/karma logs).
- For `ktlintCheck`/`detekt` findings specifically, don't scroll console output at all — read the generated
  reports, which are smaller and structured: `<module>/build/reports/ktlint/ktlint<SourceSet>SourceSetCheck/
  ktlint<SourceSet>SourceSetCheck.txt` (one line per finding) and `<module>/build/reports/detekt/{main,test}.
  txt` (also `.xml`/`.sarif`/`.html`/`.md` alongside, if a structured or human-rendered form is more useful).
- `-q`/`--quiet` is useful when you only need the pass/fail exit code and nothing else — but it also
  swallows the compiler-error detail you'd want on failure, so prefer `--console=plain` + grep over `-q` when
  a task might actually fail.

## Tests: Kotest conventions

The test suite is migrating from `kotlin.test` to Kotest 6 (issue #1017; plan and progress in
`KOTEST_MIGRATION_PLAN.md` until the migration ends). New tests are Kotest specs. Unmigrated `kotlin.test`
classes keep running next to them in the same tasks.

- **Wiring is automatic.** The root `build.gradle.kts` applies KSP and the Kotest Gradle plugin to every
  multiplatform module with test sources, and adds `kotest-framework-engine` and `kotest-assertions-core`
  (`commonTest`) plus `kotest-runner-junit5` (`jvmTest`). JVM tests run on the JUnit Platform because
  `jvmTestFramework=junit5` in `gradle.properties` (a kt-mpp property, since 6.1.0).
- **Style:** `FunSpec` only. Name classes `<Subject>Spec`, in the same package as the subject.
- **Independent examples are parameterized leaves,** not loops: `withTests(nameFn = { ... }, cases) { case -> }`
  (`io.kotest.datatest`). Keep a loop inside one leaf only when iterating *is* the behaviour (consuming a
  sequence, checking order, several state transitions on one solver).
- **Cases are small typed records with an `id`,** e.g. `IndicatorCase(id, name, arity)` in
  `core/.../testutils/IndicatorUtils.kt`. Don't build parallel `inputs`/`expected` lists correlated by
  position. Don't write one universal `TestCase` type.
- **Case names are self-contained lower-kebab paths** (`of-terms/well-formed/sumlist-2`), unique within the
  spec, and never derived from `toString()` of terms with variables. JVM reports keep only the leaf name and
  drop `context(...)` names, so a leaf's name alone must identify it.
- **Fixtures are built inside the leaf** (or by a `() -> Fixture` factory stored in the case) for anything
  mutable or identity-sensitive: solvers, streams, stores, scopes, UI. Kotest does not create a fresh spec
  instance per test the way JUnit did, so `@BeforeTest`-style spec fields become leaf-local values. Keep
  terms that must share a `Var` instance in the same factory call.
- **Keep the domain assertions** (`AssertionUtils.assertEqualities`, `assertStructurallyEquals`,
  `assertEqualsUsingVariablesSimpleNames`, test-solve's `assertSolutionEquals`, …). Their equality notions
  differ on purpose, so don't collapse them into `shouldBe`. Use Kotest matchers for generic checks. Decide
  explicitly between sequence, multiset and set equality for solutions, and never relax the first two.
- **Shared solver behaviours are Kotest factories** in `:test-solve` (`fun xContract(solverFactory, disabled) =
  contract(solverFactory, disabled) { ... }`, see `Contract.kt`). Inside, `case(id) { query yields { listOf(it.yes()) } }`
  is one query on a fresh solver with default built-ins, compared as an exact solution sequence. Anything else
  is a `scenario(id) { ... }` that builds its own solver and asserts. Each solver's `commonTest` has one
  `<Solver>SolverSpec` that `include`s every contract. Cases that solver doesn't support go in `disabled`, which
  must only name ids the contract registers.
- **Coroutines:** use `test("...").config(coroutineTestScope = true) { ... }` for virtual time. Never call
  `runTest` inside a Kotest leaf: on Kotlin/JS it isn't awaited, so its body (and its failures) run after
  the test has already passed.
- **Java tests** (`*.java`) can't be Kotest specs. They stay JUnit Jupiter tests (`org.junit.jupiter.api.*`).
- **Running one spec or case:** pass a fully qualified name to the *platform* task, e.g.
  `:core:jvmTest --tests 'it.unibo.tuprolog.core.IndicatorSpec'`, or
  `--tests 'pkg.Spec.context -- leaf'` for one nested case. Leading wildcards (`'*Spec'`) match nothing.
  `jsTest` is an alias without `--tests`, so use `jsNodeTest`. On JS, Gradle's filter applies only to Kotest
  specs, and every `kotlin.test` test in the module still runs.

## detekt

A single config (`.detekt.yml`) applies to every module.

- **JDK compatibility**: detekt's embedded Kotlin compiler can crash `detektMain`/`detektTest` with a bare
  version-number error (e.g. `26.0.2.1`) and no other detail, on JDKs newer than it supports (observed with
  JDK 25 and 26, as of October 2026) — unrelated to code content; reproduces even on unmodified files. If this happens, point Gradle at
  an older JDK toolchain for that invocation (21–22 is known to work; 23 makes `:ide-web`'s detekt tasks fail
  with `Invalid value (23) passed to --jvm-target`):
  ```
  ./gradlew detektAll -Dorg.gradle.java.home=<path-to-a-JDK-21-or-22-installation>
  ```
- **`MagicNumber` exemptions** (default config):
  - `const val X = ...` at file/class scope → exempt (only works for primitives/`String`).
  - A plain `val X = ...` at top-level or in a regular class body → **not** exempt.
  - The same `val` inside a `private companion object { ... }` → exempt. When no suitable existing
    class/companion is nearby, wrap it: `private class Foo private constructor() { companion object { val X
    = ... } }` (the `private constructor()` avoids `UtilityClassWithPublicConstructor`).
- **`UnreachableCode` false positive**: a function with **two or more** sequential `val x = expr ?: return` /
  `if (cond) return` guards gets one of the guards *and* the code after it flagged as unreachable, even
  though it's ordinary correct guard-clause style. Fix by computing all nullable values first (`?.let {}`
  chains) then one combined `if (a == null || b == null) return` — but first check whether any guarded
  expression can *throw* (not just return null) when an earlier guard's precondition doesn't hold (e.g.
  `DefaultMutableTreeNode.getLastChild()` throws `NoSuchElementException` on `childCount == 0`, unlike a
  plain nullable getter); keep that kind of guard as its own early `if` before combining the rest.

## Graph/diagram rendering: PlantUML (Smetana), not graphviz-java

For rendering a DOT/graph structure to an image anywhere in this repo, use PlantUML's pure-JVM Smetana
layout engine (`!pragma layout smetana` — already used by `:documentation`'s `generateDiagrams` task), not
`guru.nidi:graphviz-java`. That library's bundled GraalVM-JS engine is incompatible with JDK 22+
(`NoSuchMethodError` on `sun.misc.Unsafe.ensureClassInitialized`); it only works pinned to JDK 21, which
isn't viable for a real app that also needs to run on whatever JDK the user has. See
`ui/ide-plp-swing/AGENTS.md` for the incident this rule came from.
