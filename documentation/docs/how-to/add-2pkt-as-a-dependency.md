# Add 2P-Kt as a dependency

How to pull one or more 2P-Kt modules into your own Gradle, Maven, or npm project.

2P-Kt is published under the Maven group ID `it.unibo.tuprolog` (JVM/Kotlin modules, via Maven Central and
GitHub Packages) and under the [`@tuprolog` npm organization](https://www.npmjs.com/org/tuprolog) (JS modules).
Every module name below (`core`, `solve-classic`, `parser-impl`, ...) maps directly to a Maven/Gradle artifact
ID. npm is the one exception — see step 3 below.

## 1. Pick the module(s) you need

2P-Kt is split into ~33 incrementally-dependent modules. The full, currently-published set is:

```kotlin
--8<-- "settings.gradle.kts:58:93"
```

Dependencies between modules are resolved transitively: importing `theory` automatically pulls in `unify` and
`core`; importing `solve-classic` pulls in `solve`, `theory`, `unify`, and `core`; and so on. Pick the
highest-level module that covers your use case — e.g. `solve-classic` for a classic-resolution Prolog engine,
`parser-impl` for parsing terms/theories from text.

## 2. Add the 2P-Kt repositories

**Gradle (Kotlin DSL):**

```kotlin
--8<-- "README.md:256:260"
```

**Maven:**

```xml
--8<-- "README.md:291:296"
```

`mavenCentral()` alone is enough for stable releases; add the GitHub Packages repository too if you need a
dev/pre-release build (authentication may be required for GitHub Packages).

npm users don't need this step: the `@tuprolog` packages are public on the default npm registry.

## 3. Declare the dependency

Replace `2P_MODULE` with the module name from step 1, and `2P_VERSION` with the [version you want](https://github.com/tuProlog/2p-kt/releases)
(e.g. the latest release).

**Gradle (Kotlin DSL):**

```kotlin
--8<-- "README.md:246:249"
```

**Maven:**

```xml
--8<-- "README.md:280:284"
```

**npm:** because of how the Kotlin/JS compiler works, splitting the published npm packages per-module the way the
JVM/Maven artifacts are doesn't make sense; use the `@tuprolog/full` package regardless of which module(s) you
actually need:

```json
--8<-- "README.md:320:324"
```

## 4. JVM-only projects: use the `-jvm` suffix

If your Gradle/Maven project only targets the JVM (i.e. it's not a Kotlin Multiplatform project), append `-jvm`
to the artifact ID to avoid pulling in the multiplatform metadata artifact:

**Gradle (Kotlin DSL):**

```kotlin
--8<-- "README.md:269:272"
```

**Maven:**

```xml
<dependency>
    <groupId>it.unibo.tuprolog</groupId>
    <artifactId>2P_MODULE-jvm</artifactId>
    <version>2P_VERSION</version>
</dependency>
```

npm packages are JS-only and don't have a `-jvm` variant.

## Verify

Once the dependency resolves, a quick smoke test with the `core` module is enough to confirm the setup works:

```kotlin
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct

fun main() {
    println(Struct.of("foo", Atom.of("bar")))
}
```

See the [Reference](../reference/index.md) section for the full API surface of each module.
