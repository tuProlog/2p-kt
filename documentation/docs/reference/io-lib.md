# I/O library (`:io-lib`)

`:io-lib` is the [`Library`](libraries.md) contributing ISO Prolog's stream/I/O predicates, aliased `"prolog.io"`:

```kotlin
--8<-- "io-lib/src/commonMain/kotlin/it/unibo/tuprolog/solve/libs/io/IOLib.kt:74:77"
```

Load it into a `Runtime` alongside other libraries, e.g. `Runtime.of(IOLib)` — see
[Use the I/O library](../how-to/use-io-lib.md) for the full wiring walkthrough, and
[I/O library design](../explanation/io-lib-design.md) for the rationale behind the choices summarized here.

## Supported predicates

Every predicate below is implemented against `Channel`/`InputChannel`/`OutputChannel` (see
[Solver API](solver-api.md#channels)), so the same implementation works uniformly whether the channel backs a
local file, a fetched remote resource, or an in-memory string.

| Predicate | Class | Purpose |
|---|---|---|
| `open/3`, `open/4` | `Open3`, `Open4` | Open a `SourceSink` (a `Url`, see below) for `read`/`write`/`append`, register it under an alias, unify `Stream` with its `$stream(...)` term. |
| `close/1` | `Close1` | Close a stream and drop its aliases. |
| `current_input/1`, `current_output/1` | `CurrentInput`, `CurrentOutput` | Query (or check) the current input/output stream. |
| `set_input/1`, `set_output/1` | `SetInput`, `SetOutput` | Change which stream is "current" for the unary I/O predicates below. |
| `stream_property/2` | `StreamProperty` | Enumerate/query `input`/`output`/`alias(_)`/`type(text)` for open streams. |
| `at_end_of_stream/0`, `at_end_of_stream/1` | `AtEndOfStream0`, `AtEndOfStream1` | Test whether a stream is closed or exhausted. |
| `flush_output/1` | `FlushOutput` | Force buffered writes to reach their destination. |
| `get_char/1,2`, `get_code/1,2` | `GetChar1/2`, `GetCode1/2` | Read (consuming) the next character/code; `end_of_file`/`-1` at end of stream. |
| `peek_char/1,2`, `peek_code/1,2` | `PeekChar1/2`, `PeekCode1/2` | Same, without consuming — a subsequent read sees the same character again. |
| `put_char/1,2`, `put_code/1,2` | `PutChar1/2`, `PutCode1/2` | Write a character/code. |
| `nl/1` | `Nl1` | Write a newline. |
| `read/1,2` | `Read1`, `Read2` | Parse the next term from a stream. *Deviates from ISO*: fails (rather than unifying `end_of_file`) once the stream has no more terms. |
| `read_term/2,3` | `ReadTerm2`, `ReadTerm3` | Like `read/1,2`, plus an options list requesting `variables(_)`/`variable_names(_)`/`singletons(_)` metadata. |
| `write/2` | `Write2` | Write a term, unquoted, with operator notation (`TermFormatter.default(...)`). |
| `writeq/1,2` | `WriteEq1`, `WriteEq2` | Write a term, quoting atoms/functors where needed to stay re-readable (`TermFormatter.readable(...)`). |
| `write_canonical/1,2` | `WriteCanonical1/2` | Write a term in canonical form: quoted, ignoring operators (`TermFormatter.canonical()`). |
| `write_term/2,3` | `WriteTerm2`, `WriteTerm3` | Write a term per explicit `quoted(_)`/`ignore_ops(_)`/`numbervars(_)` options. |

Plus two non-ISO, tuProlog-specific predicates for loading a theory:

| Predicate | Class | Purpose |
|---|---|---|
| `consult/1` | `Consult` | Fetch the text at a `Url`/bare path and load it as a theory (appending to the current one). |
| `set_theory/1` | `SetTheory` | Parse an inline atom as Prolog source and *replace* the solver's static/dynamic KB, operators and flags with it. |

## Registered but unsupported

These predicates exist (so `existence_error(procedure, _)` isn't raised merely for referencing them) but
unconditionally raise a `SystemError` when actually called — see
[I/O library design](../explanation/io-lib-design.md#deliberately-unimplemented-predicates) for why, and
`TestUnsupportedIOPrimitives` for the test locking this in:

| Predicate | Reason |
|---|---|
| `get_byte/1,2`, `put_byte/1,2`, `peek_byte/1,2` | Only text streams exist (`stream_property/2` always reports `type(text)`); there is no binary-stream mode to operate on. Use the `_char`/`_code` equivalents. |
| `close/2` | Its only addition over `close/1` is an options list (e.g. `force(true)`) with nothing platform-specific to act on. Use `close/1`. |
| `char_conversion/2` | No character-conversion table is implemented. |
| `current_char_conversion/2` | Nothing to enumerate, since `char_conversion/2` is unsupported. |

## `Url`: the `SourceSink` argument

```kotlin
--8<-- "io-lib/src/commonMain/kotlin/it/unibo/tuprolog/solve/libs/io/Url.kt:18:38"
```

`open/3,4` and `consult/1` accept both proper URLs and bare filesystem paths (`Url.Companion.of` retries with a
`file://` prefix if the string doesn't parse as-is). Build one directly via `Url.file(path)`, `Url.remote(...)`,
`Url.http(...)`/`Url.https(...)`, or `Url.of(string)`.

## `IOMode`: the `open/3,4` mode argument

```kotlin
--8<-- "io-lib/src/commonMain/kotlin/it/unibo/tuprolog/solve/libs/io/IOMode.kt:14:18"
```

Corresponding to the Prolog atoms `read`, `write`, `append`.

## Errors

I/O-specific failures surface as regular ISO errors (see [Errors and exceptions](errors-and-exceptions.md)),
via two internal wrapper exceptions (`it.unibo.tuprolog.solve.libs.io.exceptions`):

- `InvalidUrlException` — a `SourceSink` argument didn't parse into a `Url` → `type_error(url, Culprit)`.
- `IOException` — a `Url` parsed fine but the underlying resource couldn't be read/written (missing file,
  unreachable host, writing attempted on a non-file `Url`) → an uncaught `SystemError` (2P-Kt does not attempt to
  further classify the platform failure into a more specific `existence_error`/`permission_error`).

Argument-validation failures (wrong type, unbound, invalid `stream_property/2`/option shape, ...) are reported
per-predicate above and raised as the ISO error named — see each primitive's KDoc, or the shared helpers in
`IOPrimitiveUtils`, for the exact conditions.

## Platform caveats

`:io-lib` targets the JVM and JS only (no native/wasm source sets). Behavior further forks between the JVM,
Node.js, and a JS runtime running in a browser:

| Capability | JVM | Node.js | Browser |
|---|---|---|---|
| Local (`file://`) read | Streamed lazily, via Okio `FileSystem.SYSTEM`. | Streamed lazily, via Okio `NodeJsFileSystem`. | Eager: read from `window.localStorage` (no real file system available to Okio). |
| Local (`file://`) write/append | Supported. | Supported. | **Never supported** — `open/3,4` in `write`/`append` mode raises a `SystemError`. |
| Remote (`http(s)://`) read | Streamed lazily, via `java.net.URL.openStream()`. | Eager: whole resource fetched via the `sync-request` npm package (Okio isn't an HTTP client; the `Solver` API is synchronous end-to-end). | Same as Node: eager, via `sync-request`. |
| Remote write | **Never supported**, on any platform. | | |
| Native Windows paths (`C:\Users\...`) | Rejected by the underlying `java.net.URI` parse (not absolute as a URI), triggering `Url.Companion.of`'s `file://`-prefix fallback. | Rejected by `JsUrl`'s constructor (a single-letter "scheme" is recognized as a mis-parsed drive letter and treated as invalid), same fallback. | N/A |
| Driveless absolute path (`/path/to/x.pl`) on Windows | Resolved by `java.io.File`/`URI` against the current drive. | Node's own `fileURLToPath` throws for this shape (it requires a UNC host or a genuine drive prefix); `:io-lib` falls back to the URL's plain path component instead. | N/A |

See [I/O library design](../explanation/io-lib-design.md#why-url-is-platform-specific) for the rationale behind
this split, and `JvmUrl`/`UrlUtilsJvm.kt` (JVM) vs. `JsUrl`/`UrlUtilsJs.kt`/`RemoteAndBrowserIO.kt` (JS) for the
implementations themselves.
