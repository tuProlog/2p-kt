# Validation

Streaming revision date: 2026-08-31

## Tests added by the streaming revision

- construction of a lazy source performs no reads
- requesting token `i` reads only enough input to determine token `i`
- lexical errors are deferred until the malformed token is requested
- eager and chunked token streams are identical for chunk sizes 1 through 12
- comments, numbers, escapes, full stops, and CRLF work across chunk boundaries
- committed input is released while returned clause trees remain usable
- oversized uncommitted clauses produce a typed buffer-limit failure
- source read failures retain their cause
- suspending chunk sessions parse incrementally and report EOF correctly
- JVM `Reader` lexing and parsing are lazy
- JVM reader ownership remains with the caller
- JVM parser convenience overloads accept readers.

The pre-existing common tests remain present, with lexical-error assertions updated to force the
lazy source explicitly.

## Checks performed in this workspace

- compared the final tree against the supplied archive to identify every changed or added file
- checked all Kotlin files for balanced delimiters
- checked internal imports and stale references to the removed eager `LexedSource` representation
- checked that the delivery archive contains only changed or added files.

## Build execution limitation

The supplied archive has no Gradle wrapper and its build script references an external version
catalog/convention plugin that is not included. This environment also has no Gradle or Kotlin
compiler installation. Consequently, the streaming revision could not be compiled or executed in
this standalone workspace.

In the destination repository, the required final verification is:

```text
./gradlew check
```

That build must include the repository's configured common, JVM, and JS targets. In particular, it
must compile `commonTest`, run `jvmTest`, and compile/run the configured JS test target.

## Previous baseline

Before this streaming revision, the complete `commonMain` source set was compiled with Kotlin/JVM
1.9.0 using:

- strict explicit API checking
- warnings treated as errors
- no dependency except the Kotlin standard library

No JVM-specific imports occur in `commonMain`.

The previous eager implementation's test result was:

All `commonTest` sources were compiled and executed on the JVM. Result:

```text
70 tests passed
0 tests failed
```

The tests include deterministic generated-input checks in addition to example-based unit tests.

The previous eager implementation's scaling smoke check used:

A generated left-associative expression containing:

```text
20,001 operands
20,000 operators
40,002 significant tokens including EOF
```

was lexed and parsed successfully. On this container, the observed single-run times were approximately 72 ms for lexing and 149 ms for parsing. These figures are only a smoke check, not a portable benchmark.
