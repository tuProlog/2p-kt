# Use the I/O library

How to load `:io-lib` into a `Solver`, configure its channels, and drive ISO stream/I/O predicates
programmatically. See [I/O library](../reference/io-lib.md) for the full predicate catalogue and platform
caveats, and [I/O library design](../explanation/io-lib-design.md) for the rationale behind them.

## 1. Add the dependency

`:io-lib` depends on `:solve` and `:parser-theory` (see [Module map](../reference/module-map.md)); add it like any
other 2P-Kt module (see [Add 2P-Kt as a dependency](add-2pkt-as-a-dependency.md)):

```kotlin
implementation("it.unibo.tuprolog:io-lib:2P_VERSION")
```

## 2. Load `IOLib` into a `Solver`

`IOLib` (`it.unibo.tuprolog.solve.libs.io.IOLib`) is a plain `Library` object — load it into a `Runtime` alongside
the standard builtins, either through `SolverFactory.solverWithDefaultBuiltins`:

```kotlin
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.IOLib

val solver = Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(IOLib))
```

or through `SolverBuilder` for more fluent, stepwise construction (see
[Solver API](../reference/solver-api.md#obtaining-a-solver)):

```kotlin
val solver =
    Solver.prolog.newBuilder()
        .runtime(Runtime.of(IOLib))
        .build()
```

Inside the `:dsl-solve` DSL (see [Prolog DSL](../reference/prolog-dsl.md)), a `MutableSolver`'s `loadLibrary` is
reachable directly on the scope:

```kotlin
import it.unibo.tuprolog.dsl.solve.logicProgramming
import it.unibo.tuprolog.solve.libs.io.IOLib

logicProgramming {
    loadLibrary(IOLib)
    // ... queries go here
}
```

## 3. Configure channels

`open/3,4` create *new* named channels at runtime, but a solver's initial channels (standard input/output, or any
pre-opened alias you want a query to find already there) are set up when the solver is built. Use
`SolverBuilder`'s `input`/`output`/`standardInput`/`standardOutput`/`standardError` (see
[Solver API](../reference/solver-api.md#channels)):

```kotlin
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel

val captured = StringBuilder()

val solver =
    Solver.prolog.newBuilder()
        .runtime(Runtime.of(IOLib))
        .standardOutput(OutputChannel.of { captured.append(it) }) // redirect write/1, nl/1, ...
        .input("greeting", InputChannel.of("hello")) // a pre-opened alias, readable via get_char(greeting, C)
        .build()
```

Every channel `:io-lib`'s predicates operate on — whether a solver default or one `open/3,4` created — is a plain
`InputChannel<String>`/`OutputChannel<String>`; nothing in `:io-lib` depends on how a channel was built.

## 4. Open and use a stream

`open/3,4` accepts both proper URLs and bare filesystem/`http(s)` paths as the `SourceSink` argument (via
`Url.Companion.of`). Building the query through the DSL (see [Prolog DSL](../reference/prolog-dsl.md)) reads much
closer to actual Prolog than assembling `Struct`s by hand:

```kotlin
logicProgramming {
    loadLibrary(IOLib)
    val solution =
        solveOnce(
            "open"("theory.pl", "read", "Stream", logicListOf("alias"("src"))) and
                ("read_term"("src", "Term", logicListOf()) and "close"("src")),
        )
}
```

Writing works the same way, in `write`/`append` mode:

```kotlin
logicProgramming {
    loadLibrary(IOLib)
    solveOnce(
        "open"("out.txt", "write", "Stream", logicListOf("alias"("dst"))) and
            ("write"("dst", "hello") and "nl"("dst") and "close"("dst")),
    )
}
```

See [I/O library](../reference/io-lib.md#platform-caveats) before relying on writing to anything other than a
local file, or on any predicate other than `open`/`close`/`read_term`/`write`-family/`nl` running the same way on
every platform — remote reads, in particular, stream lazily on the JVM but are fetched eagerly in full on JS.

## 5. Load a theory from a file or URL

`consult/1` is the shortcut for "open, read every clause, close" — it accepts the same `SourceSink` shapes as
`open/3,4`:

```kotlin
logicProgramming {
    loadLibrary(IOLib)
    solveOnce("consult"("theory.pl"))
    solveOnce("consult"("https://example.com/theory.pl"))
}
```

`set_theory/1` is the same idea for an inline source string instead of a file/URL — useful when the theory text
is already in memory:

```kotlin
logicProgramming {
    loadLibrary(IOLib)
    solveOnce("set_theory"("parent(tom, bob). parent(bob, ann)."))
}
```

Unlike `consult/1` (which *appends* to the current theory), `set_theory/1` *replaces* the solver's static and
dynamic knowledge base, operators and flags outright.
