# Web IDE architecture

See [Web IDE](../reference/web-ide.md) for what the app actually does, and
[Using the Web IDE](../tutorials/using-the-web-ide.md) for a walkthrough. This page is about *why* `:ide-web` is
built the way it is: why it's a thin shell around `:gui` rather than a separate implementation, how it gets a
real code editor into a browser, and why it's tested the way it is.

Why `:ide-web` is a Kotlin/JS-only multiplatform module in the first place — rather than, say, JVM-only like
`:ide-swing` — is covered in [Kotlin Multiplatform](kotlin-multiplatform.md); why it needs its own hand-rolled
`kotlin.multiplatform` plugin block instead of the shared project template is covered in
[Gradle multi-project build](gradle-multi-project-build.md). Neither is repeated here.

## One `:gui`, two shells

`:ide-web` contains almost no Prolog-editing logic of its own. State (`GuiState`), the action/dispatch loop
(`GuiController`), syntax analysis (`PrologSyntaxAnalyzer`), and solver-session management all live in `:gui`,
shared verbatim with `:ide-swing` (see [Module map](../reference/module-map.md)). What `:ide-web` actually
contains is a DOM-shaped *view*: `WebIdeView` builds/refreshes the page from a `GuiState` snapshot and turns DOM
events into `GuiAction`/`PageAction`/`WorkspaceAction`/`DocumentAction` dispatches — the same actions
`SwingIdeFrame` dispatches from Swing widgets instead. A bug fixed in `:gui` (a parser edge case, a diagnostic's
line/column convention, a solver-session race) is fixed for both frontends at once, by construction, not by
discipline.

## One highlighter for both frontends

Ace ships its own tokenizer/highlighter machinery (regex-based `TextHighlightRules`), and using it directly
would have meant maintaining Prolog's lexical rules twice — once in `:gui`'s real lexer, once again as Ace
regexes, with no way to guarantee they'd ever agree with each other or with what the parser actually accepts.
`AceCustomMode.kt` sidesteps that: it builds an Ace "Mode" object whose `getLineTokens` hook is backed directly
by `PrologSyntaxAnalyzer`'s semantic tokens (`AceEditorView.lineTokens`), the exact same analysis that produces
the Diagnostics tab's errors and the desktop IDE's own highlighting. Every other Ace mode hook this app doesn't
need (comment toggling, auto-indent, `transformAction`, autocompletion, background workers) is stubbed out —
deliberately returning a real JavaScript `undefined` rather than Kotlin's `Unit`, since `Unit` is a truthy
object as far as Ace's own `if (result) { ... }` checks are concerned, and would silently corrupt editing
behavior Ace expects to be a no-op.

## Ace, vendored the old way

Ace predates the ES module system by a decade; its own module loader expects to be a plain global script, not
something `import`ed into a webpack bundle. So `ace.js` and the two theme files (`theme-github.js`,
`theme-github_dark.js`) aren't part of the Kotlin/JS dependency graph at all — the `copyAceEditor` Gradle task
vendors them as static assets, and `index.html` loads them via ordinary `<script>` tags, *before* the compiled
`ide-web.js` bundle. `Ace.kt`'s `external` declarations then bind Kotlin code to whatever global object those
scripts leave behind, the same way any other pre-ES-module JS library gets used from Kotlin/JS.

## Two storage models, deliberately not one

A browser sandbox has no real file system to speak of, so "Save" can't mean the same thing it means in the
desktop IDE. `:ide-web` resolves this by not pretending it's the same thing: New/Open/Save/Save as write into
this browser's own `localStorage` (`LocalStorageDocumentStore`, keyed `tuprolog-fs:<name>`, a flat name→text
map with no versioning) — invisible outside that browser, but zero-friction and available offline. Upload/
Download instead go through the browser's real file picker/download machinery, for whenever a genuine `.pl`
file needs to leave the browser (emailing it, committing it, opening it in another tool). Nothing conflates the
two: a page saved with "Save" cannot be opened by another browser or another machine, only by "Upload"ing an
actually-downloaded file.

Editor zoom (font size) is the one setting explicitly shared *across* the two frontends despite being purely
cosmetic: `EditorZoom` (`:gui`) is the single source of the default font size and the 8–48px clamp range, used
by both `SwingIdeFrame`'s RSyntaxTextArea and `AceEditorView`'s Ace instance, specifically because the two used
to drift (8–48 on one frontend, 8–40 on the other) before being unified into one shared constant.

## Two-tier testing

`jsBrowserTest` (Karma + headless Chrome, part of `./gradlew check`) mounts individual Kotlin classes —
`AceEditorView`, `LocalStorageDocumentStore`, the workspace-persistence logic — in isolation, fast and
deterministic. What it structurally *cannot* catch is a bug in how those pieces are wired together on the real
page, because it never boots `index.html` at all. `scripts/browser-e2e-test.mjs` exists for exactly that gap: a
small custom harness that serves the packaged distribution over plain HTTP, drives a real headless Chrome over
the Chrome DevTools Protocol, and exercises the app the way a person would — typing through Ace's real input
pipeline (`execCommand('insertText', ...)` on its focused textarea, not calling a Kotlin setter directly),
clicking real buttons, reading back the rendered DOM. It is deliberately *not* part of `check` (it needs a
production webpack build and a local Chrome, both too heavy for the fast, tight inner loop `check` is for), and
instead runs as its own CI step (`:ide-web:browserE2ETest`) and via
[its own how-to guide](../how-to/deploy-the-web-ide.md#4-verify-the-deployment) for anyone self-hosting the app.

This isn't a hypothetical distinction: every scenario in that script exists because it once failed silently
past `jsBrowserTest` and only surfaced this way — a missing `SolverFactory` import, a shadowed `replaceWith`
that silently detached DOM nodes, and an `Ace.transformAction` no-op that discarded every keystroke were all
first caught by driving a real browser against a real build, not by unit-testing the pieces in isolation.
