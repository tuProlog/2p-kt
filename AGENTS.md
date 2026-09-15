# 2p-kt — agent notes

General, repo-wide notes. Module-specific notes live in nested `AGENTS.md` files (currently under `ui/`).

## Gradle tasks: known task names, don't rediscover them

This is a large multi-module Kotlin Multiplatform project (`./gradlew projects` lists every module) with the
`io.github.gciatto.kt-mpp.linter`/`documentation`/`bug-finder` plugins applied to essentially every module
(see `build.gradle.kts`'s `multiProjectHelper` block). Running `./gradlew tasks --all` to see what's
available wastes a lot of tokens on a project this size — the task names below (from `.github/workflows/`,
the authoritative CI source) are stable and work both at the root (applies repo-wide) and scoped to one
module with `:<module>:<task>` (e.g. `:core:ktlintCheck`, `:ide-swing:test`):

- **Style**: `ktlintFormat` (auto-fixes), `ktlintCheck` (verifies, no fixes), `detekt` (aggregates that
  module's `detektMain`/`detektTest`/... — see below for the JDK caveat). CI runs `ktlintFormat` first, then
  `ktlintCheck detekt --parallel --continue`, and commits any formatting fix — mirror that order locally so
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

## detekt

A single config (`.detekt.yml`) applies to every module.

- **JDK compatibility**: detekt's embedded Kotlin compiler can crash `detektMain`/`detektTest` with a bare
  version-number error (e.g. `26.0.2.1`) and no other detail, on JDKs newer than it supports (observed with
  JDK 25 and 26, as of October 2026) — unrelated to code content; reproduces even on unmodified files. If this happens, point Gradle at
  an older JDK toolchain for that invocation (21–23 is known to work):
  ```
  ./gradlew :<module>:detektMain -Dorg.gradle.java.home=<path-to-a-JDK-21-to-23-installation>
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
