# I/O library design

See [I/O library](../reference/io-lib.md) for the full predicate catalogue and platform caveats, and
[Use the I/O library](../how-to/use-io-lib.md) for a hands-on walkthrough. This page is about *why* `:io-lib` is
built the way it is: what it reuses from `:solve` rather than reinventing, why stream-opening is split per
platform, and why some ISO predicates are registered but deliberately left unimplemented.

## Reusing `Library` and `Channel`, adding only `Url`

`:io-lib` is, structurally, nothing but a [`Library`](../reference/libraries.md) — a `Signature`-indexed map of
`Primitive`s, aliased `"prolog.io"` (see `IOLib`, `it.unibo.tuprolog.solve.libs.io`). It contributes no new
resolution mechanism: every predicate is written against `Channel`/`InputChannel`/`OutputChannel` from `:solve`
(see [Solver design](solver-design.md#channels-solver-io-without-a-fixed-transport)), the same abstraction
`write/1`/`read/1` and friends already use for a solver's standard streams. That reuse is what lets `open/3,4`
register a *new* named channel (backing a file or a remote resource) that every other I/O predicate then talks to
exactly as it would talk to standard input/output — `IOPrimitiveUtils.ensuringArgumentIsChannel` and friends
don't care whether a channel came from `SolverBuilder.standardOutput` or from `open/3`.

The one genuinely new abstraction `:io-lib` introduces is `Url` (`it.unibo.tuprolog.solve.libs.io.Url`): a parsed
source/sink locator, since the ISO `SourceSink` argument of `open/3,4`/`consult/1` needs to name *what* to open
before a `Channel` can exist at all. `Url` is deliberately minimal — `protocol`/`host`/`port`/`path`/`query`, plus
`readAsText`/`readAsByteArray` for the eager, whole-resource-at-once reads that `consult/1` and JS's non-Node
paths need:

```kotlin
--8<-- "io-lib/src/commonMain/kotlin/it/unibo/tuprolog/solve/libs/io/Url.kt:18:38"
```

## Why `Url` is platform-specific

There is no single Kotlin Multiplatform URL-parsing API, and "open this local path for streaming" means entirely
different things on the JVM (`java.io`/`java.nio`), on Node (its own `fs` module), and in a browser (no real file
system at all). Rather than fighting that with a lowest-common-denominator abstraction, `Url` and the handful of
functions that build/use it (`parseUrl`, `fileUrl`, `remoteUrl`, `openInputChannel`, `openOutputChannel`,
`toLocalPath` — all in `UrlUtils.kt`) are Kotlin `expect`/`actual` declarations, each platform free to implement
however makes sense locally:

- **JVM** (`JvmUrl`, `UrlUtilsJvm.kt`): thinly wraps `java.net.URL`. Any protocol `java.net.URL` understands opens
  a real, lazily-streamed `InputStream`/`OutputStream`; `openInputChannel`/`openOutputChannel` never buffer a
  resource fully in memory.
- **JS** (`JsUrl`, `UrlUtilsJs.kt`, `RemoteAndBrowserIO.kt`): there is no `java.net.URL` to wrap, so `JsUrl`
  hand-parses via the WHATWG `URL` binding shared by Node and browsers. Behavior then forks again on whether the
  runtime *is* Node (`isNode`, `it.unibo.tuprolog.Info.PLATFORM`): only Node gets real, streamed local-file access
  (via Okio's `NodeJsFileSystem`, see below); a browser instead reads/writes local ("file") resources through
  `window.localStorage`, and any remote resource on either JS runtime is fetched *eagerly, in full* via the
  `sync-request` npm package, since Okio is not an HTTP client and the `Solver` API is synchronous end-to-end (no
  async I/O story to plug a real streaming HTTP client into). This is a real, user-visible asymmetry: `open/3` on
  a large remote file streams lazily on the JVM but buffers the whole thing in memory on JS — see
  [I/O library](../reference/io-lib.md#platform-caveats) for the consolidated caveat table.

Both platform `Url`s independently guard against a Windows-specific footgun: a native Windows path
(`C:\Users\...`) parses "successfully" under a generic URL parser as a URL with a single-letter scheme (the drive
letter), so both `JvmUrl`'s underlying `java.net.URI` (via `isAbsolute` checks) and `JsUrl`'s constructor
explicitly reject that shape, forcing `Url.Companion.of`'s `file://`-prefix fallback to take over instead.

## Why Okio

Local-file access used to be hand-rolled per platform. This branch moved it onto
[Okio](https://square.github.io/okio/), Square's multiplatform I/O library, for the local (`file://`) case on
both JVM and Node:

```kotlin
--8<-- "io-lib/build.gradle.kts:10:46"
```

Two things this buys, beyond not hand-rolling buffered readers/writers per platform:

- **A single testable seam.** `LocalFileSystem` (`it.unibo.tuprolog.solve.libs.io`) wraps whichever
  `okio.FileSystem` is current behind a swappable `var`, defaulting to `platformFileSystem` (`FileSystem.SYSTEM`
  on the JVM, `NodeJsFileSystem` on JS) but replaceable in tests with Okio's `FakeFileSystem` — an in-memory
  implementation that behaves like a real one without touching disk.
- **Uniform channel wrapping.** `SinkOutputChannel`/`SourceInputChannel` (`it.unibo.tuprolog.solve.libs.io.channel`)
  adapt an Okio `BufferedSink`/`BufferedSource` into `OutputChannel<String>`/`InputChannel<String>` once, shared by
  both platforms, rather than each platform separately gluing a native stream type into the `Channel` contract.

Okio still isn't an HTTP client (hence `sync-request` staying a JS dependency for remote reads) and has no
synchronous file system for browsers (hence the `window.localStorage` fallback there) — it only replaced the
*local-file* half of the picture.

## Errors: platform failures become ISO errors, not `Throwable`s

A `Primitive` must ultimately fail or throw a `LogicError` — never let a raw platform exception escape. `:io-lib`
funnels every platform-level failure through two small wrapper exceptions in
`it.unibo.tuprolog.solve.libs.io.exceptions`:

- `InvalidUrlException` — a string didn't parse into a `Url` (raised by `parseUrl`/`Url.Companion.of`). Carries
  enough context (via `toLogicError(context, signature, culprit, index)`) to become an argument-indexed ISO
  `type_error(url, Culprit)`, since the offending argument is known at the call site (`open/3,4`, `consult/1`).
- `IOException` — a resource that *did* parse as a `Url` still couldn't be read/written (missing file, unreachable
  host, writing to a non-file `Url`). Unlike `InvalidUrlException`, this carries no argument context — 2P-Kt
  doesn't attempt to classify *why* the platform I/O call failed any further — so `toLogicError(context)` always
  produces an uncaught `SystemError`, rather than a more specific `existence_error/2`/`permission_error/3`.

## Deliberately unimplemented predicates

`IOLib` registers `get_byte/1,2`, `put_byte/1,2`, `peek_byte/1,2`, `close/2`, `char_conversion/2` and
`current_char_conversion/2` for ISO conformance (a `stream_property/2` query, or program relying on their mere
*existence*, should not see `existence_error(procedure, ...)`), but every one of them unconditionally raises a
`SystemError` when actually called. This is a deliberate design choice, not an oversight:

- **Only text streams exist here.** `stream_property/2` always reports `type(text)` (see
  `IOPrimitiveUtils.propertiesOf`); there is no binary-stream mode for `get_byte`/`put_byte`/`peek_byte` to operate
  on, so implementing them "for real" would mean adding a whole second stream kind this library otherwise has no
  use for.
- **No character-conversion table exists either**, so `current_char_conversion/2` (which would enumerate it) has
  nothing to report even if `char_conversion/2` were implemented.
- **`close/2`'s only extra feature over `close/1`** is options like `force(true)`, which don't change behavior
  when there's nothing platform-specific to force.

Raising loudly (rather than silently no-op'ing or approximating) is the point: `TestUnsupportedIOPrimitives`
exists specifically to lock this behavior in, so a future partial implementation doesn't start silently returning
wrong results in place of a clear, obvious-at-a-glance gap.
