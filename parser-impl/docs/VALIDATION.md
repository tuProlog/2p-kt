# Validation performed in the implementation environment

Date: 2026-08-23

## Compilation

The complete `commonMain` source set was compiled with Kotlin/JVM 1.9.0 using:

- strict explicit API checking
- warnings treated as errors
- no dependency except the Kotlin standard library

No JVM-specific imports occur in `commonMain`.

## Tests

All `commonTest` sources were compiled and executed on the JVM. Result:

```text
70 tests passed
0 tests failed
```

The tests include deterministic generated-input checks in addition to example-based unit tests.

## Scaling smoke check

A generated left-associative expression containing:

```text
20,001 operands
20,000 operators
40,002 significant tokens including EOF
```

was lexed and parsed successfully. On this container, the observed single-run times were approximately 72 ms for lexing and 149 ms for parsing. These figures are only a smoke check, not a portable benchmark.

## Environment limitation

The container did not contain Gradle or a Kotlin/Native distribution, and its standalone Kotlin/JS installation lacked the standard-library KLIBs required for direct IR compilation. Therefore JVM compilation and execution were verified directly; JS and Native target compilation should be run through the included Gradle Multiplatform build in the destination repository or CI environment.
