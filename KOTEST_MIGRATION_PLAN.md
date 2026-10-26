# Kotest migration plan (issue #1017)

Working document for branch `feature/issue-1017`. It refines the issue's 11-stage strategy using what the
repository actually contains as of 2026-10-26. Delete this file in Step 6.

## 0. Facts this plan is built on

| Item | Value | Source |
|---|---|---|
| Kotlin | 2.4.21 | `gradle/libs.versions.toml` |
| Gradle | 9.8.2 | wrapper |
| kt-mpp | 6.0.7 | catalog (`../kt-mpp` is at the same Kotlin version) |
| Kotest (latest) | 6.2.6, incl. `io.kotest` Gradle plugin and `kotest-framework-engine-js` | Maven Central / plugin portal |
| KSP (latest) | 2.3.12 (KSP2, no longer pinned to a Kotlin version) | Maven Central |
| kt-mpp's own tests | already use Kotest 6.2.6 (JVM only) | `../kt-mpp/gradle/libs.versions.toml` |

**How kt-mpp wires tests today** (`../kt-mpp/src/main/kotlin/io/github/gciatto/kt/mpp/kotlin/`):
- `MultiplatformPlugin.kt`: `commonTest` gets `kotlin-test-common` and `kotlin-test-annotations-common`;
  `jvmTest` gets `kotlin-test-junit` (JUnit **4**).
- `AbstractKotlinProjectPlugin.kt`: `jsTest` gets `kotlin-test-js`; Node tests use Mocha with `mochaTimeout`
  (default `10m`).
- `JvmOnlyPlugin.kt`: `kotlin-test-junit`.
- Nothing is configurable: every module gets kotlin-test, unconditionally.

**Test inventory (`@Test` count, by module):**

| Module | `@Test` | Notes |
|---|---:|---|
| theory | 597 | |
| core | 581 | `testutils/AssertionUtils.kt` (`onCorrespondingItems` used in 35 files, `assertAllVsAll`), `RuleUtils`-style dummy lists |
| solve-prolog | 440 | 55 forwarding classes over `test-solve` prototypes |
| solve-problog | 411 | 51 forwarding classes; **12 files with `@Ignore`** |
| solve-concurrent | 371 | its **own** parallel contract set (`TestConcurrentX` + `TestConcurrentXImpl`, ~50 files) using multiset `WithAssertingEquals`/`FromSequence`; 9 files with `@Ignore` |
| test-solve | 250 | 56 `TestX`/`TestXImpl` + `prototype()` pairs in `commonMain` (published) |
| test-dsl | 192 | `commonMain` test support (published) |
| parser-core / parser-impl / parser-theory | 138 / 103 / 20 | |
| io-lib / unify / gui / oop-lib | 130 / 118 / 64 / 55 | io-lib + oop-lib use 6 prototypes |
| ide-swing / ide-plp-swing | 73 / 14 | incl. `swingE2eTest` source set (AssertJ Swing, JDK 21, `buildSrc/.../SwingE2eTesting.kt`) |
| ide-web | 23 | jsTest (`kotlin("test")` + coroutines-test) + `browserE2ETest` (`Exec node scripts/browser-e2e-test.mjs`) |
| serialize-core, examples, solve, utils, full, dsl-*, datalog, bdd, gui-plp, serialize-theory, solve-plp | ≤ 32 each | `examples` is JVM-only with `kotlin("test-junit")` |
| **Total** | **≈3,800** | |

Other patterns to migrate: `@BeforeTest` ×40, `@AfterTest` ×20, `runTest` ×22, `@Ignore` in 24 files, one
`expect fun` in test code, no `@JsName`.

## 1. Key design decisions (proposed defaults)

1. **Spec style:** `FunSpec` everywhere, as the issue suggests. Parameterized leaves via Kotest 6's
   data-driven API (`withTests`, `nameFn = { it.id }`). Confirm exact names against 6.2.6 in the spike.
2. **Coexistence on JVM with no kt-mpp change:** in 2p-kt, switch every JVM `Test` task to
   `useJUnitPlatform()` and add `junit-vintage-engine` to `jvmTestRuntimeOnly`. Legacy kotlin-test (JUnit 4)
   tests then run under the vintage engine and Kotest specs under the Kotest engine, in the same task and the
   same report. kt-mpp keeps adding `kotlin-test-junit` until Step 6.
3. **Kotest wiring stays in 2p-kt first, upstream later.** A root-level convention (a `buildSrc` function in
   the style of `registerSwingE2eTestSourceSet`, or the root `subprojects {}` block) applies `io.kotest` + KSP
   and adds `kotest-framework-engine` + `kotest-assertions-core` to `commonTest`. Only what kt-mpp
   **blocks** gets upstreamed (see §2).
4. **No `ProjectConfig` at the start.** Kotest's defaults (sequential specs) match today's behaviour. Add
   `io.kotest.provided.ProjectConfig` only if the spike shows a need (e.g. a global timeout).
5. **Fixtures are created inside the leaf.** Kotest does not create a fresh spec instance per test the way
   JUnit does. The isolation-mode names changed in Kotest 6, so check which modes exist in 6.2.6. Any class
   whose mutable fields are reset by `@BeforeTest` must move that state into the test body or a fixture
   factory. Do not lean on an isolation mode to cover this.
6. **Test-support modules (`test-solve`, `test-dsl`) export Kotest intentionally.** Their `commonMain`
   switches from `api(kotlin("test-common"))` to `api(kotest-framework-engine)` + `api(kotest-assertions-core)`.
   They are test-support artifacts, so this is the intended leak. Production modules must not gain Kotest on
   `*Main` classpaths (checked in Step 6). `:test-solve` is internal-only (D1), so its legacy API is removed
   outright in Stage 7 rather than deprecated.
7. **A single PR into `develop` at the end (D2)**, carrying every stage on `feature/issue-1017`. Every stage
   still ends with a green `check`, as one or more commits that can be reviewed on their own.

## 2. Proposed kt-mpp changes (in `../kt-mpp`, tested via `--include-build ../kt-mpp`)

Keep these minimal. kt-mpp is shared by other projects, so defaults must preserve current behaviour.

- **K1 (needed by Step 6, not before):** a `multiPlatformHelper` property, e.g. `testFramework` populated from
  Gradle property `ktTestFramework`, with values `kotlin-test` (default, today's behaviour) and `none`.
  `none` skips `addTestDependencies` in `MultiplatformPlugin`, `AbstractKotlinProjectPlugin` (js) and
  `JvmOnlyPlugin`. With it, 2p-kt can drop kotlin-test/JUnit 4 completely without exclusion hacks.
- **K2 (only if the spike shows Mocha config conflicts with Kotest JS):** make the Mocha `testTask`
  configuration skippable under the same property.
- **Not proposed:** bundling Kotest coordinates/versions into kt-mpp. That would tie kt-mpp releases to
  Kotest releases. Revisit after 2p-kt's migration if other kt-mpp consumers want the same thing.

Workflow: edit `../kt-mpp` on a branch there. Test with
`./gradlew --info --include-build ../kt-mpp <tasks>` from 2p-kt. Open a kt-mpp PR, then bump `ktMpp` in
the catalog once it is released. 2p-kt never merges anything that depends on an unreleased kt-mpp.

## 3. Stages

### Stage 0 — Baseline inventory and characterization (this branch)

1. **Baseline run** on `develop`:
   `./gradlew jvmTest jsTest --parallel --continue --console=plain > baseline.log 2>&1`. Record failures, skips
   and wall-clock time per task (from `--profile` or the log).
2. **Inventory script** (one small Python file, kept under `migration/`): it parses
   `**/build/test-results/**/TEST-*.xml` into `module,platform,class,test,status,time` CSV. The baseline CSV
   is committed. The same script is re-run after each stage. Names change during migration, so parity is
   checked through a hand-maintained mapping `migration/parity.csv` (`old class#method -> new spec/test path`,
   with one row per old data row for list-driven tests), not through raw counts.
3. **Characterization tests for `core/src/commonTest/.../testutils/AssertionUtils.kt`:**
   - `assertEqualities` / `assertNoEqualities` / structural and variable-simple-name variants: one positive
     and one negative example each, so the three equality notions stay distinct.
   - `onCorrespondingItems`: one test that pins down today's `zip`-truncation behaviour.
   - test-solve's `assertSolutionEquals`, `assertSolverSolutionsCorrect`, `assertSameQuery`, and
     solve-concurrent's `WithAssertingEquals` (multiset): ordered vs unordered, duplicate multiplicity,
     halt/error comparison.
4. **Hardening, as its own commit:** `onCorrespondingItems` fails when sizes differ. Run the 35 using
   files. Any test that turns red is a real latent defect, fixed in its own clearly labelled commit
   (`fix(test): ...`), never inside the framework migration.

Exit: baseline CSV and log committed; the helpers are characterized and size-checked; `check` is green.

### Stage 1 — JVM + JS compatibility spike (this branch)

Pilot: `:core` (multiplatform foundation), `:test-solve` + `:solve-prolog` (shared contract), JVM + JS.

1. Add to the catalog: `kotest = "6.2.6"`, `ksp = "2.3.12"`, plugin ids `io.kotest` and
   `com.google.devtools.ksp`, libraries `kotest-framework-engine`, `kotest-assertions-core`,
   `kotest-runner-junit5`, `junit-vintage-engine`.
2. Shared convention as in §1.3, applied to the pilot modules only.
3. Write throwaway specs that check each spike criterion listed in the issue: plain test, nested `context`,
   named parameterized leaves, a factory included from another module (`test-solve` → `solve-prolog`),
   `xtest`/`enabled = false`, a deliberately failing case (build must fail, then revert), Gradle HTML/XML
   reports, `--tests` filtering on JVM, and the closest JS equivalent.
4. **Risks to verify explicitly:**
   - KSP 2.3.12 against Kotlin 2.4.21 for the JS compilation.
   - Legacy kotlin-test tests and Kotest specs running in the **same** `jsTest` (Mocha) task.
   - `runTest` (kotlinx-coroutines-test) inside a Kotest leaf on **JS**. `runTest` returns a promise-backed
     `TestResult` that may not be awaited inside an already-suspending lambda. If it is not, use Kotest's
     coroutine test scope config for those 22 call sites.
   - ktlint/detekt ignore `build/generated/ksp/**`.
   - Gradle's up-to-date checks and build cache still work with the KSP tasks.
   - JVM vintage + Kotest engines together: counts in the reports equal the baseline plus the new specs.
5. Measure the time overhead: `:core:jvmTest :core:jsTest` before and after.

Exit (issue's own criterion): parameterized and shared-contract Kotest tests run correctly on JVM and JS, and
failures propagate. Spike specs are deleted or turned into real tests.

### Stage 2 — Conventions and shared infrastructure (this branch)

1. Roll the convention out to every module with tests (still in coexistence mode).
2. Write the conventions into `AGENTS.md` (repo-wide testing section): FunSpec, case-ID format
   `area/sub-area/case` (lower-kebab, unique per group), small domain case types
   (`data class UnificationCase(val id: String, ..., val fixture: () -> ...)`), fixtures built inside the leaf,
   when a loop stays a loop (sequences, ordering, state transitions), and sequence vs multiset vs set
   assertions.
3. Port the `core` assertion helpers to Kotest-friendly forms (matchers or plain functions that throw
   `AssertionError`). Keep the same names and the same three equality semantics. The Stage 0
   characterization tests guard them.
4. Add the contract-factory skeleton to `test-solve` (see Stage 3) for one behaviour (`TestUnify`) as the
   template.

Exit: one solver contract and one `core` spec use the conventions with no forwarding boilerplate.

### Stage 3 — `test-solve` + `solve-prolog`

Two sub-steps, kept apart as the issue requires:

- **3a — registration only (parity).** For each of the 56 `TestX` interfaces, add a factory to `test-solve`
  `commonMain` that delegates to the existing prototype:
  ```kotlin
  fun unifyContract(factory: SolverFactory, disabled: Set<String> = emptySet()) = funSpec {
      val p = TestUnify.prototype(factory)
      fun case(id: String, body: () -> Unit) = test(id).config(enabled = id !in disabled) { body() }
      case("unify/number-with-number") { p.testNumberUnify() }
      // ...
  }
  ```
  The `disabled` parameter carries today's `@Ignore`s (12 problog files) as an explicit, reviewable list.
  The 56 factories are mechanical, so generate them once with a throwaway script from the interface method
  lists, then hand-edit the names. Then replace the 55 `TestPrologX` forwarding classes with one spec per
  area (or a single `PrologConformanceSpec` that `include`s all factories), and check `parity.csv`.
- **3b — representation.** Behaviour by behaviour, rewrite repetitive prototype bodies as typed
  `SolverCase` tables. Keep ordered/unordered/multiplicity semantics exactly as they are. A loop stays a
  loop when it is part of the scenario.
- **API:** `:test-solve` is internal-only (D1). `TestX`, `TestXImpl` and `prototype()` stay only while 3a
  factories delegate to them, then get deleted in Stage 7. No deprecation cycle.

### Stage 4 — Remaining solver modules

- `solve-problog`: same as 3a, with `disabled = ...` lists.
- `solve-concurrent`: its `TestConcurrentX` set duplicates test-solve with multiset semantics. Keep it a
  separate contract family (`concurrentUnifyContract`, …) in its own `commonTest`, migrated the same
  3a → 3b way. **Do not** merge it with the sequential contracts here: that would change assertion
  semantics. Merging is deferred to a later PR (D4).
- `solve`, `solve-plp`, `io-lib`, `oop-lib` (6 prototype users).

### Stage 5 — Foundations, parsers, DSLs, libraries, apps

Order follows the issue: `utils`, `core`, `unify`, `theory` → `parser-*` → `dsl-*` + `test-dsl` →
`serialize-*`, `bdd`, `datalog`, `io-lib`, `oop-lib` → `gui`, `gui-plp`, `ide-*` ordinary tests,
`examples`, `full`. One PR per group. For each module:

1. Convert dummy-instance lists (e.g. `RuleUtils.mixedRules`, `wellFormedRules`) into case lists with IDs.
   Wherever two lists are correlated by position today, merge them into one typed case.
2. `assertAllVsAll` matrices keep their full N² coverage. When N² leaves are too many to register (measure
   it), use one leaf per row (`N` leaves × `N` assertions), not fewer comparisons.
3. Shared `Var` identity: when the old list builds terms that share a `Var`, the case factory builds them
   together in one lambda.
4. Validation per module, as the issue lists it: `ktlintFormat`, `jvmTestClasses`, `jsTestClasses`,
   `jvmTest`, `jsTest`, `check`, then refresh the inventory CSV and `parity.csv`.

`theory` (597) and `core` (581) are the largest, so plan two PRs each.

### Stage 6 — E2E suites

- **Swing:** move the specs in `src/swingE2eTest` to FunSpec. AssertJ Swing, the JDK 21 launcher, the locale
  pinning and Xvfb all stay. `SwingE2eTesting.kt` sets `testClassesDirs = e2e.output.classesDirs`, which should
  stop Kotest rediscovering unit specs. Still verify it: the runtime classpath includes `test.output`, and
  Kotest might classpath-scan on its own. Check that the e2e report contains only e2e specs. Add
  `useJUnitPlatform()` to that task.
- **Browser (D3):** keep `browser-e2e-test.mjs` and `browserE2ETest` as they are, and stay off `check`.
  Refactor the script so each scenario runs and fails independently, then emits one JUnit XML test case per
  scenario under `ui/ide-web/build/test-results/browserE2ETest/`. A failing scenario fails the task without
  hiding the scenarios after it. CI already runs the task as its own step.

### Stage 7 — Remove scaffolding

1. Release kt-mpp K1. Set `ktTestFramework=none` in `gradle.properties`, drop `junit-vintage-engine`, drop
   `kotlin("test*")` from `test-solve`, `test-dsl`, `ui/*`, `full`, `examples`.
2. Delete the prototype/forwarding infrastructure (`TestX`, `TestXImpl`, `prototype()`),
   `onCorrespondingItems`, and any helpers that have been superseded.
3. Guard against regressions with detekt's `ForbiddenImport` (`kotlin.test.Test`, `kotlin.test.Ignore`,
   `org.junit.*`) in `.detekt.yml`. It is one config entry, applies to every module and source set, and needs no
   new tooling.
4. Dependency check: `./gradlew :<module>:dependencies --configuration jvmRuntimeClasspath` for each
   published production module shows no `io.kotest`.
5. Update the docs: `AGENTS.md` (the four examples the issue asks for: normal, parameterized, solver
   contract, platform-specific), `ui/AGENTS.md` (E2E), and contributor docs. Delete `migration/` and this
   file.

### Stage 8 — Property testing (separate follow-up)

Out of the critical path. Candidates: unification (idempotence, symmetry of `mgu`), substitution application,
serialize round-trips. Use fixed seeds, bounded generators, and explicit regression examples kept beside each
property.

## 4. Decisions (maintainer, 2026-10-26)

- **D1:** `:test-solve` is internal-only. Its legacy prototype API is removed at the end of the refactoring
  (Stage 7), with no deprecation cycle.
- **D2:** a single PR into `develop` at the end, from `feature/issue-1017`.
- **D3:** browser E2E keeps the Node/Chrome script and gains per-scenario JUnit XML reporting. No Kotest layer
  is added for it.
- **D4:** unifying `solve-concurrent`'s contracts with the sequential ones is a later, separate PR.

## 5. Out of scope (from the issue)

Solver state-machine/search changes, Chronometer/timeouts, parser behaviour changes (except fixes for
defects found by tests, each in its own commit), replacing AssertJ Swing or the browser driver, and bulk
property-testing.
