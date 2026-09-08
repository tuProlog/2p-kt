# Parsing Architecture

> This page replaces two stale pages from the old wiki: an empty "Rationale and Architecture/parsing.md" stub,
> and a "Developers Guide/parsing.md" page that opened with `> Outdated! Parser has been re-implemented...` and
> then went on to document `parser-jvm`/`parser-js`, an ANTLR grammar, and per-platform generated
> lexer/parser/visitor classes. None of that reflects the current implementation. This page describes what is
> actually in the repository today.

Parsing Prolog source text — single terms, single clauses, or whole theories — is split across three modules,
each with a distinct responsibility: `parser-impl` (the actual lexer and parser), `parser-core` (turning parsed
syntax into 2P-Kt's `Term`/`Clause` model), and `parser-theory` (turning a stream of parsed clauses into a
`Theory`).

## ANTLR is gone

The historical design (and the stale wiki page) generated a lexer and parser per platform via ANTLR, requiring
a `parser-jvm` module (Java-target ANTLR runtime) and a `parser-js` module (JS-target ANTLR runtime), each
depending on platform-specific ANTLR machinery and stitched together with hand-written prototype-inheritance
tricks on the JS side to fake Kotlin `expect`/`actual` semantics across generated code.

`parser-jvm` and `parser-js` still exist as directories in this repository, but grep confirms there is no
`antlr` dependency anywhere in the current build, and — more tellingly — **neither module is listed in
`settings.gradle.kts`**: they are not part of the Gradle build at all anymore, just leftover source trees. The
parser was rewritten as a hand-written, pure-Kotlin **lexer + Pratt parser**, entirely in `commonMain`, with no
code generation step and no per-platform grammar artifacts. This is a good example of a PDF-era claim ("ANTLR
under the hood, platform-agnostic API on top") that does not hold anymore: the platform-agnostic API stayed, but
the ANTLR implementation underneath it was fully replaced.

## `parser-impl`: lexing and parsing, as ordinary Kotlin

`parser-impl` (`it.unibo.tuprolog.parser`) owns the whole lexical/syntactic pipeline, and — this is the
significant architectural change — almost all of it lives in `commonMain`, not behind `expect`/`actual`:

- **`RegexPrologLexer`**, a `PrologLexer` implementation that tokenizes Prolog source using regular expressions
  (`it.unibo.tuprolog.parser.impl.lexer`), producing a `LexedSource` lazily (`LazyLexedSource`) so that very
  large inputs need not be tokenized eagerly.
- **`PrattPrologParser`**, the `PrologParser` implementation, built as a
  [Pratt parser](https://en.wikipedia.org/wiki/Operator-precedence_parser) (`it.unibo.tuprolog.parser.impl.parser`,
  see `PrologGrammar`/`GrammarDsl`):

  ```kotlin
  --8<-- "parser-impl/src/commonMain/kotlin/it/unibo/tuprolog/parser/impl/parser/PrattPrologParser.kt:17:38"
  ```

  A Pratt parser is a natural fit here for the same reason ANTLR's LL grammar needed the elaborate
  `expression[priority, disabled]`/"outer" rotation logic described in the old, now-removed docs: Prolog operator
  parsing (`xfx`/`xfy`/`yfx`/`fy`/`fx`/`xf`/`yf`, arbitrary user-declared priorities via `op/3`) is exactly the
  problem operator-precedence/Pratt parsing was designed to solve directly, via binding-power-driven recursive
  descent, without needing separate grammar productions per priority level or a post-hoc tree-rotation pass.
  `OperatorTable`/`MutableOperatorTable` (`it.unibo.tuprolog.parser.operators`) model the live, mutable set of
  known operators a parse session consults, which is what makes runtime `op/3` declarations affect subsequent
  parsing within the same theory.
- The parser produces a **lossless concrete syntax tree** (`SyntaxTree`, `it.unibo.tuprolog.parser.tree`:
  `TermNode`, `ClauseNode`, `TheoryNode`, `OperatorExpressionNode`, etc.) rather than 2P-Kt `Term`s directly —
  "lossless" meaning source positions, comments, and token spans survive parsing, which is what later lets tools
  built on this layer report precise syntax errors (`PrologSyntaxException`, `SyntaxErrorCode`,
  `SyntaxExpectation`) and, in principle, support IDE-style tooling that needs to map back to source ranges.
- Platform-specific code in `parser-impl` is now confined to *feeding text in*, not to lexing or parsing it:
  `jvmMain` adapts a `java.io.Reader` into the common `TextChunkSource` abstraction
  (`ReaderToTextChunkSourceAdapter`), and `jsMain` adapts a JS `ReadableStream`
  (`JsReadableStreamTextChunkSource`). The grammar, the lexer, the parser, and the resulting syntax tree types are
  all ordinary multiplatform `commonMain` code.

## `parser-core`: from syntax tree to `Term`

`parser-core` (`it.unibo.tuprolog.core.parsing`) is the bridge from `parser-impl`'s syntax-tree world to
2P-Kt's actual term model (`:core`). `TermParser` is its public contract:

```kotlin
--8<-- "parser-core/src/commonMain/kotlin/it/unibo/tuprolog/core/parsing/TermParser.kt:20:35"
```

`TermParserImpl` walks a `SyntaxTree` (via `PrologTermParserVisitor`) and builds `Term`/`Clause` instances
against a `Scope`, so that variables sharing a simple name within one parsed expression are correctly identified
as the *same* `Var` (see [the `:core` variable/scope model](../reference/index.md)). This is the module 2P-Kt
users actually depend on for one-off term/clause parsing (`TermParser.withDefaultOperators`,
`String.parseAsTerm(...)`-style extensions), and it never exposes `parser-impl`'s syntax-tree types in its own
public API — keeping the CST-vs-domain-model boundary at exactly one module edge. `TermReader`, for reading a
lazy `Sequence<Term>` off a `Reader`/`InputStream`, remains a **JVM-only** addition on top of `TermParser`
(`parser-core/src/jvmMain`) — this JVM-only limitation flagged in the source talk still holds today; there is no
multiplatform equivalent yet.

## `parser-theory`: from clauses to a `Theory`

`parser-theory` (`it.unibo.tuprolog.theory.parsing`) is the thinnest of the three: `ClausesParser` builds on
`parser-core`'s clause parsing to parse a whole source string into a `Theory` (see
[Clause databases and indexing](clause-db-and-rete.md)) or a lazy `Sequence<Clause>`. It is not a separate
grammar or a separate parsing pass — the module-boundary reason to split it out from `parser-core` is that
`ClausesParser` depends on `:theory`, and `parser-core` deliberately does not, keeping single-term parsing usable
without pulling in the knowledge-base machinery. It also owns one piece of behaviour that only makes sense once
you have a *stream* of clauses rather than a single term: recognizing `op/3` directives as they are parsed and
feeding them back into the session's operator table so they affect only the clauses that follow, matching the
standard Prolog reading behaviour of `op/3` declarations. `ClausesReader` (JVM-only, mirroring `TermReader`)
provides the equivalent lazy `Reader`-based API for whole theories.

## Why this split

The three-module boundary (`parser-impl` → `parser-core` → `parser-theory`) tracks three genuinely different
concerns: *recognizing* Prolog syntax (a general parsing-theory problem, entirely reusable and testable without
any notion of "term" or "theory"), *interpreting* that syntax as 2P-Kt terms (a `:core`-specific concern), and
*assembling* a knowledge base out of a clause stream (a `:theory`-specific concern, including cross-clause state
like operator declarations). Each module depends only on what it actually needs — `parser-impl` depends on
nothing 2P-Kt-specific at all — which is also why replacing the entire ANTLR-based implementation with the
current hand-written lexer/parser was possible without changing `TermParser`'s or `ClausesParser`'s public API.
