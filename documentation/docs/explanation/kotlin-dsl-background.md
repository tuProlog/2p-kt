# Why the Build Scripts Are Kotlin, Too

This page is about **Gradle's Kotlin DSL** (`build.gradle.kts`, `settings.gradle.kts`) — the language 2P-Kt's
*build* is written in. It is unrelated to the Prolog-term-building DSL discussed in
[the Prolog DSL reference](../reference/prolog-dsl.md) (`:dsl-core`/`:dsl-solve`, for writing terms and queries
as Kotlin code); the two happen to both be "a DSL embedded in Kotlin", but one configures Gradle and the other
constructs logic terms, and they share no code.

`settings.gradle.kts` even says so explicitly, at the very top of the repository:

```kotlin
> __NOTICE:__ Prefer _Kotlin_ as the scripting language for Gradle
```

## The alternative was Groovy, and it costs you at scale

Gradle has always supported two build-script languages: Groovy (the original, dynamically typed) and Kotlin
(added later, statically typed). For a one-module toy project the difference barely shows. For a build with
over thirty interdependent modules (see
[Why a ~30-module Gradle build](gradle-multi-project-build.md)) and a fair amount of genuinely custom build
logic, it shows constantly. A few concrete examples straight out of this repository's own build scripts:

- **Typed task references, checked at compile time.** `core/build.gradle.kts` configures Detekt, Dokka, and
  ktlint tasks by type, not by name-as-a-string:

  ```kotlin
  --8<-- "core/build.gradle.kts:1:5"
  ```

  ```kotlin
  --8<-- "core/build.gradle.kts:62:66"
  ```

  In Groovy's dynamic DSL, `tasks.withType(Detekt)` still works, but there is no compiler checking that
  `Detekt` is spelled right, imported from the right plugin, or that `.configureEach { ... }`'s closure body
  refers to properties `Detekt` actually has — mistakes surface at build time, potentially only in CI, rather
  than as a red squiggle in the IDE while writing the script.
- **Type-safe accessors for the version catalog.** `libs.versions.jvm.get()` and
  `libs.plugins.ktMpp.mavenPublish.get().pluginId` (both used verbatim in this repo's build scripts) are
  generated, statically-typed accessors into `gradle/libs.versions.toml` — autocomplete works, a typo'd
  dependency alias is a compile error in the build script itself, not a runtime "could not find dependency".
- **Real Kotlin where the build genuinely needs logic.** `core/build.gradle.kts` writes a generated `Info.kt`
  file (embedding the project's version into a Kotlin `object`) as part of the build, with a `require(...)`
  check that the module's version matches the root project's:

  ```kotlin
  --8<-- "core/build.gradle.kts:39:48"
  ```

  and `solve-classic/build.gradle.kts` reads Gradle properties with typed defaults to size the JVM test
  process:

  ```kotlin
  --8<-- "solve-classic/build.gradle.kts:9:10"
  ```

  Both are ordinary Kotlin — string templates, `require`, the standard library — reusing exactly the language
  and standard library every contributor to the *production* code already knows, rather than asking them to
  context-switch into Groovy's separate (and looser) semantics just to touch the build.

## Why this matters specifically for a project this shape

2P-Kt is a Kotlin-multiplatform codebase maintained by people who already read and write Kotlin all day; making
the build scripts Kotlin too means IDE navigation ("go to definition" on a plugin ID, a task type, a dependency
alias), refactoring support, and static type-checking all keep working when you jump from application code into
build configuration — which happens often in a build this modular, since so much of the shared configuration
(the `multiProjectHelper` templates described in
[Why a ~30-module Gradle build](gradle-multi-project-build.md)) is itself non-trivial Kotlin code, not simple
declarative property lists. Choosing Groovy for the scripts and Kotlin for everything else would have meant maintaining build logic in a
second language purely for historical reasons, for no benefit specific to this project.
