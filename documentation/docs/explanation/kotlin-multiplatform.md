# Why Kotlin Multiplatform

Plain tuProlog was a Java library: anything built on it had to run on a JVM, and anything that wanted to
talk to it from outside the JVM needed some kind of bridge. That constraint sat uneasily with where the
projects built on tuProlog were actually headed — coordination models for mobile and web agents, logic
solvers meant to run on IoT/edge devices, browser-hosted reasoners — all of which wanted a logic engine that
did not assume a JVM was present. Rewriting from scratch (see [Project History](project-history.md)) was the
opportunity to fix that at the foundation instead of bridging around it.

## Why Kotlin specifically

Kotlin was chosen over, say, keeping the codebase in Java or moving it to a JVM-only alternative, for a
fairly small set of concrete reasons:

- **One source, several platforms.** Kotlin Multiplatform lets the same `commonMain` source set compile to
  genuinely different targets (a JVM library, a JavaScript library) without hand-porting the logic twice.
  For a project whose whole point is a shared, well-tested `core`/`unify`/`theory`/`solve` stack (see
  [Design Philosophy](philosophy.md)), duplicating that stack per platform was never an option worth
  considering — a bug fixed once in `commonMain` is fixed everywhere.
- **Interop in both directions.** A Kotlin/JVM artifact is usable from plain Java with no special ceremony,
  and a Kotlin/JS artifact is usable from plain JavaScript/TypeScript in the same way. That matters directly
  for tuProlog's long-standing goal of bidirectional OOP interoperability (see
  [`:oop-lib`](../reference/module-map.md)): the host language's objects need to be reachable from Prolog
  and vice versa, on whichever platform the host happens to be.
- **Practical, modern tooling**, without requiring the project to abandon the JVM ecosystem (Gradle, Maven
  Central, the existing tuProlog user base) it was already part of.

## What this actually buys, and its cost

The upside is real: a single `:core` module change is validated against every platform target in one build,
and users get to pick the deployment that fits them — a JVM library, an npm package, a self-contained
desktop GUI — from the same codebase, with dependency management (Gradle module graph, see [Module
map](../reference/module-map.md)) doing the work of keeping platform-specific artifacts consistent with each
other.

The cost is that `commonMain` code can only rely on the *Kotlin common* standard library, not on
`java.util.*` or platform-specific browser APIs. Anything that genuinely needs a platform capability has to
be written against a Kotlin `expect`/`actual` declaration, with one implementation per target — which is
exactly why the module graph keeps a hard line between "common" modules and modules that are frankly
platform-specific by necessity.

## The current targets, precisely

It is easy to over-state Kotlin Multiplatform's reach in the abstract, so it is worth being precise about
what 2P-Kt actually builds for today, per the project's own Gradle configuration: **JVM and JavaScript**.
Most modules opt into both automatically; a handful of modules are deliberately JVM-only — `:ide`,
`:ide-plp`, and `:examples` — because they depend on JavaFX for their GUI, which has no JS or other-platform
equivalent. Everything else in the ecosystem, including `:core`, `:unify`, `:theory`, and the `:solve-*`
family, is common code compiled for both targets.

Earlier design notes for this project (circa 2021) described Android and Kotlin/Native support as open
questions, and singled out `:oop-lib` as JVM-only because Kotlin's reflection API — which OOP interop
depends on to inspect host objects at runtime — was not available on other platforms at the time. Neither
claim reflects the current codebase: Android and Native are not targeted, and `:oop-lib` has since gained a
real JavaScript implementation (`TypeUtilsJs.kt`) alongside its JVM one, sitting behind the same common
API. This is a reasonable trajectory for a multiplatform project to take — platform reach tends to expand
where it is actually needed, rather than being front-loaded speculatively.
