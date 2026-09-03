# 2P-Kt GUI, IDE, and REPL Refactoring Plan

## Implementation-grade architectural specification for an AI coding agent

**Repository:** `https://github.com/tuProlog/2p-kt`  
**Baseline branch:** `master`  
**Historical reference branch:** `feature/gui` only  
**Prepared / revised:** 2026-09-02  
**Primary scope:** `ide`, `ide-plp`, `repl`, plus new presentation/application modules  
**Primary objective:** permanently remove JavaFX from the end-user tools while extracting a reusable Kotlin Multiplatform application layer

---

## 1. Purpose of this document

This document is intended to be handed directly to an AI coding agent as the implementation plan for a substantial refactoring of the 2P-Kt user-facing tools.

The plan is based on source-level reverse engineering of the current `master` implementations of:

- `ide`
- `ide-plp`
- `repl`
- their Gradle configuration and release integration
- the abandoned `feature/gui` branch, used only as architectural archaeology

The goal is not merely to replace JavaFX widgets with Swing or Compose widgets. The goal is to isolate the semantics of an interactive tuProlog application from any particular presentation technology, then reuse those semantics across desktop, browser, and terminal frontends.

The target architecture must make it possible to support different solver families, including probabilistic solvers, and frontend-specific additional components without coupling the shared application model to JavaFX, Swing, Compose, the browser DOM, or terminal libraries.

This document deliberately records both:

1. **current externally visible functionality that should normally be preserved**
2. **current implementation accidents, limitations, incomplete features, and probable bugs that should not automatically become compatibility requirements**

The implementation agent should use this distinction throughout the migration.

---

# 2. High-level target

The final repository should conceptually contain:

```text
:gui
:gui-plp

:ide-swing
:ide-plp-swing

:ide-compose
:ide-plp-compose

:ide-web
:ide-plp-web

:repl
:repl-plp
```

The existing modules:

```text
:ide
:ide-plp
```

should eventually be removed.

The old JavaFX implementation should remain in the repository during most of the migration as a behavioural reference and should be deleted only after replacement frontends reach sufficient parity.

The core target dependency direction is:

```text
                         +-------------------+
                         |       :gui        |
                         | toolkit-neutral   |
                         | app semantics     |
                         +---------+---------+
                                   |
                 +-----------------+------------------+
                 |                 |                  |
                 v                 v                  v
          +-------------+   +-------------+    +-------------+
          | :ide-swing  |   |:ide-compose |    |  :ide-web   |
          +------+------+   +------+------+    +------+------+
                 |                 |                  |
                 |                 |                  |
                 +-----------------+------------------+
                                   |
                         frontend adapters only

                         +-------------------+
                         |      :repl        |
                         | terminal adapter  |
                         +---------+---------+
                                   |
                                   v
                                  :gui

                         +-------------------+
                         |     :gui-plp      |
                         | common PLP app    |
                         | extension logic   |
                         +---------+---------+
                                   |
               +-------------------+-------------------+
               |                   |                   |
               v                   v                   v
      :ide-plp-swing      :ide-plp-compose      :ide-plp-web
                                   |
                                   v
                               :repl-plp
```

The exact Gradle graph will not literally be shaped as the ASCII drawing above, but these dependency principles are mandatory:

- `gui` must never depend on a concrete frontend
- frontend modules depend on `gui`
- `gui-plp` depends on `gui` and PLP-domain/solver libraries
- PLP frontend modules are thin combinations of the corresponding base frontend plus `gui-plp`
- frontend-specific PLP renderers remain outside `gui-plp`
- `repl-plp` should be a thin extension of `repl` using the same PLP semantics


## 2.1 Locked design decisions introduced by this revision

The following decisions are part of the target architecture and should not be reopened implicitly during implementation:

- **Query state is page-specific**, not application-global
- **Resolution state, solver-session state, solution/history state, console streams, diagnostics, inspectors, and session-derived extension state are page-specific**
- **`Document` and `Page` are distinct entities**: documents represent source/persistence; pages represent interactive sessions/views over content
- **`selectedPageId` is presentation/navigation state only** and must never be used to retarget asynchronous results
- **Every solver/query action carries an explicit `PageId`; every source/persistence mutation carries an explicit `DocumentId`**
- **The shared core permits independent page solver sessions and cross-page concurrency**; a product may restrict concurrency through an explicit scheduling policy
- **Live solver objects, jobs, workers, and other runtime resources stay outside immutable public state**
- **Solver sessions should be lazy/rebuildable and revision-aware**
- **Configuration is hierarchical**, with application/workspace defaults and page overrides
- **Extensions contribute semantic features declaratively where practical**, while toolkit renderers remain frontend-specific
- **Capabilities, semantic features, and native renderers are separate concepts**
- **Semantic tokens/source diagnostics should be shared across frontends where existing parser coordinate abstractions permit it**
- **`ide-web` remains a dedicated Kotlin/JS serverless product rather than being replaced by Compose/Wasm** under the current requirements.

---

# 3. Non-negotiable architectural objectives

The implementation must satisfy the following principles.

## 3.1 Eliminate JavaFX completely at the end

After migration, the source and dependency graph should contain no dependency on:

- JavaFX
- ReactFX
- RichTextFX

This includes direct dependencies, Gradle helpers used only for JavaFX packaging, FXML files, JavaFX-specific CSS where no longer applicable, and JavaFX types exposed through extension APIs.

## 3.2 Make `gui` genuinely Kotlin Multiplatform

Nothing in `gui/commonMain` may expose or import:

- `java.*`
- `javax.swing.*`
- `javafx.*`
- `org.reactfx.*`
- `org.fxmisc.*`
- AWT/Swing types
- Compose UI types
- browser DOM types
- JVM executors
- filesystem paths as application-level document identity

Kotlin common types, tuProlog common abstractions, and maintained Kotlin Multiplatform libraries are acceptable.

## 3.3 Keep model/controller semantics independent from the view

The shared layer should own:

- workspace, document, and page state
- page-specific query/history state
- page-scoped solve/resolution state
- page-scoped solver-session lifecycle
- resolution lifecycle
- solve options
- output/warnings/errors as application data/events
- solver-context snapshots needed by inspectors
- page-local in-memory query history
- document dirty state
- extension state
- action validation
- command handling
- application-level errors

The shared layer must not own:

- actual Swing/Compose/DOM widgets
- pixel/layout concerns
- platform window lifecycle details
- toolkit-specific dialogs
- toolkit-specific clipboard objects
- toolkit-specific file chooser objects

## 3.4 Solver creation must be injected

The shared application core must not hard-code the equivalent of:

```kotlin
Solver.prolog.newBuilder()
```

as its only solver construction mechanism.

It must support classic, PLP/ProbLog, and future solver configurations without changing `GuiController` or the base model.

## 3.5 Extensions must be semantic first, graphical second

Current JavaFX extensions can inject custom JavaFX tabs and configure the model directly. This is too tightly coupled.

The new mechanism must distinguish:

- a cross-platform semantic extension
- a frontend-specific renderer for the extension

A PLP extension, for example, should define probability/BDD semantics once, while Swing, Compose, Web, and terminal each decide how to render those semantics.

## 3.6 Behaviour must be testable without a graphical toolkit

Most application behaviour should be testable in `gui/commonTest` using deterministic sequences of:

```text
actions -> states -> events/effects
```

The historical `feature/gui` tests are useful evidence that this is feasible.

## 3.7 Releases must distribute end-user tools explicitly

The release must produce self-contained artifacts for each user-facing tool rather than relying on the current generic `*redist*.jar` collection strategy.

The intended logical products are:

- `ide-swing`
- `ide-compose`
- `ide-web`
- `repl`
- `ide-plp-swing`
- `ide-plp-compose`
- `ide-plp-web`
- `repl-plp`

---

# 4. Current repository context

## 4.1 Relevant current modules

The current `master` Gradle settings include, among many others:

```text
:solve
:solve-classic
:solve-concurrent
:solve-plp
:solve-problog
:io-lib
:oop-lib
:parser-theory
:ide
:ide-plp
:repl
```

The current GUI products are therefore not isolated from solver/library concerns; they directly construct and configure solvers.

## 4.2 Current technology versions relevant to this migration

At the time of inspection, the version catalogue declares approximately:

- Kotlin `2.4.10`
- JVM target `17`
- kotlinx-coroutines `1.11.0`
- JavaFX `21.0.8`
- Graphviz Java `0.18.1`
- RichTextFX `0.11.7`

An important observation is that `kotlinx-coroutines-core` and `kotlinx-coroutines-test` are already part of the repository's dependency vocabulary. Therefore using coroutine `Flow`, `StateFlow`, `SharedFlow`, and structured concurrency in the new shared application core does not introduce an unrelated concurrency ecosystem.

---

# 5. Reverse engineering of the current `ide`

This section is intentionally detailed because the replacement IDEs should not accidentally omit functionality hidden behind the JavaFX implementation.

## 5.1 Current module dependencies and packaging

The current `ide` module is JVM/JavaFX based.

Its build currently depends directly on application/domain modules including:

- `io-lib`
- `oop-lib`
- `parser-theory`
- `solve-classic`
- RichTextFX
- JavaFX base/controls/FXML/graphics artifacts for supported desktop platforms

It produces a JavaFX fat JAR with entry point:

```text
it.unibo.tuprolog.ui.gui.Main
```

The module description calls it a customisable JavaFX-based GUI for logic solvers.

No `src/test` tree is present in the current module. In other words, most current GUI semantics are effectively untested at the module level.

## 5.2 Current source-level decomposition

The current IDE contains the following major classes/resources:

```text
AssignmentView.kt
CustomTab.kt
FileTabView.kt
LibraryView.kt
ListCellView.kt
ModelConfigurator.kt
RegexExtensions.kt
Resources.kt
SolutionView.kt
SolverEvent.kt
SyntaxColoring.kt
SyntaxException.kt
TuPrologIDEApplication.kt
TuPrologIDEBuilder.kt
TuPrologIDEController.kt
TuPrologIDEModel.kt
TuPrologIDEModelImpl.kt
```

and FXML/CSS resources including:

```text
AssignmentView.fxml
FileTabView.fxml
SolutionView.fxml
TuPrologIDEView.fxml
styles/light-theme/syntax-coloring.css
styles/dark-theme/syntax-coloring.css
```

The architecture is nominally model/controller/view-oriented, but the actual boundaries are heavily JVM/JavaFX specific.

## 5.3 Current model API

`TuPrologIDEModel` exposes application state and operations such as:

- solve options
- IDE state
- executor
- solver customisation
- new file
- load file
- save file
- current file
- select file
- close file
- retrieve file content
- update file content
- rename file
- stdin
- quit
- solve
- solve all
- next
- next all
- stop
- reset
- query

Its state machine is approximately:

```text
IDLE
COMPUTING
SOLUTION
```

It also exposes many event streams, including events for:

- reset
- quit
- solve options changes
- file selection
- file creation
- file loading
- file closing
- query changes
- creation of a new solver
- loading of a new static knowledge base
- starting a new query
- resolution start
- new solution
- resolution end
- query end
- stdout
- stderr
- warnings
- errors

### Architectural problem

Despite being called the model, its public API directly exposes JVM/ReactFX types such as:

- `java.io.File`
- `ExecutorService`
- ReactFX `EventStream`

Therefore the current model cannot serve as the common model for Swing, Compose Multiplatform, Kotlin/JS, or the terminal.

This is one of the strongest reasons to create a new module rather than trying to relocate the existing model unchanged.

## 5.4 Current document model

Internally, the model keeps a mapping approximately equivalent to:

```text
File -> FileContent(text, changed)
```

Important current behaviours include:

- `newFile()` creates a real temporary file on the JVM filesystem
- untitled documents therefore already have a `java.io.File` identity
- loaded file content is read fully into memory
- saving writes current text to disk
- changing text marks the current file as changed
- selecting another file can mark the previously selected file as changed in the current implementation

That last behaviour is unusual and appears tied to forcing the static knowledge base to refresh when tabs change. It should not be preserved by accident. The new model should distinguish true document dirty state from solver-cache invalidation.

### Target implication

The new application model should represent documents independently from physical files.

At minimum distinguish:

```text
DocumentId
DocumentState
DocumentOrigin / external persistence metadata
text
isDirty
```

An untitled document should not require creation of an actual temporary filesystem file.

## 5.5 Current IDE startup behaviour

The JavaFX controller constructs the model itself rather than receiving it through dependency injection.

At initialisation it approximately:

- subscribes to all model event streams
- initialises custom solution cells
- initialises warnings/operator/flag/library views
- creates a fresh new file
- resets the solver/application state

This direct model construction is another dependency-inversion problem to remove.

## 5.6 Current window/menu structure

The FXML defines an IDE-like window with menus:

### File

- New
- Open
- Open Recent — present but disabled
- Close
- Save
- Save As
- Revert/reload
- Settings — present but effectively unimplemented/disabled
- Quit

### Edit

- Undo
- Redo
- Cut
- Copy
- Paste
- Delete
- Select All
- Unselect All

### Help

- About

The target IDEs should preserve the implemented commands. `Open Recent` and `Settings` should be treated as current placeholders, not mandatory parity features unless deliberately implemented during the refactoring.

## 5.7 Current main layout

The main view is vertically divided into:

1. a file/editor tab area
2. a query/solution/context-inspector area

The lower area exposes tabs approximately corresponding to:

- Solutions
- Stdin
- Stdout
- Stderr
- Warnings
- Operators
- Flags
- Libraries
- Static KB
- Dynamic KB

A query row contains approximately:

```text
?- [query editor] [Solve/Next] [Solve all/All next] [Stop] [Reset]
```

The status area exposes:

- current textual status
- a progress bar
- timeout control/value
- caret line/column information

This collection of panels is a parity requirement for graphical IDEs unless a specific item is marked otherwise below.

## 5.8 Multi-document editing

The IDE supports multiple open documents via tabs.

Current behaviour includes:

- new document creation
- opening files
- selecting documents
- closing documents
- saving
- save-as
- reloading/reverting from disk
- keeping editor content associated with each tab

The application has one selected/current file at a time.

The query field is global in the current implementation rather than scoped per document. The current solution/resolution workflow is therefore also effectively global from the user's point of view.

### Required deliberate improvement

The replacement architecture **must not preserve the global query bar**. A graphical tab/page is the unit of interactive Prolog work: switching page must restore that page's query, current/past resolution results, console streams, diagnostics, solver-session state, and applicable page configuration.

The design must also distinguish a **Document** from a **Page**. A document is persistent/editable source content; a page is an interactive view/session that may reference a document and owns query/session state. This permits untitled/scratch pages and leaves room for multiple pages over the same document without forcing that feature into the first UI release.

This is an intentional semantic improvement over the JavaFX implementation and is a hard target requirement, not an optional follow-up. It must be covered by explicit isolation tests.

## 5.9 Current file chooser behaviour

Opening a file uses a JavaFX `FileChooser`, starting approximately from the user's home directory and filtering for:

- Prolog files such as `*.pl`, `*.2p`
- text files
- all files

Save-as also uses a JavaFX file chooser.

The browser cannot reproduce filesystem semantics literally. Therefore open/save must become platform effects or platform services with frontend-specific implementations.

## 5.10 Current unsaved-change behaviour

The current implementation tracks whether content changed for solver-loading purposes, but the UI does not provide a robust modern dirty-document workflow.

In particular, current close/reload paths do not appear to implement a consistent confirmation-before-discard model.

This should not be copied as a compatibility requirement.

The new shared model should introduce explicit document dirty semantics and allow frontends to request confirmation before destructive operations.

Recommended behaviour:

```text
close clean document -> immediate
close dirty document -> effect requesting Save / Discard / Cancel
reload clean document -> immediate
reload dirty document -> confirmation first
quit with dirty documents -> aggregate confirmation workflow
```

This is a desirable correctness improvement.

## 5.11 Rich text editor functionality

The current editor uses RichTextFX `CodeArea`.

It provides at least:

- text editing
- line numbers
- caret reporting
- syntax colouring
- undo/redo
- cut/copy/paste
- delete
- select all/unselect all
- keyboard handling
- dynamic editor font size changes using Ctrl+plus/minus, with a configured minimum/default size

Swing/Compose/Web should preserve useful editing behaviour while not necessarily reproducing RichTextFX APIs.

## 5.12 Current syntax highlighting

The current `SyntaxColoring` implementation performs asynchronous/debounced syntax colouring.

It recognises categories including:

- comments
- operators/keywords
- parentheses
- braces
- brackets
- functors
- atoms
- variables
- numbers
- strings
- full stop

Numeric handling includes multiple Prolog forms such as decimal, hexadecimal, binary, octal, floating point, and character-like forms.

The current colouring logic also depends on the active solver operator set, meaning syntax highlighting can change when solver context changes.

This behaviour is important: syntax highlighting is not purely static lexical colouring.

### Recommended new design

Move the **classification calculation**, where practical, into a toolkit-neutral service in `gui`:

```text
SyntaxHighlightingService
    input: document text + effective operator set for the requesting page/session
    output: List<SemanticToken> with shared source ranges
```

Frontend adapters then translate semantic token categories into:

- Swing styled-document attributes
- Compose annotated text/editor styles
- DOM/editor decorations

Do not put toolkit styles, colours, fonts, CSS classes, or Swing attributes into `gui`.

If the new parser/lexer already provides high-quality lexical token metadata, prefer building highlighting on that rather than maintaining an independent large regex grammar. However, preserve the ability to account for the current dynamic operator set.

## 5.13 Light/dark syntax resources

The current JavaFX resources contain light and dark syntax CSS variants, but the existing UI does not appear to expose a complete theme-selection workflow through the disabled Settings menu.

Therefore:

- syntax theming is useful to retain
- a runtime theme settings UI is not strict current-feature parity
- Compose can naturally support light/dark themes
- Swing and Web may do likewise
- do not claim the current IDE already has a complete theme settings feature

## 5.14 Current solver construction in the IDE

The model lazily/cached-constructs a classic Prolog solver using the classic solver factory/builder.

Its default configuration includes approximately:

- classic Prolog solver
- `OOPLib`
- `IOLib`
- `TrackVariables` enabled
- configured stdin
- stdout channel connected to IDE events
- stderr channel connected to IDE events
- warning channel connected to IDE events
- optional custom solver transformation/customisation

The customisation hook is how `ide-plp` replaces the solver.

### Target implication

All of the above needs to become explicit configuration rather than hidden assumptions in a model implementation.

## 5.15 Current static/dynamic knowledge-base lifecycle

When starting a resolution, the current implementation:

- uses the current document as the source of the static KB
- parses it using the relevant parser/operator configuration
- loads it into the solver when considered changed
- resets the dynamic KB when reloading the static KB
- emits context-change events

A full reset regenerates the solver and reloads the current theory.

An important subtlety is that dynamic KB state can remain meaningful across operations when the static KB is not reloaded. This should be investigated with tests before changing semantics.

The new shared core should make solver-session lifecycle explicit rather than encoding it through the file `changed` flag.

Recommended concepts:

```text
SolverSessionId
SolverSessionState
DocumentRevision
LoadedDocumentRevision
ResolutionSessionId
```

A solver session should know which document revision its static KB represents.

## 5.16 Query parsing and syntax errors

Queries are parsed using the solver's current operators.

Theory and query parsing errors are surfaced distinctly.

The current `SyntaxException` carries useful diagnostic information including concepts such as:

- line
- column
- clause index
- error message
- source file for theory errors

The IDE displays different messages for theory syntax errors and query syntax errors.

The replacement should preserve and ideally improve source diagnostics.

### Multiplatform requirement

The new syntax error model must not carry `java.io.File`.

Use `DocumentId`, source name, and source coordinates instead.

## 5.17 Empty-query behaviour

The current syntax-error wrapper has explicit handling for an empty query.

The new common controller should make empty-query handling deterministic and test it directly.

## 5.18 Current solve controls and semantics

The IDE exposes:

- Solve
- Solve all
- Next
- All next
- Stop
- Reset

Button labels change depending on resolution state.

Roughly:

```text
IDLE:
    Solve
    Solve all

SOLUTION:
    Next
    All next
```

During computation controls are selectively disabled.

## 5.19 Current resolution state flow

Current event semantics include:

- new query
- resolution started
- resolution ended
- new solution
- query ended

A solution iterator is retained between `Solve` and `Next` operations.

`solveAll`/`nextAll` automatically consume successive solutions.

This is a strong candidate for a shared state machine in `gui`.

Recommended target model:

```text
Idle
Running(sessionId, documentId, query)
AwaitingContinuation(sessionId, lastSolution, hasMorePotentially)
Completed(sessionId)
Failed(sessionId, error)
Cancelled(sessionId)
```

The externally exposed state may be simpler, but internally the implementation needs an unambiguous session identity.

## 5.20 Important current `Stop` limitation

The current IDE cannot genuinely interrupt a currently blocking solver computation.

The controller disables `Stop` while a resolution step is actually computing, and the model's `stop()` operation is valid only when it is already in the `SOLUTION` state.

In practice `Stop` means approximately:

> abandon continuation after a yielded solution

rather than:

> interrupt the currently running solver computation

This is a current limitation, not a behaviour to preserve.

### Target requirement

The new application core should support real cancellation where the underlying solver API permits it.

Implementation tasks:

1. run each resolution session inside a coroutine `Job`
2. associate every produced result/event with a unique resolution session identifier
3. on stop, cancel the active job
4. invoke any solver-specific close/cancel mechanism if available
5. prevent stale results from cancelled sessions from mutating current state
6. test cancellation against representative classic and probabilistic solves
7. if some solver call is synchronously non-cancellable, document that fact and isolate execution behind an abstraction that can use a worker/thread where supported

This is also essential for browser responsiveness.

## 5.21 Current timeout control

The GUI exposes a timeout slider/value.

The current slider maps a slider value logarithmically to milliseconds, roughly using a power-of-ten relationship.

The model default is approximately 5000 ms.

The replacement frontends do not need identical slider mechanics, but they should expose equivalent timeout control and use a common timeout representation in `gui`.

Prefer Kotlin `Duration` where compatible with the relevant tuProlog APIs, converting only at boundaries.

## 5.22 Stdin behaviour

The IDE exposes a Stdin tab/text area.

In the current implementation the stored stdin value can be changed while idle; changing it invalidates/recreates the cached solver. During active resolution, stdin editing is disabled.

This is not the same as a fully interactive terminal stream that can supply data on demand while a computation is blocked waiting for input.

The new architecture should initially preserve the supported semantics, while keeping solver I/O adapters sufficiently abstract to allow richer interactive input later.

## 5.23 Stdout, stderr, warnings

The IDE has dedicated views for:

- stdout
- stderr
- warnings

Solver channels push content into corresponding event streams.

The controller appends new data and places notification markers on tabs that are not currently selected.

This behaviour should be represented in shared application state/events so all graphical frontends can implement equivalent output panels.

For terminal frontends, streams naturally map to terminal output channels.

## 5.24 Background-tab notification markers

The JavaFX UI appends `*` to the title of certain tabs when new data arrives while the tab is not selected.

Selecting the tab clears/hides the marker.

This is presentation behaviour rather than domain logic, but the semantic information that a panel contains unseen updates can be shared.

Recommended shared representation:

```text
PanelState.hasUnreadChanges
```

or a monotonically increasing revision counter per panel.

Do not expose the literal `*` convention from `gui`.

## 5.25 Solver-context inspection

One of the most significant current IDE capabilities is inspection of solver execution context.

The current IDE displays:

- Operators
- Flags
- Libraries
- Static KB
- Dynamic KB

A `SolverEvent` captures an execution-context snapshot including approximately:

- unificator
- operators
- libraries
- flags
- static KB
- dynamic KB
- input channels
- output channels

The UI compares snapshots/events to determine which inspector tabs should be updated/notified.

### Target requirement

Create a framework-neutral immutable `SolverSnapshot` containing only data actually useful to frontends.

Do not expose an entire mutable solver/execution context through the UI state.

Possible shape:

```kotlin
data class SolverSnapshot(
    val operators: List<OperatorDescription>,
    val flags: List<FlagDescription>,
    val libraries: List<LibraryDescription>,
    val staticKb: TheoryPresentation,
    val dynamicKb: TheoryPresentation,
    val revision: Long,
)
```

The exact types should preserve useful tuProlog domain objects where they are common and immutable, avoiding redundant presentation DTOs if unnecessary.

## 5.26 Operators view

The current operator table is updated from the solver context.

Operators are also fed back into syntax colouring.

This means the following flow must remain possible:

```text
solver context changes
    -> operator snapshot changes
    -> editor syntax classification invalidated
    -> editor decorations recomputed
```

This is an explicit cross-component dependency to model cleanly.

## 5.27 Flags view

The current IDE shows Prolog flags in a table-like representation.

Equivalent content should be available in all graphical IDEs and in textual form in the REPL where useful.

## 5.28 Libraries view

The JavaFX IDE renders a tree of loaded libraries.

For each library alias it exposes categories approximately corresponding to:

- Functions
- Predicates
- Operators

Predicate information includes both rule head indicators and primitives, deduplicated/sorted.

The shared application layer should expose enough library metadata to let all frontends render equivalent information.

## 5.29 Static and dynamic KB views

The current IDE renders static and dynamic knowledge bases in separate inspector tabs using formatted theory text.

These are user-visible capabilities and should be retained.

Do not tie them to JavaFX `TextArea` semantics.

## 5.30 Solution presentation

The classic IDE renders different solution variants distinctly.

### `Yes`

It shows approximately:

- positive/green visual indication
- the solved query
- substitution assignments

### `No`

It shows approximately:

- negative/neutral indication
- `no.`-like result

### `Halt` caused by timeout

It shows a timeout-specific indication/message.

### Other `Halt`

It shows approximately:

- halt indication
- exception message
- logic stack trace entries

Term formatting uses existing tuProlog formatting utilities.

### Target requirement

Create a framework-neutral solution presentation model or formatter logic shared by frontends.

For example:

```text
SolutionPresentation
    Yes(query, assignments, metadata)
    No(query)
    Halt(query, message, logicStackTrace, timeout)
```

Do not make `gui` return pre-styled HTML or Swing components.

## 5.31 About dialog

The current builder exposes a default About dialog containing information such as:

- 2P-Kt version
- JVM information
- JavaFX version

The new application core should expose product/version metadata. Individual graphical frontends can display framework/runtime information appropriate to themselves.

The semantic parity requirement is an About/product-information affordance, not specifically a JavaFX version string.

## 5.32 Current JavaFX extension mechanism

`TuPrologIDEBuilder` exposes JavaFX-specific customisation through concepts such as:

- `Stage`
- title
- JavaFX `Image`
- close handler
- about handler
- stylesheets
- custom libraries
- custom tabs

`CustomTab` directly contains a JavaFX `Tab` plus a `ModelConfigurator` callback.

`ModelConfigurator` directly receives the current `TuPrologIDEModel`.

Custom tabs can replace an existing tab by matching JavaFX tab IDs.

This is how `ide-plp` replaces the standard solution view.

### Target implication

The ability is important; the mechanism must be replaced.

Do not implement a new shared `CustomTab` that contains Swing/Compose/DOM components.

Instead introduce semantic feature contributions plus frontend renderer registration, described later in this document.

---

# 6. Reverse engineering of the current `ide-plp`

## 6.1 Current role

`ide-plp` is a JavaFX application layered directly on top of `ide`.

Its build depends on approximately:

- `:ide`
- `:solve-problog`
- Graphviz Java

It also produces a JavaFX fat JAR, with entry point approximately:

```text
it.unibo.tuprolog.ui.gui.PLPMain
```

Like `ide`, the current module has no dedicated test source tree.

## 6.2 Current extension strategy

`PLPIDEApplication` demonstrates exactly which extensibility features the replacement architecture must support.

It:

1. creates a replacement Solutions tab
2. gives it the same tab ID as the normal solutions tab so it substitutes the standard renderer
3. attaches PLP-specific solution cells
4. initialises Graphviz support
5. configures the base IDE model
6. changes solve options to probabilistic mode
7. replaces/customises the classic solver with a ProbLog solver
8. preserves standard stdin/stdout/stderr/warning channels
9. retains `OOPLib` and `IOLib`
10. reacts to reset/new-query/new-solution events

Therefore the new extension system must support more than merely adding a panel.

It must support at least:

- alternate solver profiles
- solver-specific solve options
- replacement or augmentation of a standard semantic feature such as solution presentation
- extension-specific state derived from solutions
- extension-specific actions
- frontend-specific additional rendering

## 6.3 PLP solver setup

The current PLP IDE uses a probabilistic/ProbLog mutable solver with default builtins and configures the solve options to probabilistic mode.

The new design should move this into `gui-plp` as a reusable solver/application profile rather than repeating solver construction in four products.

## 6.4 PLP solution presentation

`PLPSolutionView` extends normal solution semantics.

For a successful probabilistic solution it displays approximately:

- normal query/substitution information
- probability as a percentage
- a BDD button when a binary decision diagram is available

`No` and `Halt` variants remain broadly analogous to the classic view.

### Target common representation

`gui-plp` should convert PLP-specific solution data into a toolkit-neutral state such as:

```kotlin
data class PlpSolutionDetails(
    val probability: Double?,
    val bdd: BddPresentation?,
)
```

where a `BddPresentation` might contain at least the DOT graph representation or enough domain data to derive it.

The actual graphical renderer belongs to the frontend.

## 6.5 Current BDD workflow

The JavaFX PLP application can inspect a binary decision diagram associated with a solution.

The current graph view supports:

- showing DOT source text
- copying DOT text to clipboard
- rendering the DOT graph to an image
- displaying the rendered image
- saving the rendered image
- showing Graphviz availability/initialisation state
- progress/status indication for asynchronous rendering

The current save workflow suggests a default PNG filename based on time.

This functionality is easy to overlook and must be included in PLP parity planning.

## 6.6 Current Graphviz implementation

Graph rendering is JVM-specific and uses Graphviz Java.

It asynchronously checks Graphviz/rendering availability and renders DOT to PNG bytes.

This implementation cannot be moved into `gui-plp/commonMain`.

### Target design

Keep common BDD semantics in `gui-plp` and provide rendering adapters:

```text
ide-plp-swing
    JVM Graphviz renderer or other Swing/JVM-compatible renderer

ide-plp-compose
    JVM/native-compatible renderer appropriate to Compose target

ide-plp-web
    browser-side DOT renderer using a maintained JS/browser Graphviz implementation

repl-plp
    textual/DOT output, with optional save/export command if useful
```

The common extension should not require that every frontend can render a PNG. It should expose a semantic graph/DOT representation and frontend capabilities.

---

# 7. Reverse engineering of the current `repl`

The REPL is particularly important because it tests whether the proposed `gui` abstraction really captures application semantics rather than graphical widgets.

## 7.1 Current module shape

The current `repl` module is Kotlin Multiplatform and has approximately:

```text
commonMain
jvmMain
jsMain
```

It depends in common code on libraries including:

- Clikt
- core
- `oop-lib`
- `io-lib`
- `solve-classic`
- `parser-theory`

It currently produces a fat JAR for the JVM with entry point approximately:

```text
it.unibo.tuprolog.ui.repl.Main
```

No current dedicated test tree was found under the module.

## 7.2 Current command-line options

The main command supports approximately:

```text
-T, --theory <path>
-t, --timeout <milliseconds>
--oop
```

The theory option can be provided multiple times.

The default timeout is approximately 1000 ms, which differs from the current IDE's approximate 5000 ms default.

The `--oop` flag enables OOP support in the REPL.

This difference matters when extracting shared solver profiles: the IDE currently includes OOP functionality by default, whereas the REPL makes it opt-in.

Do not accidentally change product behaviour merely because both products share solver construction code.

## 7.3 Current additional-library injection

`TuPrologCmd` can be constructed with additional libraries.

This is conceptually similar to the IDE builder's custom-library injection and should become a common solver/application extension mechanism.

## 7.4 Startup theory loading

The REPL can load one or more theories from paths supplied using `-T/--theory`.

Current behaviour includes:

- starting from an empty theory
- loading each readable file
- merging loaded theories
- reporting the number of loaded clauses
- reporting parse errors with source/line/column/clause information

Files that fail the current readability predicate are effectively skipped rather than producing a rich error workflow.

The new shared document/theory-loading logic should make failure semantics explicit.

## 7.5 Current JS gap in file loading

The common REPL code declares platform-specific file helpers.

The JVM implementation uses `java.io.File` and parses the theory from file contents.

The inspected JS implementation currently contains `TODO("Not yet implemented")` for the corresponding readable-file/theory-file operations.

Therefore the current REPL is structurally multiplatform but does not provide equivalent `-T` filesystem behaviour on JS.

This should be recorded as an existing gap, not silently treated as working cross-platform functionality.

If the target `repl` continues to have JS targets, the implementation agent should either:

- implement an appropriate JS/Node file abstraction
- or explicitly narrow/document the supported target matrix

Do not leave platform `TODO`s in released code.

## 7.6 Current REPL solver construction

At startup, the REPL constructs a classic Prolog solver.

Its configuration includes approximately:

- classic solver
- static KB from merged theory files
- `TrackVariables` enabled
- `IOLib`
- optional `OOPLib` controlled by `--oop`
- additional injected libraries
- warning channel mapped to stderr

It prints loaded library aliases.

Like the IDE, solver creation is hard-coded to the classic solver family.

The new common solver profile mechanism should remove this duplication while preserving frontend-specific defaults.

## 7.7 Current interactive prompt

The REPL prompts for a query using approximately:

```text
?- 
```

It supports continuation/multiline input using a secondary prompt approximately:

```text
> 
```

until a complete query ending in a full stop is entered.

EOF causes a goodbye message and clean termination.

Null input has a dedicated exception path.

## 7.8 Query parsing

Queries are parsed as structures using the active solver operator set.

Parse failures are printed to stderr with a `#`-style diagnostic prefix.

This mirrors the IDE requirement that parsing depends on active operators.

## 7.9 Current interactive solution continuation

The current REPL iterates solutions.

After a first solution, when additional solutions may exist, it prompts the user for continuation.

Entering approximately:

```text
;
```

requests another solution.

Other input terminates continuation.

This is the terminal equivalent of the IDE's `Next` action.

This semantic relationship is an excellent reason to share resolution-session logic through `gui`.

Conceptually:

```text
IDE Next button
    ==
REPL semicolon continuation
    ==
GuiAction.NextSolution
```

## 7.10 Current solution rendering

The REPL formats normal Yes/No solutions using tuProlog formatters.

For halt conditions:

- some halt cases produce a goodbye/program exit status
- other halt conditions are formatted as errors/results

The new `SolutionPresentation` abstraction should be reusable by terminal and graphical adapters while still permitting terminal-specific formatting.

## 7.11 `solve` subcommand

The REPL also exposes a `solve` subcommand.

It accepts:

- a required query argument
- `-n/--numberOfSolutions`, default approximately zero

Current semantics are notable:

- with a positive solution limit, it prints up to that many solutions automatically
- with the default zero branch, it reuses the interactive continuation routine and may therefore prompt with semicolon-style continuation when multiple solutions are available

This behaviour should be tested and consciously preserved or revised. Do not unknowingly change it while refactoring.

An empty-query branch in the current command implementation appears incomplete/TODO-like and should be resolved explicitly in the new tests.

## 7.12 REPL warnings and logic stack traces

Warnings are sent to stderr and include the warning message plus formatted logic stack information.

This should remain available in the terminal frontend and can use the same warning semantic model as graphical frontends.

## 7.13 Why adapting `repl` to `gui` is sensible

The REPL already performs most of the same semantic operations as the IDE:

```text
configure solver
load theory
parse query
start resolution
consume solution
request next solution
apply timeout
render errors/warnings
manage process/application lifecycle
```

The primary difference is the presentation surface.

Therefore reusing the common application/controller layer is architecturally useful.

However, do **not** force graphical concepts onto the REPL. `gui` should really become a toolkit-neutral interactive application core despite its historical/project name.

If the terminal integration becomes awkward because `gui` requires concepts like tabs, windows, pixels, or graphical nodes, treat that as evidence that the `gui` abstraction is at the wrong level.

---

# 8. Current test-coverage observation

Source-tree inspection of the current `master` modules found no dedicated test source trees for:

- `ide`
- `ide-plp`
- `repl`

Their Gradle files may declare basic test dependencies, but there is no substantial behavioural suite comparable to the historical `feature/gui` tests.

This means migration cannot safely rely on existing automated regression coverage.

A core objective of this refactoring is therefore to **extract behaviour into testable common code before deleting the old implementation**.

---

# 9. Historical `feature/gui` branch: what to learn from it

The old branch is significantly behind `master` and must not be merged wholesale.

It is nevertheless useful architectural evidence.

## 9.1 Historical `gui` module

The old branch introduced a Kotlin Multiplatform `gui` module depending on common solver/parser/IO abstractions.

Its common source tree contained concepts such as:

```text
Application
Page
Event
FileContent
FileName
History
PageID
PageName
Runner
SolverEvent
SyntaxException
```

This establishes that an earlier attempt had already identified the need for a toolkit-neutral application/page layer.

## 9.2 Historical `Application`

The old application abstraction managed concepts such as:

- solver factory
- collection of pages
- current page
- application start
- application quit
- page creation
- page loading
- page selection/unselection
- application errors
- corresponding events

The basic decomposition between application/workspace and page/document remains useful.

## 9.3 Historical `Page`

The old `Page` abstraction contained much of the behaviour that is currently inside `TuPrologIDEModel`.

It included concepts such as:

- page identity
- timeout
- theory
- query
- stdin
- solve options
- solver builder/factory
- query history
- solve
- next
- stop
- reset
- save
- close
- rename
- state changes
- many corresponding events

Its state machine was again approximately:

```text
IDLE
COMPUTING
SOLUTION
```

The historical branch also scoped more behaviour per page than the current JavaFX IDE does.

This is worth reconsidering in the new design.

## 9.4 Historical `Runner`

The old branch had a custom execution abstraction separating callbacks approximately into:

- UI
- background
- I/O

This was a reasonable attempt to avoid hard-coding JavaFX threading.

Do not resurrect this abstraction as-is.

Use structured Kotlin coroutines and injected dispatch/execution policies instead.

The old `Runner` is useful only as evidence of the underlying requirement.

## 9.5 Historical custom event implementation

The branch had its own event/observable machinery.

Do not copy it unless a compelling reason appears.

Prefer maintained Kotlin Multiplatform coroutine flows.

Recommended mapping:

```text
persistent application state -> StateFlow
transient domain/application notifications -> SharedFlow
platform side effects -> SharedFlow<GuiEffect> or equivalent effect channel
```

## 9.6 Historical query `History`

The historical branch contained a query-history abstraction and tests.

Current `master` IDE does not expose an equally obvious query-history UI.

Treat history as a desirable reusable application feature rather than strict JavaFX parity.

It is particularly appropriate for:

- terminal up/down history
- graphical query history
- potentially browser persistence later

Keep it optional at the presentation level.

## 9.7 Historical common tests

The old branch had substantive common tests, including:

```text
EventsAsserter
Runner4Tests
TestApplication
TestHistory
TestPage
```

`EventsAsserter` validated ordered event streams/checkpoints.

`TestApplication` exercised behaviours such as:

- start
- initial empty pages
- page creation
- generated untitled names
- page selection/unselection
- loading
- missing-file/error workflows
- closing
- propagation of query/theory syntax errors

`TestPage` covered a substantial set of state/event interactions including:

- initial state
- theory/query changes
- swapping solver builders
- solve option changes
- one or multiple solution traces
- state changes
- background/UI execution ordering
- query history
- stop-related behaviour

### Recommendation

Port the **testing philosophy**, not the old implementation.

Create modern coroutine-based deterministic tests that assert ordered action/state/event/effect traces.

---

# 10. What should be preserved versus deliberately changed

The implementation agent should classify behaviour into the following categories.

## 10.1 Must-preserve functional capability

Unless a technical investigation proves otherwise, preserve:

- multiple theory documents in graphical IDEs
- create/open/select/close documents
- save/save-as/reload semantics
- Prolog source editing
- line/caret awareness where appropriate
- syntax highlighting
- dynamic operator-aware highlighting
- query editing
- solve
- solve all
- next solution
- consume remaining solutions
- timeout control
- reset
- solution rendering for Yes/No/Halt/timeout
- stdin configuration
- stdout
- stderr
- warnings
- operators inspection
- flags inspection
- libraries inspection
- static KB inspection
- dynamic KB inspection
- context-update notification semantics
- classic solver support
- custom/additional library support
- custom solver support
- PLP probabilistic solve mode
- PLP probabilities
- PLP BDD inspection
- BDD DOT text
- graphical BDD rendering in graphical PLP products
- BDD export/save where meaningful
- terminal interactive query loop
- terminal semicolon continuation
- terminal timeout
- terminal startup theory loading on supported filesystem targets
- terminal `solve` command
- terminal additional-library injection

## 10.2 Current implementation limitations that should normally be fixed

Do not preserve these merely for compatibility:

- model public API tied to `java.io.File`
- model public API tied to `ExecutorService`
- model public API tied to ReactFX
- controller directly constructing its model
- controller directly being a JavaFX `Initializable`
- JavaFX types in the extension API
- temporary physical files for untitled documents
- conflation of document dirty state and solver-cache invalidation
- inability to cancel an actively computing resolution
- absence of stale-resolution protection
- weak dirty-document close/reload handling
- platform-specific syntax exception carrying a JVM `File`
- JS REPL file operations left as TODOs
- lack of automated behaviour tests

## 10.3 Existing placeholders that are not strict parity requirements

Current source/UI contains concepts that appear stubbed or disabled:

- Open Recent
- Settings
- a full user-facing theme settings workflow

These can be implemented later, but should not block replacement parity unless the project owner explicitly promotes them to requirements.

## 10.4 Historical features that are sensible improvements

Consider adding/recovering:

- query history
- page-specific query, resolution, console, history, diagnostics, and solver-session state
- a strict distinction between persistent `Document` state and interactive `Page` state
- pure event-trace testing

These should be introduced intentionally and tested rather than smuggled in as incidental refactoring changes.

---

# 11. Recommended shared architecture

The shared architecture should be designed around a **workspace containing documents and interactive pages**. A page, not the whole application, is the primary unit of query execution.

This is the main semantic change from the current JavaFX IDE and supersedes any earlier idea of application-global query, solver, resolution, or console state.

## 11.1 Core terminology

Use the following concepts consistently throughout the implementation.

### `Document`

A `Document` represents editable source content and its persistence metadata.

It owns concepts such as:

```text
DocumentId
source text
display name
origin/backing reference
content revision
persisted revision
document-dirty state
```

A document **does not** own the current query, active resolution, solution history, or live solver instance.

### `Page`

A `Page` represents one interactive tuProlog working context shown by a frontend.

It owns or references:

```text
PageId
DocumentId? / page content reference
query
query history
page configuration
solver-session state
resolution state
solutions / resolution history
stdin/stdout/stderr/warnings
diagnostics
page-local feature state
```

Graphical frontends normally render pages as editor tabs. The terminal frontend may expose only one current page at a time, but it should still use the same page semantics internally where practical.

### `Workspace`

A `Workspace` contains documents, pages, workspace-level configuration, and the selected page.

The selected page is a **presentation/navigation fact**, not an implicit target for asynchronous operations.

### `SolverProfile`

A `SolverProfile` is declarative configuration/factory information describing how a solver session is created and what capabilities it provides.

### `SolverSession`

A `SolverSession` is a live page-scoped runtime instance containing solver state for one page/configuration/revision context.

A solver session may be rebuilt lazily when its document or effective configuration changes.

### `PageRuntime`

`PageRuntime` contains ephemeral implementation resources that must never leak into immutable public application state, for example:

```text
live MutableSolver / SolverSession object
resolution coroutine Job
worker/bridge handles
cancellation resources
platform execution resources
```

### `Feature`

A feature is a semantic application capability/presentation contribution such as standard solutions, KB inspection, or a PLP BDD view.

### `Renderer`

A renderer is frontend-specific code that visualises a semantic feature using Swing, Compose, Web, or terminal output.

## 11.2 Target aggregate model

The conceptual application state should look approximately like this:

```text
GuiApplication
│
└── WorkspaceState
     │
     ├── configuration
     │
     ├── documents: Map<DocumentId, DocumentState>
     │    ├── family.pl
     │    └── graph.pl
     │
     ├── pages: List<PageState>
     │    ├── Page A
     │    │    ├── documentId = family.pl
     │    │    ├── query = ancestor(X, Y)
     │    │    ├── resolution
     │    │    ├── history
     │    │    ├── console
     │    │    ├── diagnostics
     │    │    └── solverSessionState
     │    │
     │    ├── Page B
     │    │    ├── documentId = family.pl
     │    │    ├── query = parent(X, Y)
     │    │    └── independent solver/session state
     │    │
     │    └── Page C
     │         ├── documentId = graph.pl
     │         ├── query = path(a, b, P)
     │         └── independent solver/session state
     │
     └── selectedPageId

Internal runtime registry
│
└── PageId -> PageRuntime
     ├── SolverSession?
     ├── resolution Job?
     └── execution resources
```

Supporting multiple pages for one document is architecturally allowed even if the first graphical frontends initially create one page per opened document.

## 11.3 Presentation-independent application core

Even though the Gradle module is named `gui`, treat it as a **presentation-independent interactive application core**.

Recommended conceptual layers:

```text
Domain libraries
    tuProlog terms, theories, solvers, solve options

Application state
    immutable WorkspaceState / DocumentState / PageState

Application controller
    receives typed actions
    validates transitions
    coordinates page runtimes and solver sessions
    updates state
    emits semantic events and platform effects

Frontend adapter
    maps native input to actions
    renders immutable state
    executes effects
    maps semantic features to native renderers
```

## 11.4 Unidirectional interaction model

Use unidirectional data flow even if public naming remains MVC-flavoured:

```text
native user input
       |
       v
   GuiAction
       |
       v
 GuiController
    /      \
   v        v
state     GuiEffect ------------------+
   |                                  |
   v                                  v
GuiModel                         platform adapter
   |                                  |
   +-----------> frontend render      |
                                      |
GuiEvent <-----------------------------+
                 result action
```

Every asynchronous page action must carry the target `PageId` explicitly. Never resolve the target later by reading `selectedPageId`.

## 11.5 State, event, and effect are different concepts

Use three distinct categories.

### State

Durable facts that a newly attached view must know immediately, exposed through `StateFlow`.

Examples:

```text
page query text
page resolution status
open pages
selected page
current solutions
console history
current diagnostics
document dirty state
```

### Event

Transient semantic occurrences useful to observers, logging, analytics, or extensions.

Examples:

```text
ResolutionStarted
SolutionProduced
ResolutionCompleted
DocumentSaved
PageOpened
ActionRejected
```

### Effect

A request that must be executed by the hosting environment/front end.

Examples:

```text
RequestOpenDocument
RequestSaveDestination
ConfirmDiscardChanges
CopyTextToClipboard
OpenExternalLink
RequestApplicationExit
```

Do not encode effects as domain events and do not use events as the sole storage for durable state.

---

# 12. Proposed `gui` package structure

A concrete starting layout could be:

```text
gui/src/commonMain/kotlin/it/unibo/tuprolog/ui/gui/
    application/
        GuiApplication.kt
        GuiConfiguration.kt
        ApplicationMetadata.kt

    model/
        GuiModel.kt
        GuiState.kt
        WorkspaceState.kt
        WorkspaceConfiguration.kt
        ApplicationState.kt

    document/
        DocumentId.kt
        DocumentState.kt
        DocumentOrigin.kt
        DocumentReference.kt
        DocumentRevision.kt
        DocumentSnapshot.kt

    page/
        PageId.kt
        PageState.kt
        PageContent.kt
        PageConfiguration.kt
        PageHistoryState.kt
        QueryState.kt
        ResolutionState.kt
        ConsoleState.kt
        DiagnosticState.kt
        PageFeatureState.kt

    runtime/
        PageRuntime.kt
        PageRuntimeRegistry.kt

    controller/
        GuiController.kt
        DefaultGuiController.kt
        GuiAction.kt
        ApplicationAction.kt
        DocumentAction.kt
        PageAction.kt
        GuiEffect.kt

    solver/
        SolverProfile.kt
        SolverProfileId.kt
        SolverCapabilities.kt
        SolverSession.kt
        SolverSessionId.kt
        SolverSessionState.kt
        SolverSessionFactory.kt
        SolverSnapshot.kt
        ResolutionSessionId.kt
        ResolutionRequest.kt
        ResolutionHandle.kt
        SolverExecution.kt
        ResolutionSchedulingPolicy.kt

    event/
        GuiEvent.kt

    extension/
        GuiExtension.kt
        ExtensionId.kt
        GuiContributions.kt
        FeatureId.kt
        PageFeature.kt
        FeaturePlacement.kt
        SemanticRegion.kt
        CommandDescriptor.kt
        ExtensionActionHandler.kt

    presentation/
        SolutionPresentation.kt
        WarningPresentation.kt
        SemanticToken.kt
        SemanticCategory.kt
        TextRange.kt
        SyntaxHighlightingService.kt

    history/
        History.kt

    diagnostic/
        Diagnostic.kt
        DiagnosticSeverity.kt
        DiagnosticSource.kt

    persistence/
        WorkspaceSnapshot.kt
        WorkspacePersistence.kt

    error/
        GuiException.kt
```

This is a proposed structure, not an immutable naming requirement. Preserve responsibility boundaries and dependency direction even if names evolve.

`PageRuntime` and live solver objects should remain internal unless a concrete need for a public abstraction is demonstrated.

---

# 13. Shared observable state

## 13.1 Model API

Recommended minimal shape:

```kotlin
interface GuiModel {
    val state: StateFlow<GuiState>
    val events: SharedFlow<GuiEvent>
    val effects: SharedFlow<GuiEffect>
}
```

`effects` may alternatively live on the controller/application. The architectural requirement is the conceptual distinction, not that exact property placement.

## 13.2 Why use coroutines

Use `kotlinx.coroutines` because it is:

- Kotlin Multiplatform
- actively maintained
- already present in the repository
- suitable for structured cancellation
- testable with `kotlinx-coroutines-test`
- supported on JVM and JS

This replaces both ReactFX and public/ad-hoc executor exposure.

## 13.3 Single-writer state discipline

State should be changed through one controlled controller/reducer path rather than mutated arbitrarily from solver callbacks or worker threads.

Frontends receive immutable snapshots and cannot mutate application state directly.

## 13.4 Keep event-library coupling local

If future replacement of coroutines-based streams is a concern, isolate construction/collection helpers inside the shared event/state infrastructure.

Do not build a broad custom observable framework merely to avoid a standard KMP dependency.

---

# 14. Proposed state model

## 14.1 Top-level state

Prefer a small top-level state:

```kotlin
data class GuiState(
    val workspace: WorkspaceState,
    val application: ApplicationState,
)
```

Do **not** place `activeSolver`, global `resolution`, global `query`, global `console`, or global inspector state here.

Those are page/session concerns.

## 14.2 Workspace state

Conceptually:

```kotlin
data class WorkspaceState(
    val documents: Map<DocumentId, DocumentState>,
    val pages: List<PageState>,
    val selectedPageId: PageId?,
    val configuration: WorkspaceConfiguration,
)
```

Invariant:

```text
selectedPageId == null
OR
selectedPageId references an existing PageState
```

## 14.3 Document state

`DocumentState` should represent source/persistence state only.

At minimum:

```text
DocumentId
display name
text
origin/backing reference
current revision
persisted revision
isDocumentDirty derived from revisions
```

Do not use physical filesystem path as `DocumentId`.

Do not put page query, solver, current solutions, or console state in `DocumentState`.

## 14.4 Document revisions and solver freshness

Use monotonically changing document revisions so each page/session can determine whether its solver reflects the current source.

Example:

```text
Document revision = 12
Page A solver loaded revision = 12 -> fresh
Page B solver loaded revision = 10 -> stale
```

This is distinct from persistence dirty state:

```text
current revision != persisted revision -> document dirty
loaded solver revision != current revision -> solver session stale
```

Do not conflate the two.

## 14.5 Page state

Conceptually:

```kotlin
data class PageState(
    val id: PageId,
    val documentId: DocumentId?,
    val content: PageContent,
    val query: QueryState,
    val configuration: PageConfiguration,
    val solverSession: SolverSessionState,
    val resolution: ResolutionState,
    val history: PageHistoryState,
    val console: ConsoleState,
    val diagnostics: DiagnosticState,
    val features: Map<FeatureId, PageFeatureState>,
)
```

The exact split can evolve, but query/resolution/session state must remain page-scoped.

## 14.6 Query bar is page-specific: hard requirement

The query bar of every graphical frontend must be logically owned by the active page.

Switching from page A to page B must atomically present B's:

```text
query text
query history/current history position
current resolution/solutions
console streams
diagnostics
solver/context inspection state
page-level solver/options configuration
```

Switching back to A restores A's state.

This prevents the dangerous current behaviour where a global query can silently run against a different theory after a tab switch.

The first UI may visually keep the query bar in a fixed location below the editor tabs. It is still **semantically page-local**; its bound state changes with `selectedPageId`.

## 14.7 Document and page are deliberately distinct

A page may refer to a document, but they are not the same entity.

This allows:

- untitled/scratch pages
- future generated/read-only pages
- future multiple views/sessions over one document
- closing a page independently of deleting a document object where appropriate
- workspace/session restoration without relying on filesystem identity

The first release may enforce a convenient one-open-page-per-document policy in graphical frontends, but the shared model should not encode that as a fundamental invariant unless there is a strong reason.

## 14.8 Page-local history

Distinguish at least:

```text
query editing history/navigation
query run history
current resolution
past resolution summaries/results, if retained
```

A useful future-friendly run record is:

```text
QueryRun
    query
    effective solver profile/configuration
    solve options
    termination reason
    solutions or summary
```

Full persistent history need not be implemented immediately, but the model should not force a single global history.

## 14.9 Page-local console and diagnostics

`stdin`, `stdout`, `stderr`, and warnings belong to the page/solver session that produced or consumes them.

A frontend may additionally present a merged workspace console, but that is a derived view and not the source of truth.

Diagnostics are also page-scoped, with optional references to source document/ranges.

## 14.10 Page-local inspector state

Operators, flags, libraries, static KB, and dynamic KB snapshots are properties of a page's solver session.

Associate them with a solver-session/revision identity so stale inspector results cannot be presented as current.

## 14.11 Hierarchical configuration

Support configuration inheritance conceptually:

```text
Application defaults
       |
       v
Workspace configuration
       |
       v
Page overrides
```

A page can normally inherit the default classic solver and timeout, while a specialised page may override:

```text
solver profile
timeout
solve options
libraries/solver options supported by the profile
```

The API should distinguish inherited/default values from explicit overrides where that matters for persistence and UI.

## 14.12 Runtime state is not model state

Do not put coroutine jobs, live solver objects, worker handles, or platform resources in `PageState`.

Keep them in an internal runtime registry:

```text
PageId -> PageRuntime
```

This makes observable state deterministic, serializable where useful, and easy to test.

## 14.13 Workspace persistence

Design state so a `WorkspaceSnapshot` can eventually restore useful session information:

```text
open documents/references
untitled content where policy allows
open pages
selected page
page queries
page solver profile/options
query history, if persisted
layout/preferences if kept outside core domain state
```

Desktop can persist this to a file/config location; Web can use browser storage. Persistence itself remains an adapter/service concern.

## 14.14 Dirty-state categories

Do not use one generic `dirty` flag for unrelated concepts.

Distinguish at least:

```text
document dirty
    current source revision differs from persisted revision

solver session stale
    loaded document/config revision differs from effective page state

workspace/preferences dirty
    restorable workspace metadata changed, if workspace persistence is implemented
```

---

# 15. Controller, commands, and actions

## 15.1 Controller API

Recommended shape:

```kotlin
interface GuiController {
    val model: GuiModel
    suspend fun dispatch(action: GuiAction)
}
```

## 15.2 Separate application, document, and page actions

Use explicit action scopes:

```kotlin
sealed interface GuiAction

sealed interface ApplicationAction : GuiAction

sealed interface DocumentAction : GuiAction {
    val documentId: DocumentId
}

sealed interface PageAction : GuiAction {
    val pageId: PageId
}
```

Possible application/workspace actions:

```text
ApplicationStarted
ApplicationQuitRequested
ApplicationQuitConfirmed
NewPage
OpenDocumentRequested
DocumentOpened
CreatePageForDocument
SelectPage
ClosePageRequested
ClosePageConfirmed
RestoreWorkspace
```

Possible document actions:

```text
ChangeDocumentText(documentId, ...)
CloseDocumentRequested(documentId)
SaveDocumentRequested(documentId)
SaveDocumentAsRequested(documentId)
DocumentSaved(documentId, ...)
ReloadDocumentRequested(documentId)
```

Possible page actions:

```text
ChangeQuery(pageId, ...)
SelectPreviousQuery(pageId)
SelectNextQuery(pageId)
ChangeStdin(pageId, ...)
ChangeTimeout(pageId, ...)
ChangeSolveOptions(pageId, ...)
ChangeSolverProfile(pageId, ...)
Solve(pageId)
SolveAll(pageId)
NextSolution(pageId)
NextAllSolutions(pageId)
StopResolution(pageId)
ResetSolver(pageId)
MarkFeatureSeen(pageId, featureId)
PageExtensionAction(pageId, ...)
```

Source and persistence mutations should use `DocumentAction` with explicit `DocumentId`. Solver/query/session operations use `PageAction` with explicit `PageId`. If two pages reference the same document, a document edit must update the shared document source while leaving each page's query/session state independent and marking/revising affected solver sessions appropriately.

## 15.3 Never retarget asynchronous work through `selectedPageId`

This is a correctness requirement.

Bad design:

```kotlin
controller.solve() // internally reads selected page later
```

Required design:

```kotlin
controller.dispatch(PageAction.Solve(pageId))
```

Scenario that must work correctly:

```text
user starts Solve on Page A
Page A computation continues asynchronously
user selects Page B
Page A solution arrives
solution updates Page A only
Page B remains untouched
```

`selectedPageId` is used by frontends to decide what to render, not to identify ownership of asynchronous results.

## 15.4 Commands and command availability

Avoid storing redundant mutable booleans such as `canSolve`, `canNext`, and `canStop` if they are derivable from page state and capabilities.

Prefer a derived model such as:

```text
PageState + SolverCapabilities -> AvailableCommands
```

This reduces impossible combinations such as `resolution = Idle` while `canStop = true`.

Frontend menus/buttons bind to derived command availability.

## 15.5 Editor-local operations

Not every low-level editor operation must be a shared action.

Local operations such as selection movement or native undo/redo may stay inside an editor implementation as long as source changes are propagated to the shared document model and shared semantics do not depend on hidden editor state.

---

# 16. Platform effects and persistence ports

A common controller must not directly invoke Swing `JFileChooser`, browser `File`, Compose dialogs, terminal prompts, clipboard APIs, or external browser/window APIs.

## 16.1 Effect model

Possible effects:

```text
RequestOpenDocument
RequestSaveDestination(documentId)
ConfirmDiscardDocumentChanges(documentId)
ConfirmClosePage(pageId)
ConfirmQuitWithDirtyDocuments(...)
ShowError(...)
ShowInformation(...)
CopyTextToClipboard(...)
OpenExternalLink(...)
RequestApplicationExit
```

A frontend executes the effect and dispatches a typed result action.

Example:

```text
ApplicationAction.OpenDocumentRequested
    -> GuiEffect.RequestOpenDocument

Swing adapter
    -> JFileChooser
    -> read selected content
    -> ApplicationAction.DocumentOpened(...)

Web adapter
    -> browser picker/File API
    -> read selected content
    -> ApplicationAction.DocumentOpened(...)
```

## 16.2 Persistence abstraction

If reusable direct persistence is useful, use narrow common ports with opaque references:

```kotlin
interface DocumentPersistence {
    suspend fun load(reference: DocumentReference): DocumentSnapshot
    suspend fun save(reference: DocumentReference, text: String)
}
```

Do not leak platform path/object types.

For Web, save-as may legitimately be an effect that produces/downloads a Blob rather than a persistent path.

## 16.3 Closing pages versus closing documents

Define the lifecycle explicitly.

At minimum support:

```text
close page
    dispose page runtime/session
    preserve/save/discard referenced document according to policy

close document
    first resolve any pages referencing it
    then resolve dirty persistence state
```

If the first implementation enforces one page per document, this is simpler, but keep IDs and state concepts separate.

---

# 17. Page-scoped resolution state machine

Each page owns an independent resolution state machine.

Suggested states:

```text
Idle
Running
AwaitingContinuation
Completed
Cancelled
Failed
```

Each active resolution has a unique `ResolutionSessionId` and is associated with exactly one `PageId` and one effective solver-session revision.

## 17.1 Required transitions

At minimum test:

```text
Page Idle -> Solve -> Running
Running -> solution -> AwaitingContinuation
AwaitingContinuation -> Next -> Running
Running -> exhaustion -> Completed/Idle
Running -> Stop -> Cancelled/Idle
AwaitingContinuation -> Stop -> Cancelled/Idle
active -> Reset -> Idle with invalidated/rebuilt solver session
```

Invalid transitions should be explicitly defined:

- reject with typed `GuiEvent.ActionRejected`
- harmless no-op where documented
- throw only for programmer errors, not ordinary user actions

## 17.2 Resolution ownership

Every async solver result carries or is checked against:

```text
PageId
ResolutionSessionId
SolverSessionId / effective revision
```

A stale result from an old page/session must be discarded.

## 17.3 Cross-page concurrency

The shared architecture must allow this:

```text
Page A -> SolverSession A -> Resolution A
Page B -> SolverSession B -> Resolution B
```

It must not encode one global application resolution as a fundamental invariant.

A frontend may initially choose to restrict concurrent runs for UX/resource reasons, but that is a scheduling policy rather than the shared state model.

## 17.4 Resolution scheduling policy

If product policy needs to restrict concurrency, isolate it behind a concept such as:

```kotlin
interface ResolutionSchedulingPolicy {
    fun mayStart(pageId: PageId, state: GuiState): Boolean
}
```

Possible policies include:

```text
one active resolution per page        // mandatory baseline
one active resolution per workspace   // optional frontend/product policy
bounded N concurrent resolutions      // future
unbounded by core, constrained by runtime resources
```

---

# 18. Solver execution, sessions, and cancellation

## 18.1 Remove public executors

Do not expose `ExecutorService` or `ForkJoinPool`.

Use coroutines internally and execution ports where blocking/platform isolation is required.

## 18.2 Inject execution policy

Tests must be deterministic.

Inject scopes/contexts/dispatchers or a higher-level execution service instead of hard-coding `Dispatchers.Default` throughout application logic.

## 18.3 Explicit `SolverSession`

Introduce a page-oriented solver session abstraction conceptually like:

```kotlin
interface SolverSession {
    val id: SolverSessionId
    val state: StateFlow<SolverSessionState>

    suspend fun start(request: ResolutionRequest): ResolutionHandle
    suspend fun reset()
    suspend fun close()
}
```

Exact methods should follow current tuProlog solver semantics, but the abstraction should own:

```text
live solver
loaded source/configuration revisions
operators/libraries/flags/KB snapshots
active resolution integration
cleanup
```

## 18.4 Lazy solver-session creation and rebuild

Do not eagerly instantiate one expensive solver for every open page.

Preferred lifecycle:

```text
open page
    -> no live solver required

edit source/query
    -> still no solver required

Solve
    -> create SolverSession lazily
    -> load effective document/configuration
    -> solve

edit source or change relevant configuration
    -> mark page solver session stale

next Solve / explicit Reset
    -> rebuild or incrementally refresh according to proven solver semantics
```

This is especially useful for many tabs, PLP, Web Workers, and future expensive profiles.

## 18.5 Blocking solver behaviour

Investigate whether solver iteration can block synchronously.

This matters for:

- Stop semantics on JVM
- cross-page concurrency
- browser responsiveness
- Web Worker requirements
- cancellation correctness

Do not assume coroutine cancellation can interrupt arbitrary synchronous code.

## 18.6 `SolverExecution` port

Where needed, isolate runtime execution:

```kotlin
interface SolverExecution {
    suspend fun start(
        pageId: PageId,
        session: SolverSession,
        request: ResolutionRequest,
    ): ResolutionHandle
}
```

A JVM implementation may isolate blocking work on worker threads; a browser implementation may bridge to a Web Worker.

## 18.7 Cancellation correctness

Cancellation must:

- invalidate the current `ResolutionSessionId`
- cancel/close the resolution handle where supported
- cancel the coroutine job
- attempt solver/platform cleanup
- prevent stale late writes
- transition only the owning page state

---

# 19. Solver profiles, hierarchical configuration, and capabilities

## 19.1 `SolverProfile`

Introduce a cross-platform profile/factory abstraction, conceptually:

```kotlin
interface SolverProfile {
    val id: SolverProfileId
    val displayName: String
    val capabilities: SolverCapabilities
    val defaultSolveOptions: SolveOptions

    fun create(context: SolverCreationContext): MutableSolver
}
```

The exact solver type should match current common solver APIs.

## 19.2 No hidden default solver in `gui`

The base `gui` module should not depend on `solve-classic` merely to have a default profile.

Concrete products choose profiles in their composition roots.

## 19.3 Page-effective solver configuration

Compute an effective configuration from:

```text
application defaults
+ workspace defaults
+ page overrides
```

Changing the effective solver/profile/configuration of page A must not implicitly reset page B.

The UI may initially expose only global defaults plus a page-level selector/options dialog, but the core should support page overrides.

## 19.4 Preserve current IDE/REPL library-default differences

Current behaviour differs:

```text
IDE
    IOLib + OOPLib by default

REPL
    IOLib by default
    OOPLib only with --oop
```

Make these explicit product/profile defaults rather than flattening them accidentally.

## 19.5 Capabilities are backend facts

Candidate capabilities:

```text
supportsStaticKbInspection
supportsDynamicKbInspection
supportsOperatorsInspection
supportsFlagsInspection
supportsLibrariesInspection
supportsProbabilisticSolutions
supportsBdd
supportsMutableKnowledgeBase
supportsInteractiveInput
supportsCancellation
supportsOop
```

Do not mechanically mirror every solver method. Add capabilities when application features need to make meaningful availability decisions.

## 19.6 Capabilities are not features

Keep these concepts separate:

```text
Solver capability
    backend can produce/perform something

Feature
    application exposes a semantic user-facing function

Renderer
    frontend-specific visual/terminal implementation
```

Example:

```text
PLP solver -> supportsBdd
                  |
                  v
             plp.bdd feature
              /     |      \
          Swing  Compose   Web / textual REPL
```

Avoid type tests such as `if (solver is ProbLogSolver)` in frontend/controller logic when a capability/feature contract is sufficient.

---

# 20. Extension architecture

The extension system must support PLP and future solver/tool variants without reintroducing toolkit coupling.

## 20.1 Prefer declarative contributions over imperative mutation

Avoid making the primary API an unrestricted:

```kotlin
extension.install(controller)
```

that mutates global internals in order-dependent ways.

Prefer something conceptually like:

```kotlin
interface GuiExtension {
    val id: ExtensionId
    fun contributions(): GuiContributions
}
```

with:

```kotlin
data class GuiContributions(
    val solverProfiles: List<SolverProfile> = emptyList(),
    val pageFeatures: List<PageFeature> = emptyList(),
    val commands: List<CommandDescriptor> = emptyList(),
    val reducersOrHandlers: List<ExtensionActionHandler> = emptyList(),
)
```

Exact types should remain type-safe and minimal.

## 20.2 Semantic feature IDs

Define stable semantic identifiers, for example:

```text
standard.solutions
standard.stdout
standard.stderr
standard.warnings
standard.operators
standard.flags
standard.libraries
standard.static-kb
standard.dynamic-kb
plp.solution-details
plp.bdd
```

These are application concepts, not widget IDs.

## 20.3 Semantic placement regions

Use semantic placement intent rather than tab indices or toolkit containers.

Suggested regions:

```text
EDITOR
QUERY
RESULTS
INSPECTOR
CONSOLE
SIDEBAR
STATUS
AUXILIARY
COMMAND_ONLY
```

A contribution may include priority/order metadata:

```text
FeaturePlacement(region = RESULTS, priority = 100)
```

Each frontend translates this into its own layout.

## 20.4 Frontend renderer registry

Each frontend owns:

```text
FeatureId -> native renderer
```

For example:

```text
plp.bdd
    Swing -> SwingBddRenderer
    Compose -> ComposeBddRenderer
    Web -> WebBddRenderer
    REPL -> TextBddRenderer
```

No Swing/Compose/DOM type crosses into `gui` or `gui-plp`.

## 20.5 Feature replacement and decoration

PLP currently replaces the normal Solutions tab/presentation.

Support this deliberately through semantic policies such as:

```text
add feature
replace semantic feature
decorate/enrich semantic feature
```

Conflicts and ordering must be deterministic and tested.

## 20.6 Page-scoped extension state

Extension state that reflects a particular solve/session belongs to the owning page, for example:

```text
PLP probability metadata
BDD availability/current graph
specialised inspector state
```

Changing or resetting one page must not clear extension state in unrelated pages.

## 20.7 Explicit composition over global discovery

Use explicit application composition, not JVM `ServiceLoader`, for the cross-platform extension mechanism.

Conceptually:

```kotlin
GuiConfiguration {
    solverProfile(classicProfile)
    extension(StandardFeatures())
    extension(...)
}
```

The configuration determines what is available; page state determines what is currently active/effective.

---

# 21. `gui-plp`

Introduce `gui-plp` even though it is an additional module beyond the initially listed set.

Without it, PLP application semantics would be duplicated across:

- `ide-plp-swing`
- `ide-plp-compose`
- `ide-plp-web`
- `repl-plp`

That would defeat the desired extension architecture.

## 21.1 Responsibilities

`gui-plp` should own cross-platform semantics such as:

- probabilistic solver profile or profile contribution
- default probabilistic solve options
- page-scoped probability extraction/presentation state
- page-scoped PLP-specific solution metadata
- page-scoped BDD semantic data
- BDD DOT representation where domain APIs support it
- PLP-specific actions
- semantic feature contributions

It must not own:

- Swing components
- Compose components
- DOM nodes
- JavaFX classes
- Graphviz Java UI objects

## 21.2 PLP solution metadata

Provide common state suitable for all frontends:

```text
standard solution presentation
+ probability
+ optional BDD information
```

## 21.3 BDD representation

Prefer an intermediate common representation that can at least expose DOT text.

DOT is already the current interoperability boundary for graphical rendering and is suitable for:

- copy/export
- terminal display
- browser graph rendering
- JVM Graphviz rendering

---

# 22. `ide-swing`

Implement Swing first.

## 22.1 Why Swing first

Swing is the lowest-risk JVM-native replacement because:

- it ships with the JVM/JDK ecosystem
- no JavaFX runtime/module packaging is required
- it can consume all existing JVM tuProlog artifacts
- it gives a concrete parity target before Compose/Web introduce additional platform variables
- it provides an immediate path to permanent JavaFX removal

## 22.2 Responsibilities

`ide-swing` should contain only desktop/Swing concerns:

- application/window bootstrap
- Swing layout
- Swing editor integration
- file chooser
- clipboard
- confirmations/dialogs
- state collection -> EDT rendering
- user interaction -> `GuiAction`
- standard feature renderers
- platform persistence effects

## 22.3 Do not leak EDT into `gui`

The Swing adapter is responsible for ensuring native widget mutation occurs on the Swing Event Dispatch Thread.

The shared controller must not know the EDT exists.

## 22.4 Suggested UI mapping

Possible Swing equivalents:

```text
JFrame
    application shell

JMenuBar
    File/Edit/Help

JTabbedPane
    open pages (normally one page per opened document in the initial Swing UI)

JTextPane or appropriate Swing text component
    theory editor

JTextField/JTextPane
    query editor bound to the selected PageState; visually fixed placement is acceptable

JTabbedPane
    solutions/output/inspector panels

JTable
    operators/flags

JTree
    libraries

JTextArea/JTextPane
    KB/output panels

JFileChooser
    file effects

JOptionPane/custom dialogs
    confirmations/errors/about
```

## 22.5 Editor choice

Start with Swing-native components unless a third-party editor dependency gives a large, justified improvement.

If using a third-party Swing editor, keep it behind a small adapter so it cannot affect `gui`.

Required editor capabilities include:

- source editing
- line/caret tracking
- syntax highlighting
- reasonable undo/redo
- clipboard operations

Line-number rendering may be implemented with a row-header component if the chosen editor lacks it.

## 22.6 Swing parity milestone

Before proceeding to JavaFX deletion, Swing must successfully cover the standard IDE conformance scenarios described later.

---

# 23. `ide-plp-swing`

This should be a thin extension module.

It should contain approximately:

- Swing application composition/bootstrap
- registration of `gui-plp`
- PLP Swing solution renderer/decorator
- Swing BDD/DOT renderer and related dialogs

It should **not** duplicate PLP solver configuration/business logic already present in `gui-plp`.

A useful architectural test is:

> If all Swing renderer classes are deleted from `ide-plp-swing`, almost nothing should remain except composition/bootstrap code.

---

# 24. `ide-compose`

## 24.1 Primary target

Implement the canonical Compose IDE first for Compose Desktop/JVM on:

- Windows
- macOS
- Linux

Compose Multiplatform desktop is currently the mature/stable portion of the Compose target set and is well suited to replacing JavaFX for a modern cross-platform desktop UI.

## 24.2 Responsibilities

Exactly like Swing, Compose should consume the shared application API.

Do not create a Compose-specific duplicate model/controller.

Map:

```text
GuiState -> composables
selected PageState -> page-specific editor/query/results/inspectors
UI intents -> GuiAction carrying PageId where page-scoped
GuiEffect -> Compose/platform service
```

## 24.3 Editor

Compose text editing should preserve the same semantic editor features as Swing where practical.

If the standard Compose text-field APIs are insufficient for a full code-editor experience, use a dedicated editor abstraction/component but keep all technology-specific code within `ide-compose`.

The shared syntax-highlighting service can provide spans/classes.

## 24.4 Desktop native packaging caveat

The requested release model asks for one self-contained `ide-compose` executable/artifact.

Compose Desktop relies on platform-native runtime components such as Skiko. A single genuinely portable fat JAR across all desktop OSes may or may not be reliable depending on packaging/native-loading strategy.

Therefore create an early packaging spike.

Decision order:

1. attempt one portable JVM fat JAR containing all required native variants and smoke-test it on Windows/macOS/Linux
2. if reliable, use it as the requested single artifact
3. if not reliable, document the technical reason and produce per-OS self-contained Compose distributions as the explicit exception

Do not label a Linux-only Compose package as multiplatform.

---

# 25. `ide-plp-compose`

This module should contain:

- Compose PLP feature renderers
- probability decorations
- Compose BDD/DOT presentation
- graph-rendering adapter
- minimal bootstrap/composition

All solver/application semantics belong in `gui-plp`.

---

# 26. `ide-web`

## 26.1 Recommendation: make it mandatory

Do not treat `ide-web` as optional if the requirement remains:

> the browser IDE must use tuProlog libraries compiled for JS and run as a serverless static application

Current Compose Multiplatform Web strategy is primarily Kotlin/Wasm-oriented for shared Compose UI. A dedicated Kotlin/JS frontend is therefore the cleanest way to satisfy the strict JS-library requirement.

If the requirement later changes to merely “runs in the browser” and tuProlog gains appropriate Wasm targets, sharing Compose UI with Web can be reconsidered.

## 26.2 Core requirements

`ide-web` must:

- compile against JS variants of tuProlog modules
- run fully client-side
- require no backend solver service
- require no JVM
- be deployable to arbitrary static hosting
- support multiple page contexts with page-specific query/session state
- support the standard IDE solve workflow
- preserve background-page result isolation
- produce a ZIP containing the static website distribution

## 26.3 Web UI technology

Keep `gui` independent from the web framework.

Reasonable implementation paths include:

- Kotlin/JS DOM APIs
- Compose HTML/DOM-based Kotlin/JS APIs
- another actively maintained Kotlin/JS web framework after evaluation

Do not choose a framework merely because its name contains Compose. The architectural contract is toolkit independence.

## 26.4 Code editor

For the browser, it is acceptable and likely preferable to integrate a mature JavaScript code editor through a thin wrapper, for example a currently maintained CodeMirror- or Monaco-class editor.

The wrapper should expose only the small operations needed by the frontend:

- set/get text
- change callback
- selection/caret
- decorations/highlighting
- undo/redo
- focus

Do not expose the JS editor type to `gui`.

## 26.5 Browser file semantics

The browser cannot guarantee ordinary filesystem paths.

Implement semantic equivalents:

```text
Open
    browser File API / picker

Save / Save As
    downloadable Blob or supported browser file-system API

persistent path-like editing
    optional enhancement when File System Access API is supported
```

Always retain a generic standards-compatible fallback.

## 26.6 Static distribution

The release artifact should be conceptually:

```text
2p-ide-web-<version>.zip
```

containing everything necessary to serve the site from a static HTTP server/CDN.

No backend configuration should be required.

---

# 27. Browser responsiveness and workers

This requires an explicit technical spike before declaring Web complete.

A solver running synchronously on the JavaScript main thread may freeze:

- rendering
- editor input
- Stop
- progress indication

## 27.1 Required experiment

Benchmark representative queries in the browser and determine whether solver execution yields sufficiently.

## 27.2 If main-thread execution blocks

Move resolution work behind a Web Worker adapter.

The shared architecture should make this possible through `SolverExecution`.

A worker implementation may need message-friendly DTOs for:

- theory text
- query text
- solve options
- solver profile identifier/configuration
- solutions
- context snapshots
- warnings/errors
- cancellation

Do not serialise arbitrary mutable solver objects across worker boundaries.

## 27.3 Stop requirement

The browser UI must remain responsive enough to request cancellation while a long resolution is active.

This is a functional requirement, not merely a performance optimisation.

---

# 28. `ide-plp-web`

The PLP Web variant should be another thin extension product.

It should provide:

- PLP solver profile/extension from `gui-plp`
- probability display
- BDD availability and details
- DOT view/copy
- browser graph rendering
- graph/image export where browser APIs permit

Use a maintained browser-side DOT/Graphviz renderer behind an adapter.

Do not import JVM Graphviz Java into common or JS code.

---

# 29. Refactoring `repl` onto the common application core

## 29.1 Overall recommendation

Yes, refactor `repl` to reuse `gui` where this means sharing application semantics.

Do not force it to imitate a graphical tabbed application.

## 29.2 Shared concepts

The REPL should reuse at least:

- solver profile/configuration machinery
- theory/document/page creation semantics where applicable
- one explicit current PageId for interactive commands where the terminal UX remains single-context
- page-scoped query parsing/state
- timeout representation
- resolution/solver-session lifecycle
- solution continuation
- solution presentation semantics
- warnings/errors
- reset/cancellation
- extension semantics

## 29.3 Terminal adapter

Conceptually:

```text
terminal input / Clikt command
       |
       v
 GuiAction (with current PageId for page operations)
       |
       v
 GuiController
       |
       v
GuiState + GuiEvent
       |
       v
terminal renderer
```

Interactive continuation:

```text
user enters ';'
    -> PageAction.NextSolution(currentPageId)
```

EOF/quit becomes an application lifecycle action.

## 29.4 Keep CLI syntax stable

Preserve established command-line syntax unless there is a clear reason to change it.

In particular keep or compatibly alias:

```text
-T / --theory
-t / --timeout
--oop
solve
-n / --numberOfSolutions
```

Add new solver/profile options only in a backwards-compatible manner.

## 29.5 File handling on JVM and JS/Node

If `repl` continues to publish JS/Node executables, finish the current missing JS file operations using Node-compatible filesystem APIs behind a platform implementation.

If JS REPL distribution is not actually a supported product, make that explicit in build/publication configuration instead of keeping incomplete targets.

---

# 30. `repl-plp`

Create `repl-plp` as a thin extension of `repl` plus `gui-plp`.

Expected behaviour:

- same base CLI interaction and explicit current-page/session ownership
- probabilistic solver profile selected/configured
- probability displayed for successful probabilistic solutions
- BDD availability represented textually
- DOT graph may be printed/exported via a suitable command/flag where useful

Avoid duplicating normal REPL parsing, continuation, timeout, warning, or theory-loading logic.

---

# 31. Functional parity and deliberate-improvement matrix

The following matrix is a minimum target. Rows marked **improve** are deliberate changes from current JavaFX behaviour and must be tested as product semantics rather than treated as optional polish.

| Capability | Current IDE | Swing | Compose | Web | REPL | REPL-PLP |
|---|---:|---:|---:|---:|---:|---:|
| New document/page | yes | yes | yes | yes | equivalent | equivalent |
| Open theory/document | yes | yes | yes | yes, browser semantics | `-T` / command semantics | same |
| Multiple open documents | yes | yes | yes | yes | not required | not required |
| Distinct `Document` and `Page` identities | no | **required** | **required** | **required** | internal where practical | same |
| Page-specific query | no, global | **improve/required** | **improve/required** | **improve/required** | one/current page semantics | same |
| Switching page restores its query/results/session state | no | **required** | **required** | **required** | N/A/current page | same |
| Multiple pages may reference one document | no | architecture permits; UI optional | same | same | not required | not required |
| Page-local solver profile/options | no | supported | supported | supported | supported/configurable | supported |
| Independent page solver sessions | no | **required core semantics** | **required** | **required** | current-page equivalent | same |
| Cross-page concurrent resolution | no | core permits; UI policy may restrict | same | same | not required | not required |
| Save | yes | yes | yes | browser equivalent | file-oriented equivalent if exposed | same |
| Save As | yes | yes | yes | download/picker equivalent | not required as interactive editor feature | not required |
| Reload/Revert | yes | yes | yes | where meaningful | not required | not required |
| Dirty-state protection | weak | **improve** | **improve** | **improve** | N/A | N/A |
| Distinguish document-dirty from solver-stale | no | **required** | **required** | **required** | shared core | shared core |
| Source editor | yes | yes | yes | yes | terminal input/file | terminal input/file |
| Line numbers | yes | yes | yes | yes if editor supports | N/A | N/A |
| Caret position | yes | yes | yes | yes | N/A | N/A |
| Syntax highlighting | yes | yes | yes | yes | N/A | N/A |
| Dynamic operator highlighting | yes | yes | yes | yes | parser uses operators | parser uses operators |
| Shared semantic token/diagnostic model | no | **improve** | **improve** | **improve** | **improve** | **improve** |
| Undo/redo | yes | yes | yes | yes | terminal/history semantics | same |
| Clipboard operations | yes | yes | yes | yes | terminal-native | terminal-native |
| Query editor | yes | page-local | page-local | page-local | yes | yes |
| Query history | historical branch | page-local | page-local | page-local | supported/reused where practical | same |
| Resolution/solution history | minimal/current only | page-local model permits | same | same | optional textual | same |
| Solve | yes | yes | yes | yes | yes | yes |
| Solve all | yes | yes | yes | yes | via solution count/loop | yes |
| Next solution | yes | yes | yes | yes | `;` | `;` |
| Stop after yielded solution | yes | yes | yes | yes | yes/equivalent | yes |
| Interrupt active computation | effectively no | **required where possible** | **required where possible** | **required** | **required where possible** | same |
| Reset solver | yes | per page | per page | per page | current-page/session equivalent | same |
| Timeout | yes | page/default configuration | same | same | yes | yes |
| Stdin configuration | yes | page-local | page-local | page-local | native stdin | native stdin |
| Stdout/stderr/warnings | yes | page-local, merge view optional | same | same | terminal streams | terminal streams |
| Operators inspector | yes | page session | page session | page session | textual/command optional | same |
| Flags inspector | yes | page session | page session | page session | textual/command optional | same |
| Libraries inspector | yes | page session | page session | page session | current aliases, expandable | same |
| Static KB inspector | yes | page session | page session | page session | textual/command optional | same |
| Dynamic KB inspector | yes | page session | page session | page session | textual/command optional | same |
| Tab/page unread markers | yes | equivalent | equivalent | equivalent | N/A | N/A |
| Yes/No/Halt presentation | yes | yes | yes | yes | yes | yes |
| Logic stack information | yes | yes | yes | yes | yes | yes |
| Custom libraries | yes | yes | yes | yes where available | yes | yes |
| Alternate solver injection | partial hook | yes | yes | yes | yes | yes |
| Semantic extensions | JavaFX-specific | yes | yes | yes | yes | yes |
| Declarative extension contributions | no | **required** | **required** | **required** | **required** | **required** |
| PLP probability | PLP IDE | PLP variant | PLP variant | PLP variant | no | yes |
| PLP state isolated per page | no explicit model | **required** | **required** | **required** | N/A | current-page equivalent |
| BDD DOT view | PLP IDE | PLP variant | PLP variant | PLP variant | no | yes/text |
| BDD graphical render | PLP IDE | PLP variant | PLP variant | PLP variant | N/A | N/A |
| BDD save/export | PLP IDE | PLP variant | PLP variant | PLP variant | optional DOT | optional DOT |
| About/product info | yes | yes | yes | yes | `--version` | `--version` |
| Open Recent | disabled/stub | optional | optional | optional | N/A | N/A |
| Settings UI | disabled/stub | optional | optional | optional | CLI opts | CLI opts |

---

# 32. Shared solution presentation

Create a presentation-neutral representation so every frontend need not reverse-engineer `Solution` independently.

Possible structure:

```kotlin
sealed interface SolutionPresentation {
    data class Yes(
        val query: String,
        val assignments: List<AssignmentPresentation>,
        val metadata: Map<FeatureId, Any /* replace with safe typed mechanism */>,
    ) : SolutionPresentation

    data class No(
        val query: String,
    ) : SolutionPresentation

    data class Halt(
        val query: String,
        val message: String,
        val logicStackTrace: List<String>,
        val isTimeout: Boolean,
    ) : SolutionPresentation
}
```

Do not literally use `Any` for extension metadata in the final public API unless a type-safe strategy is designed. The snippet only illustrates the need for extensible metadata.

A better extension design may use typed feature-state registries keyed by `FeatureId` with frontend renderers supplied by the same extension.

---

# 33. Shared diagnostics

Create a common diagnostic model for theory/query errors.

Possible fields:

```text
kind: query | theory | runtime | warning
message
documentId?
pageId?
sourceDisplayName?
range or line/column?
clauseIndex?
logicStack?
underlying domain error metadata where safe
```

This lets:

- Swing show dialogs and highlight source locations
- Compose show banners/dialogs
- Web show inline messages
- REPL print precise diagnostics

Do not require platform files in the diagnostic object.

---

# 34. Testing strategy

Testing is a first-class deliverable. The page/document separation and page-specific query/session semantics must be proven in common tests before graphical implementation.

## 34.1 `gui/commonTest`

Use:

- `kotlin.test`
- `kotlinx-coroutines-test`
- deterministic dispatchers/execution fakes
- fake solver profiles and solver sessions
- fake/in-memory document persistence/effect responders
- test clocks only if timestamps are persisted
- state/event/effect trace recorders

## 34.2 Trace-based testing

Recover the useful idea behind historical `EventsAsserter`, but assert all three channels:

```text
Given immutable state
When typed actions are dispatched
Then state transitions + ordered events + effects match expectations
```

For page-specific operations every expectation should include the owning `PageId` where ambiguity is possible.

## 34.3 Document lifecycle tests

Cover:

- create untitled document
- deterministic display naming
- open persistent document
- change source text
- revision increments
- document dirty transitions
- save/save-as/failure
- reload/revert confirmation
- close clean document
- close dirty document: save/discard/cancel
- physical origin independent from `DocumentId`
- document can exist independently from page identity
- document revision and persisted revision remain separate

## 34.4 Page lifecycle and isolation tests

Cover:

- create page over a document
- create scratch/untitled page according to chosen model
- select page
- close page
- page runtime disposed on close
- selected-page fallback policy after close
- two pages can be represented independently even if UI initially does not expose duplicate views
- when two pages reference one document, a `DocumentAction` source edit is visible through both document views while page query/session state remains independent
- selected `PageId` always references an existing page or is null

Most importantly, test page isolation:

```text
Page A query = p(X)
Page B query = q(Y)
select A -> UI state exposes A query
select B -> UI state exposes B query
change B query -> A query unchanged
```

and similarly for:

- resolution state
- solutions/history
- console streams
- diagnostics
- solver profile/options
- extension state

## 34.5 Parsing, semantic token, and diagnostic tests

Cover:

- valid/invalid theory
- valid/invalid/empty query
- diagnostic line/column/range
- clause index where available
- custom operators affecting query parsing
- operator change invalidating semantic highlighting
- tokens/diagnostics reference document/page correctly

## 34.6 Solver-session lifecycle tests

Cover:

- no eager solver required on simple page creation
- first Solve lazily creates a session
- solver profile selection
- inherited versus page-overridden profile/options
- custom libraries
- stdin configuration
- reset invalidates/recreates only the target page session
- document edit marks sessions over that document stale
- one page may be stale while another session revision differs
- static/dynamic KB load
- context snapshot updates
- profile capability changes
- closing a page disposes its live runtime/session

## 34.7 Resolution tests

Cover per page:

- single `Yes`
- `No`
- multiple solutions
- Solve All
- Next / Next All
- timeout Halt
- non-timeout Halt
- warning/stdout/stderr during solve
- stop while awaiting continuation
- stop while actively computing
- cancellation race
- stale result after cancellation
- new query after cancellation
- reset during/after resolution

## 34.8 Cross-page concurrency and stale-result tests

These are mandatory because the architecture intentionally permits independent sessions.

At minimum:

```text
start resolution A on Page A
switch to Page B
start/change/interact with B
A result arrives
only A changes
```

Also test:

```text
A and B resolve concurrently where scheduling policy permits
cancelling A does not cancel B
resetting A does not reset B
changing A's solver profile does not affect B
late result from old A session cannot update new A session
late result from closed Page A is discarded
```

If a configured scheduling policy restricts concurrent pages, test policy rejection separately from core ownership correctness.

## 34.9 State-machine invariants

Add invariant/property-oriented tests:

```text
at most one active resolution per page solver session
an event/result owned by Page A cannot update Page B
Reset invalidates active results for its target page only
Stop eventually leaves Running where cancellation is supported
Next is invalid without a resumable resolution
changing source invalidates affected solver freshness
selectedPageId is null or references an existing page
DocumentId and PageId are not interchangeable identities
runtime resources never appear in immutable/public state
```

## 34.10 Extension tests

Create at least one synthetic extension independent of PLP.

Test:

- solver profile contribution
- declarative feature contribution
- semantic placement
- extension action handler
- feature replacement/decoration
- duplicate/conflicting feature policy
- renderer lookup contract
- page-scoped extension state isolation
- resetting/closing one page does not clear another page's extension state

## 34.11 `gui-plp` tests

Cover:

- probabilistic profile selection
- page-level PLP profile override
- probabilistic solve options
- probability extraction
- BDD presence/absence
- DOT representation
- normal solution compatibility
- PLP state cleared/recomputed only for the affected page
- two PLP pages do not leak probability/BDD state into each other

## 34.12 Swing tests

Do not repeat solver semantics exhaustively. Test adapter correctness:

- selected page binds the fixed-location query bar to that page
- switching tabs restores query/results/console/inspectors
- widget event dispatches action with the correct `PageId`
- late background-page results update unread markers but do not overwrite selected page controls
- state -> widgets
- feature renderer registration
- EDT confinement
- file/dialog effects
- smoke startup

## 34.13 Compose tests

Test equivalent adapter properties:

- page-scoped state collection
- correct `PageId` in actions
- page switching
- extension composition
- critical UI flows
- packaged startup on supported OSes

## 34.14 Web tests

Use browser automation against the produced static distribution.

At minimum:

- static app loads
- create/open multiple pages
- page-specific queries survive tab switching
- solving A then switching to B never overwrites B
- representative long solve keeps UI/cancellation usable
- browser file-open/save workflow where automatable
- PLP probability/BDD
- static artifact has no backend dependency

## 34.15 REPL tests

Cover:

- CLI option parsing
- startup theory loading
- malformed theory diagnostics
- multiline query
- one/multiple solutions and `;`
- EOF/user continuation decisions
- timeout
- warnings/stderr
- `--oop`
- custom libraries
- `solve` subcommand and `-n`
- empty query
- shared solution/diagnostic semantics
- PLP output

Where REPL exposes a single current page, verify its operations still go through explicit page/session ownership internally rather than a special global solver path.

---

# 35. Cross-frontend conformance scenarios

Create semantic acceptance scenarios. Core semantics are exercised once in shared tests; each frontend gets a thin integration equivalent.

## Scenario A — deterministic success

Theory:

```prolog
parent(alice, bob).
```

Query:

```prolog
parent(alice, X).
```

Expect one successful binding.

## Scenario B — multiple solutions

```prolog
p(1).
p(2).
p(3).
```

Query `p(X).`; exercise Solve + Next and Solve All.

## Scenario C — failure

Query with no solution and verify `No` presentation.

## Scenario D — theory syntax error

Verify range/source diagnostic.

## Scenario E — query syntax error

Verify page-specific query diagnostic.

## Scenario F — solver warning/output

Exercise output/warning channels where practical and verify ownership by page.

## Scenario G — mutable KB

Exercise dynamic KB update and page inspector refresh.

## Scenario H — timeout/cancellation

Use a deliberately long/non-terminating computation with bounded safeguards.

## Scenario I — custom operator

Verify parser and semantic-highlighting operator awareness.

## Scenario J — alternate solver/profile

Register a non-default/synthetic profile and ensure the base controller remains unchanged.

## Scenario K — extension feature

Install a synthetic feature and verify semantic state + renderer registration.

## Scenario L — PLP

Use a stable probabilistic example and verify probability, BDD availability, and DOT representation.

## Scenario M — page-specific query isolation

Create two pages over different theories:

```text
Page A: query p(X)
Page B: query q(Y)
```

Switch repeatedly and verify each frontend restores the correct query and solution state.

## Scenario N — background-page result

Start a solve on Page A, switch to Page B before A completes, then verify:

```text
A receives its result
B controls/query/results remain unchanged
A receives an unread/update indication where the frontend supports it
```

## Scenario O — independent solver configuration

Configure Page A with classic/default settings and Page B with a different profile/options. Verify resets, edits, and solve operations remain isolated.

## Scenario P — concurrent pages

Where the selected scheduling policy permits concurrency, run A and B simultaneously and verify cancellation/reset/result isolation. Where policy forbids it, verify a typed rejection rather than accidental global corruption.

---

# 36. Migration implementation phases

The migration should remain incremental and repository-buildable after each major phase. The page/document distinction and page-specific query/session model must be established before implementing the replacement frontends; do not defer them as a later UX refactor.

## Phase 0 — Baseline and inventory

### Tasks

- checkout current `master`
- record exact baseline commit SHA
- run relevant existing checks
- inventory JavaFX/ReactFX/RichTextFX usage
- inventory current executable entry points and packaging
- inspect `feature/gui` only for concepts/tests
- build a small theory/query fixture corpus
- record current IDE/PLP/REPL behaviour, including the current global-query limitation

### Suggested commands

```text
./gradlew :ide:check
./gradlew :ide-plp:check
./gradlew :repl:check
```

Also run relevant root checks if feasible.

### Exit criteria

- baseline documented
- pre-existing failures recorded separately
- parity versus deliberate-improvement list accepted

## Phase 1 — Create `gui` skeleton with final identity model

### Tasks

- add KMP `:gui`
- add `DocumentId` and `PageId` as distinct types
- add immutable `GuiState`, `WorkspaceState`, `DocumentState`, `PageState`
- add selected page state
- add action/event/effect infrastructure
- add internal page runtime registry abstraction
- add common tests proving document/page identity isolation

### Hard constraints

- no frontend/platform UI types in `commonMain`
- no application-global query/resolution/console model
- no use of `selectedPageId` as implicit async-operation target

### Exit criteria

`:gui:check` passes on configured targets and two independent pages can exist in common tests.

## Phase 2 — Document/page lifecycle and persistence semantics

### Tasks

- new/open/edit/save/reload/close document workflows
- create/select/close page workflows
- revisions and dirty-state categories
- platform effect protocol
- quit workflow
- optional workspace snapshot DTO skeleton
- deterministic trace-test utilities

### Exit criteria

Document and page lifecycle is fully testable without filesystem or graphical toolkit.

## Phase 3 — Page-specific query/history/diagnostics/presentation primitives

### Tasks

- page-local query state
- page-local query history
- shared text ranges/semantic tokens
- shared diagnostics
- page-local console state
- switching-page isolation tests

### Exit criteria

A test can edit/switch two pages repeatedly without any query/history/diagnostic/console leakage.

## Phase 4 — Solver profiles and page-scoped sessions

### Tasks

- `SolverProfile`
- hierarchical/effective page configuration
- capabilities
- lazy `SolverSession` creation
- solver-session revision/freshness tracking
- static/dynamic KB/context snapshots
- per-page reset
- shared solution presentation

### Exit criteria

Two pages can hold independent profile/session state and document edits invalidate only the appropriate session freshness.

## Phase 5 — Resolution, cancellation, and scheduling policy

### Tasks

- page-scoped resolution state machine
- unique resolution/session IDs
- Solve/SolveAll/Next/NextAll
- timeout
- stdout/stderr/warnings
- active cancellation
- stale-result protection
- `ResolutionSchedulingPolicy`
- concurrency/isolation tests

### Exit criteria

Background Page A results can arrive while Page B is selected without modifying B; cancellation/reset isolation is proven.

## Phase 6 — Declarative extension framework

### Tasks

- declarative `GuiContributions`
- solver-profile contributions
- semantic feature IDs
- semantic placement regions
- page-scoped extension state
- feature replacement/decorating policy
- renderer registration contract
- synthetic extension tests

### Exit criteria

A fake extension contributes behaviour and a page feature without `gui` seeing native renderer types or mutating unrelated pages.

## Phase 7 — Create `gui-plp`

### Tasks

- probabilistic profile/contribution
- probabilistic solve options
- probability presentation
- page-scoped BDD semantic state
- DOT representation
- isolation tests across multiple PLP/non-PLP pages

### Exit criteria

PLP behaviour is testable without JavaFX/Graphviz UI classes and does not leak between pages.

## Phase 8 — Implement `ide-swing`

### Tasks

- Swing shell and page/editor tabs
- fixed-location or page-contained query bar bound to selected page state
- menus/editor/actions
- page-specific solutions, console, diagnostics, inspectors
- background-page unread/update markers
- syntax highlighting adapter
- file/dialog/clipboard effects
- extension renderer registry
- fat-JAR packaging

### Exit criteria

- standard conformance scenarios including M/N/O pass
- switching pages restores independent query/session state
- no JavaFX dependency in `ide-swing`

## Phase 9 — Implement `ide-plp-swing`

Install `gui-plp`, PLP renderers, probability display, BDD/DOT/graph export. Keep composition thin.

## Phase 10 — Refactor `repl`

Preserve the established Clikt surface while delegating solver/session/solution/diagnostic semantics to the common page-oriented core. Resolve JS/Node file TODOs or explicitly revise supported targets.

## Phase 11 — Implement `repl-plp`

Add PLP profile/extension composition and terminal probability/BDD/DOT rendering without copying base REPL logic.

## Phase 12 — Implement `ide-compose`

Implement Compose Desktop against the same page/document/controller contracts. Do not create a Compose-specific application model.

## Phase 13 — Implement `ide-plp-compose`

Add only PLP composition/renderers.

## Phase 14 — Implement `ide-web`

### Tasks

- Kotlin/JS application
- page/tab editor model
- browser file effects
- page-specific query/result state
- responsiveness/Web Worker spike
- browser E2E scenarios including background-page result isolation
- static ZIP packaging

### Exit criteria

Unpacked static site runs without backend and preserves independent page state while solving entirely client-side.

## Phase 15 — Implement `ide-plp-web`

Add PLP composition, probability, BDD/DOT browser renderer/export.

## Phase 16 — Remove legacy JavaFX modules

Only after replacement parity remove:

```text
:ide
:ide-plp
```

Also remove JavaFX/ReactFX/RichTextFX dependencies, FXML/resources, JavaFX-only build helpers, and obsolete docs.

### Verification

Search source and dependency graphs. No legacy GUI technology may remain in migrated tool architecture.

## Phase 17 — Release and CI migration

Update product distributions, explicit artifact manifest, OS/browser smoke tests, and release workflow as described below.

---

# 37. Current release context

The current release configuration is strongly JAR-oriented.

At source-inspection time:

- semantic release gathers GitHub release assets through patterns focusing on `*redist*.jar`
- the release workflow invokes an aggregate shadow/fat-JAR build task before semantic release
- this is adequate for JVM tools but insufficient for static web ZIPs and potentially Compose native/self-contained distributions

The migration therefore needs an explicit distribution contract.

---

# 38. Target release artifacts

Use explicit, stable artifact names.

A suggested logical naming scheme is:

```text
2p-ide-swing-<version>-redist.jar
2p-ide-compose-<version>-redist.jar
2p-ide-web-<version>.zip
2p-repl-<version>-redist.jar

2p-ide-plp-swing-<version>-redist.jar
2p-ide-plp-compose-<version>-redist.jar
2p-ide-plp-web-<version>.zip
2p-repl-plp-<version>-redist.jar
```

For Compose, revise the naming if platform-specific packages prove technically necessary, for example:

```text
2p-ide-compose-<version>-windows.zip
2p-ide-compose-<version>-macos.zip
2p-ide-compose-<version>-linux.zip
```

but only after the single-fat-JAR feasibility spike.

---

# 39. Gradle distribution tasks

Create a root aggregate task such as:

```text
assembleToolDistributions
```

It should depend on explicit tasks for every end-user artifact.

Possible logical tasks:

```text
:ide-swing:shadowJar / fatJar equivalent
:ide-plp-swing:shadowJar / fatJar equivalent
:repl:shadowJar / fatJar equivalent
:repl-plp:shadowJar / fatJar equivalent

:ide-compose:packageDistribution
:ide-plp-compose:packageDistribution

:ide-web:productionExecutableCompileSync or dedicated dist task
:ide-web:zipDistribution
:ide-plp-web:zipDistribution
```

Use the actual Kotlin/Compose plugin task names appropriate to the final build rather than hard-coding this sketch.

The aggregate task should fail if any expected distribution is missing.

---

# 40. Release artifact manifest

Generate or validate an explicit manifest during CI.

For example:

```text
expected:
  ide-swing
  ide-compose
  ide-web
  repl
  ide-plp-swing
  ide-plp-compose
  ide-plp-web
  repl-plp
```

CI should compare expected products with built artifacts.

This is safer than a wildcard that silently publishes only whatever happened to be built.

---

# 41. Artifact smoke testing

Test final distributions, not just source projects.

## 41.1 JVM tools

Where practical, support non-interactive commands such as:

```text
--version
--self-check
```

Then CI can run:

```text
java -jar 2p-ide-swing-...jar --version
java -jar 2p-repl-...jar --version
java -jar 2p-repl-plp-...jar --version
```

For graphical products, a `--self-check` can initialise critical dependencies without opening a long-lived UI process.

## 41.2 Compose

Smoke-test the packaged distribution on:

- Windows
- macOS
- Linux

This is especially important if a universal JAR approach is attempted.

## 41.3 Web

CI should:

```text
unzip distribution
start temporary static server
open in headless browser
enter a trivial theory/query
solve
assert result
```

Repeat a PLP smoke case for `ide-plp-web`.

---

# 42. CI changes

The repository already has broad JVM and JS CI matrices, which are a useful base.

Extend CI conceptually into:

```text
common-core checks
    :gui:check
    :gui-plp:check

JVM checks
    :ide-swing:check
    :ide-plp-swing:check
    :repl:check
    :repl-plp:check

Compose desktop
    Linux build/test
    Windows build/smoke
    macOS build/smoke

JS/browser
    :ide-web checks
    :ide-plp-web checks
    browser E2E

distribution
    assembleToolDistributions
    validate artifact manifest
    smoke-test artifacts

release
    publish libraries
    attach explicit tool distributions
```

Do not require full graphical E2E on every JDK/OS combination if that makes CI unnecessarily expensive. Separate broad library testing from targeted UI/distribution smoke matrices.

---

# 43. Publication policy

Distinguish reusable libraries from applications.

## Publishable libraries

At minimum consider publishing:

```text
:gui
:gui-plp
```

according to normal KMP publication conventions.

## End-user applications

Primarily publish release distributions for:

```text
ide-swing
ide-compose
ide-web
repl
ide-plp-swing
ide-plp-compose
ide-plp-web
repl-plp
```

The web IDE does not need to become an npm library simply because Kotlin/JS produces JavaScript.

---

# 44. Dependency rules to enforce

Treat the following as architectural tests/code-review rules.

```text
:gui MUST NOT depend on :ide-swing
:gui MUST NOT depend on :ide-compose
:gui MUST NOT depend on :ide-web
:gui MUST NOT depend on :repl

:gui MUST NOT depend on solve-classic merely to provide a default solver

:gui-plp MAY depend on :gui and PLP solver/domain modules

:ide-swing depends on :gui and chooses classic solver configuration
:ide-compose depends on :gui and chooses classic solver configuration
:ide-web depends on :gui and chooses a JS-compatible classic solver configuration
:repl depends on :gui and chooses its CLI classic profile

:ide-plp-swing depends on :ide-swing interfaces/utilities where appropriate + :gui-plp
:ide-plp-compose depends on :ide-compose utilities + :gui-plp
:ide-plp-web depends on :ide-web utilities + :gui-plp
:repl-plp depends on :repl utilities + :gui-plp
```

Be careful about Gradle module dependencies between executable modules. If sharing frontend-specific infrastructure between base and PLP products becomes awkward, extract small frontend support modules rather than creating cyclic/overly broad application dependencies.

For example, if necessary:

```text
:ide-swing-core
:ide-swing
:ide-plp-swing
```

may be cleaner than making one executable application module a reusable library. Do this only if build structure justifies it; do not proliferate modules prematurely.

---

# 45. Composition-root pattern

Every executable should explicitly assemble its application.

Conceptual classic Swing example:

```kotlin
fun main() {
    SwingIdeApplication(
        GuiConfiguration {
            solverProfile(classicIdeProfile())
            extension(StandardGuiFeatures())
        },
    ).run()
}
```

Conceptual PLP Swing example:

```kotlin
fun main() {
    SwingIdeApplication(
        GuiConfiguration {
            solverProfile(probabilisticProfile())
            extension(StandardGuiFeatures())
            extension(PlpGuiExtension())
        },
        renderers = swingRenderers + plpSwingRenderers,
    ).run()
}
```

Conceptual REPL example:

```kotlin
fun main(args: Array<String>) {
    ReplApplication(
        configurationFactory = { cli ->
            GuiConfiguration {
                solverProfile(classicReplProfile(oop = cli.oop))
            }
        },
    ).main(args)
}
```

Exact APIs may differ, but explicit composition is preferred over hidden globals/service discovery.

---

# 46. Potential public APIs

The following sketches express intended ownership and dependency boundaries, not final signatures.

## 46.1 Model/controller

```kotlin
interface GuiModel {
    val state: StateFlow<GuiState>
    val events: SharedFlow<GuiEvent>
    val effects: SharedFlow<GuiEffect>
}

interface GuiController {
    val model: GuiModel
    suspend fun dispatch(action: GuiAction)
}
```

## 46.2 Distinct IDs

```kotlin
@JvmInline
value class DocumentId(val value: String)

@JvmInline
value class PageId(val value: String)
```

Use KMP-compatible value-class details according to repository conventions. The important rule is that the two identities are not interchangeable.

## 46.3 Workspace/page state sketch

```kotlin
data class WorkspaceState(
    val documents: Map<DocumentId, DocumentState>,
    val pages: List<PageState>,
    val selectedPageId: PageId?,
)

data class PageState(
    val id: PageId,
    val documentId: DocumentId?,
    val query: QueryState,
    val configuration: PageConfiguration,
    val solverSession: SolverSessionState,
    val resolution: ResolutionState,
    val history: PageHistoryState,
    val console: ConsoleState,
    val diagnostics: DiagnosticState,
    val features: Map<FeatureId, PageFeatureState>,
)
```

## 46.4 Scoped actions

```kotlin
sealed interface DocumentAction : GuiAction {
    val documentId: DocumentId
}

sealed interface PageAction : GuiAction {
    val pageId: PageId
}

data class ChangeQuery(
    override val pageId: PageId,
    val text: String,
) : PageAction

data class Solve(
    override val pageId: PageId,
) : PageAction
```

This explicit ownership pattern must extend to every mutation: source/persistence actions identify `DocumentId`; query/solver/session actions identify `PageId`.

## 46.5 Solver profile/session

```kotlin
interface SolverProfile {
    val id: SolverProfileId
    val displayName: String
    val capabilities: SolverCapabilities
    val defaultSolveOptions: SolveOptions

    fun create(context: SolverCreationContext): MutableSolver
}

interface SolverSession {
    val id: SolverSessionId
    val state: StateFlow<SolverSessionState>
    suspend fun start(request: ResolutionRequest): ResolutionHandle
    suspend fun reset()
    suspend fun close()
}
```

Exact tuProlog solver types must be verified for common-source compatibility.

## 46.6 Declarative extension

```kotlin
interface GuiExtension {
    val id: ExtensionId
    fun contributions(): GuiContributions
}
```

Avoid unrestricted frontend/controller mutation as the default extension API.

## 46.7 Semantic tokens and diagnostics

```kotlin
data class TextRange(
    val start: Int,
    val endExclusive: Int,
)

data class SemanticToken(
    val range: TextRange,
    val category: SemanticCategory,
)

data class Diagnostic(
    val severity: DiagnosticSeverity,
    val message: String,
    val documentId: DocumentId?,
    val pageId: PageId?,
    val range: TextRange?,
    val source: DiagnosticSource,
)
```

Prefer an existing lower-level common source-coordinate abstraction from parser infrastructure if it already fits; do not duplicate coordinate types gratuitously.

---

# 47. Concurrency correctness requirements

The current JavaFX code relies on an executor plus `Platform.runLater` and does not model page/session ownership strongly enough. The new architecture must establish the following guarantees.

## 47.1 Single-writer state discipline

State mutations occur through a controlled controller/reducer execution path, not arbitrarily from worker callbacks.

## 47.2 Explicit ownership tuple

Every asynchronous resolution/result is associated with at least:

```text
PageId
SolverSessionId
ResolutionSessionId
```

Before applying a solution, completion, warning, inspector snapshot, or failure, verify that the owning page/runtime still exists and the IDs/revisions are current.

## 47.3 Selection independence

Changing `selectedPageId` must never retarget or cancel background work implicitly.

Selection affects presentation only unless the user invokes an explicit policy/action.

## 47.4 Page-local cancellation

Cancelling Page A must:

- invalidate A's resolution ID
- cancel A's coroutine/handle
- attempt A's solver cleanup
- prevent A's late writes
- update A only
- leave Page B untouched

## 47.5 Concurrent pages

The core must remain correct with two active page resolutions even if a product-level scheduling policy normally disallows that state.

This keeps the model future-proof and makes ownership bugs easier to detect.

## 47.6 Page closure during execution

Closing a page with active work must either:

- cancel/dispose its runtime before removing it from state, or
- mark it closing and discard every later result until cleanup completes

No callback may recreate/update a closed page.

## 47.7 Frontend isolation

Frontends receive immutable state and cannot hold mutable references to live solver/runtime objects.

---

# 48. Browser-specific architectural constraints

Because `ide-web` is JS-only/serverless:

- no JVM filesystem
- no JVM threads
- no JavaFX/Swing
- no Java Graphviz
- no backend process
- no hidden HTTP solver service

All required solver libraries must have compatible JS artifacts.

Before implementation, verify each dependency in the intended Web configuration has a JS target.

If some optional library does not, expose the resulting capability difference explicitly rather than breaking the entire build.

---

# 49. Compose versus dedicated Web frontend

As of this plan, Compose Multiplatform supports desktop robustly, while its shared browser UI story is centred on Kotlin/Wasm and remains less mature than desktop.

The requested Web product specifically asks to rely on JS-compiled tuProlog libraries.

Therefore use:

```text
ide-compose
    Compose Desktop first

ide-web
    dedicated Kotlin/JS browser frontend
```

Possible future evolution:

```text
if tuProlog gains suitable Wasm targets
and Compose Web reaches acceptable maturity
and the strict JS-only condition is relaxed
    -> investigate shared Compose desktop/browser UI
```

Do not make that future possibility block the present architecture.

---

# 50. Risks and decisions that need explicit verification during implementation

## 50.1 Solver cancellability

Risk: current solver iteration may synchronously block and ignore coroutine cancellation.

Mitigation:

- inspect solver APIs
- write cancellation tests
- isolate execution
- use JVM worker thread and browser Web Worker where necessary

## 50.2 Compose universal fat JAR

Risk: native Skiko components make a universal single artifact unreliable.

Mitigation:

- early packaging spike
- cross-OS smoke test
- documented per-OS exception if unavoidable

## 50.3 Browser graph rendering

Risk: Graphviz Java cannot be reused.

Mitigation:

- common DOT representation
- browser-specific maintained Graphviz/DOT renderer

## 50.4 Browser filesystem semantics

Risk: direct equivalence with desktop paths is impossible.

Mitigation:

- semantic document IDs
- browser pick/download effects
- optional modern File System Access enhancement

## 50.5 Editor parity

Risk: RichTextFX currently gives code-editor conveniences that plain Swing/Compose fields may lack.

Mitigation:

- define semantic editor requirements first
- use adapters
- choose frontend-native/editor-specific implementation per target
- centralise syntax classification

## 50.6 Solver-context snapshot cost

Risk: repeatedly copying large static/dynamic theories into immutable UI state may be expensive.

Mitigation:

- use revisioned immutable references where safe
- lazily format expensive theory text
- do not recompute inspector presentation when revision is unchanged

## 50.7 Extension API overengineering

Risk: designing a fully generic plugin platform before PLP needs are understood.

Mitigation:

- use current standard IDE + PLP as the first two concrete consumers
- add one synthetic test extension
- support only demonstrated extension dimensions initially
- keep interfaces evolvable

## 50.8 Exact UI parity versus better semantics

Risk: copying implementation accidents such as weak dirty-state handling, application-global query state, or one-global-solver assumptions.

Mitigation:

- maintain this parity/limitation distinction
- make semantic improvements explicit in commits and tests

---

# 51. Suggested AI-agent work rules

Give the coding agent these rules verbatim or nearly verbatim.

1. Start from current `master`; record the exact baseline commit
2. Inspect `feature/gui` only for concepts/tests; do not merge or cherry-pick it wholesale
3. Keep the existing JavaFX IDE working as a behavioural oracle until replacement parity is demonstrated
4. Treat the new `gui` as an application core, not a widget library
5. Make `DocumentId` and `PageId` distinct from the first implementation commit
6. A document owns source/persistence state; a page owns query, resolution, console, diagnostics, history, solver-session state, and page feature state
7. The query bar is page-specific even if rendered in a fixed visual location outside the tab content
8. Never use selection as an implicit mutation target: source/persistence actions carry `DocumentId`, solver/query/session actions carry `PageId`
9. Do not create application-global query, resolution, console, inspector, or live solver state
10. Permit independent page solver sessions in the shared architecture; any cross-page concurrency restriction is a scheduling policy
11. Keep live solvers, coroutine jobs, worker handles, and other runtime resources outside immutable public state
12. Keep `gui/commonMain` free from frontend/platform UI types
13. Do not expose `java.io.File`, executors, ReactFX, JavaFX, Swing, Compose, or DOM types through shared APIs
14. Prefer `StateFlow` for durable state and typed `SharedFlow` for events/effects
15. Use structured concurrency and explicit page/solver/resolution session IDs
16. Do not assume coroutine cancellation interrupts solver calls; verify it
17. Create solver sessions lazily where practical and track loaded document/configuration revisions
18. Never hard-code classic solver creation into the shared controller
19. Support application/workspace defaults plus page-level solver/options overrides
20. Preserve current IDE and REPL differences in default libraries unless deliberately changed
21. Separate solver capabilities from semantic GUI features
22. Prefer declarative extension contributions over arbitrary mutation of controller internals
23. Keep extension state page-scoped when it describes a page/session result
24. Keep native renderers outside common extensions
25. Add `gui-plp` so PLP semantics are not repeated across products
26. Keep every `*-plp` executable thin
27. Build common page/document/session semantics and tests before replacement frontends
28. Make Swing the first production replacement frontend
29. Refactor REPL onto shared application/session semantics without forcing graphical concepts into the CLI
30. Build Compose Desktop against exactly the same shared state/controller model
31. Implement a dedicated Kotlin/JS Web IDE while the JS-only browser requirement remains
32. Test browser responsiveness and use a Web Worker if solving blocks the main thread
33. Treat document dirty state, solver freshness, and workspace persistence dirty state as separate concepts
34. Preserve operator-aware parsing and centralise semantic token classification where feasible
35. Make diagnostics source-range aware and reusable by graphical IDEs, REPL, and LSP-oriented tooling where compatible
36. Preserve solver-context inspection functionality as page-session state
37. Preserve PLP probability and BDD workflows with per-page isolation
38. Add deterministic page-isolation and stale-result tests before deleting JavaFX code
39. Smoke-test built distribution artifacts themselves
40. Replace wildcard-only release collection with an explicit product manifest
41. Remove JavaFX/ReactFX/RichTextFX only after replacement frontends are proven
42. Keep the repository buildable at each planned milestone.

---

# 52. Suggested commit/PR boundaries

Avoid one enormous rewrite commit. A reasonable sequence is:

```text
1. Add KMP gui skeleton with distinct Document/Page identities
2. Add document and page lifecycle/effect semantics
3. Add page-specific query/history/diagnostics/semantic tokens
4. Add solver profiles, hierarchical page configuration, and lazy page sessions
5. Add page-scoped resolution/cancellation/scheduling semantics
6. Add declarative extension API and synthetic extension tests
7. Add common PLP gui extension with page-isolation tests
8. Add Swing IDE
9. Add Swing PLP IDE
10. Refactor REPL onto shared application core
11. Add PLP REPL
12. Add Compose IDE
13. Add Compose PLP IDE
14. Add Kotlin/JS Web IDE
15. Add PLP Web IDE
16. Add explicit tool distributions and smoke tests
17. Remove JavaFX IDE/PLP modules
18. Remove JavaFX/ReactFX/RichTextFX dependencies and obsolete build helpers
19. Update documentation and release workflows
```

Each PR should include tests for the semantics it introduces. Page-specific query/session behaviour must exist before the first replacement graphical IDE is considered feature-complete.

---

# 53. Definition of done

The migration is complete only when all of the following are true.

## Architecture

- `gui` is Kotlin Multiplatform
- common model/controller compile without any graphical framework
- `DocumentId` and `PageId` are distinct concepts
- documents own source/persistence state, not query/session state
- every graphical page owns its query, resolution, history, console, diagnostics, solver-session state, and page feature state
- switching pages restores that page's query/results/session-visible state
- no application-global query/resolution/live-solver assumption remains
- source/persistence mutations use explicit `DocumentId`; solver/query/session mutations use explicit `PageId`
- shared state is immutable from the frontend perspective
- runtime solver/jobs/workers are kept outside observable immutable state
- solver creation is injected
- page-effective configuration can inherit defaults and override solver/options where supported
- solver capabilities are distinct from semantic features
- solver sessions may be created/rebuilt lazily
- cancellation/session lifecycle and stale-result protection are explicit
- core semantics remain correct for independent/concurrent page resolutions even if a frontend policy restricts concurrency
- document identity is independent from filesystem path
- document dirty state is independent from solver freshness
- extension contributions are toolkit-neutral and preferably declarative
- page-scoped extension state is isolated
- PLP common behaviour exists in one shared extension module

## Swing

- standard IDE functionality works
- page-specific query/session state is visible and correctly restored on tab switching
- background-page results cannot overwrite selected-page controls
- produces self-contained JVM artifact
- PLP Swing variant works

## Compose

- functional equivalent of standard IDE on supported desktop platforms
- uses the same page/document/controller model as Swing
- packaging strategy explicitly verified
- PLP Compose variant works

## Web

- Kotlin/JS-only tuProlog dependency path verified
- static site needs no backend
- page-specific query/session behaviour works
- long solve does not make cancellation unusable
- background-page solve isolation works
- PLP Web variant works
- static ZIP artifact released

## REPL

- established CLI preserved or compatibly evolved
- shared page/session solver semantics reused
- tests cover continuation and solve subcommand
- JS/Node file support completed or target support explicitly changed
- PLP REPL exists as an extension, not a copy

## Functional parity and deliberate improvements

- editor/query/solution workflow retained
- global JavaFX query semantics replaced by page-specific query/session semantics
- stdout/stderr/warnings retained and correctly attributed
- stdin semantics retained or improved
- timeout retained
- solver inspectors retained and page-session scoped
- dynamic operator awareness retained
- shared source diagnostics/semantic token model implemented where feasible
- PLP probability retained
- BDD DOT/render/export retained where appropriate

## Testing

- substantial `gui/commonTest` suite exists
- deterministic state/event/effect traces exist
- document/page identity and lifecycle tests exist
- page switching/query isolation tests exist
- background-page result isolation tests exist
- cancellation/stale-result races tested
- cross-page concurrency or scheduling-policy tests exist
- extension isolation tests exist
- PLP common multi-page isolation tests exist
- frontend adapter tests exist
- browser E2E exists
- release artifacts are smoke-tested

## Cleanup

- old `ide` removed
- old `ide-plp` removed
- no JavaFX imports/dependencies remain
- no ReactFX imports/dependencies remain
- no RichTextFX imports/dependencies remain
- obsolete FXML removed
- obsolete JavaFX packaging removed

## Release

- explicit artifacts generated for every tool
- release fails when an expected artifact is absent
- static Web ZIPs attached to releases
- Compose artifact strategy validated on all claimed OSes

---

# 54. Recommended validation commands at the end

Use the actual module/task names established during implementation.

At minimum run subproject checks such as:

```text
./gradlew :gui:check
./gradlew :gui-plp:check
./gradlew :ide-swing:check
./gradlew :ide-plp-swing:check
./gradlew :ide-compose:check
./gradlew :ide-plp-compose:check
./gradlew :ide-web:check
./gradlew :ide-plp-web:check
./gradlew :repl:check
./gradlew :repl-plp:check
```

Then run:

```text
./gradlew check
./gradlew assembleToolDistributions
```

Run formatting/lint tasks according to repository conventions.

Finally inspect dependencies/source for forbidden legacy technologies.

---

# 55. Suggested manual parity checklist before deleting JavaFX

Run the old JavaFX IDE and new Swing IDE side-by-side if the environment permits.

For each item below verify user-visible behaviour rather than visual identity.

## Document/page workflow

- launch with editable document/page
- create another document/page
- switch page tabs
- open `.pl`
- edit
- save
- save-as
- reload
- close
- dirty-close confirmation in new IDE

## Editing

- syntax colouring
- comments
- strings
- numbers
- variables
- functors
- parentheses/brackets/braces
- custom operator highlighting
- line/caret reporting
- undo/redo
- clipboard

## Query workflow

- create/open at least two pages with different queries
- switching pages restores each page's query
- switching pages restores each page's current solutions/console/inspector state
- run a slow query on page A, switch to B, and verify A's result cannot overwrite B
- reset/change solver options on A and verify B is unaffected
- valid query
- empty query
- malformed query
- single solution
- multiple solutions
- Next
- Solve All
- All next
- failure
- timeout
- halt
- reset
- Stop/cancel

## Streams

- stdin configuration
- stdout
- stderr
- warning
- unread markers

## Context

- operators
- flags
- libraries
- static KB
- dynamic KB
- updates after solver operations

## PLP

- probabilistic query
- probability display
- BDD availability
- DOT text
- copy DOT
- render graph
- save/export graph

After Swing parity is established, repeat the same semantic checklist against Compose and Web.

---

# 56. Documentation changes

Update project documentation to reflect the new product split.

At minimum document:

- what `gui` is and is not
- how to create a custom solver profile
- how to create a semantic GUI extension
- how to provide a Swing renderer
- how to provide a Compose renderer
- how to provide a Web renderer
- how to extend REPL output/commands where supported
- how PLP uses the extension mechanism as a worked example
- how to run each tool
- how to build release artifacts
- browser static-hosting instructions
- supported target matrix

PLP should become the canonical extension example in developer documentation.

---

# 57. Architecture rationale in one sentence

The intended migration is not:

```text
JavaFX -> Swing + Compose
```

It is:

```text
JavaFX-coupled application with global query/session semantics
    -> toolkit-neutral tested workspace/document/page application core
       -> page-scoped solver sessions and queries
       -> Swing adapter
       -> Compose adapter
       -> Kotlin/JS browser adapter
       -> terminal adapter
       -> reusable semantic extensions such as PLP
```

That distinction should guide every implementation decision.

---

# 58. Source-evidence appendix

The following source files/pages were inspected when preparing this plan. These are references for the implementation agent and should be re-checked against the exact baseline commit before coding because `master` may evolve.

## Current `master`

Repository:

- https://github.com/tuProlog/2p-kt

Gradle/module configuration:

- https://raw.githubusercontent.com/tuProlog/2p-kt/master/settings.gradle.kts
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/gradle/libs.versions.toml
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/build.gradle.kts
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/gradle.properties
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide-plp/build.gradle.kts
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide-plp/gradle.properties
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/repl/build.gradle.kts
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/repl/gradle.properties

Core IDE model/controller:

- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/TuPrologIDEModel.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/TuPrologIDEModelImpl.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/TuPrologIDEController.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/TuPrologIDEBuilder.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/TuPrologIDEApplication.kt

IDE views/helpers:

- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/FileTabView.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/SyntaxColoring.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/SolutionView.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/LibraryView.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/SolverEvent.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/SyntaxException.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/CustomTab.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/kotlin/it/unibo/tuprolog/ui/gui/ModelConfigurator.kt

Main JavaFX view:

- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide/src/main/resources/it/unibo/tuprolog/ui/gui/TuPrologIDEView.fxml

PLP IDE:

- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide-plp/src/main/kotlin/it/unibo/tuprolog/ui/gui/PLPIDEApplication.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide-plp/src/main/kotlin/it/unibo/tuprolog/ui/gui/PLPSolutionView.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide-plp/src/main/kotlin/it/unibo/tuprolog/ui/gui/GraphRenderView.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/ide-plp/src/main/kotlin/it/unibo/tuprolog/ui/gui/GraphvizRenderer.kt

REPL:

- https://raw.githubusercontent.com/tuProlog/2p-kt/master/repl/src/commonMain/kotlin/it/unibo/tuprolog/ui/repl/AbstractTuPrologCommand.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/repl/src/commonMain/kotlin/it/unibo/tuprolog/ui/repl/TuPrologCmd.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/repl/src/commonMain/kotlin/it/unibo/tuprolog/ui/repl/TuPrologSolveQuery.kt
- inspect the corresponding `PlatformSpecificUtils` expect/actual implementations under `commonMain`, `jvmMain`, and `jsMain`

Release/CI:

- https://raw.githubusercontent.com/tuProlog/2p-kt/master/release.config.js
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/.github/workflows/build-and-deploy.yml
- https://raw.githubusercontent.com/tuProlog/2p-kt/master/.github/workflows/test-extensively.yml

## Historical `feature/gui`

Use only as conceptual/test reference:

- https://raw.githubusercontent.com/tuProlog/2p-kt/feature/gui/gui/build.gradle.kts
- https://raw.githubusercontent.com/tuProlog/2p-kt/feature/gui/gui/src/commonMain/kotlin/it/unibo/tuprolog/ui/gui/Application.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/feature/gui/gui/src/commonMain/kotlin/it/unibo/tuprolog/ui/gui/Page.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/feature/gui/gui/src/commonMain/kotlin/it/unibo/tuprolog/ui/gui/Runner.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/feature/gui/gui/src/commonTest/kotlin/it/unibo/tuprolog/ui/gui/EventsAsserter.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/feature/gui/gui/src/commonTest/kotlin/it/unibo/tuprolog/ui/gui/TestApplication.kt
- https://raw.githubusercontent.com/tuProlog/2p-kt/feature/gui/gui/src/commonTest/kotlin/it/unibo/tuprolog/ui/gui/TestPage.kt

## External platform context

Before finalising Compose/Web implementation choices, re-check current official documentation because Kotlin/Compose Web target maturity evolves quickly:

- https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform.html
- https://kotlinlang.org/docs/wasm-overview.html
- https://kotlinlang.org/docs/js-overview.html
- https://kotlinlang.org/api/kotlinx.coroutines/

---

# 59. Evidence and uncertainty note

This plan was prepared through source-level inspection of the repository as exposed on GitHub on 2026-09-02.

The repository was not locally executed as part of producing this document, so runtime-only behaviour may differ in small details from static interpretation. The implementation agent should therefore begin with Phase 0 and validate all parity assumptions against the exact baseline commit and executable applications before deleting legacy code.

The source-level evidence is nevertheless sufficient to establish the principal architectural facts:

- current IDE model/controller APIs are JVM/JavaFX/ReactFX coupled
- current graphical IDE exposes substantially more functionality than only editing/querying
- current PLP IDE exercises solver substitution, solve-option customisation, view replacement, probability display, BDD inspection, graph rendering, clipboard, and export
- current REPL duplicates significant solver/session semantics and has incomplete JS file helpers
- the historical `feature/gui` branch already explored toolkit-neutral application/page/event concepts and contained meaningful event-sequence tests; the revised target strengthens that direction by making query/resolution/session state explicitly page-scoped and separating `Document` from `Page`
- current release machinery is primarily JAR-oriented and needs explicit support for the new product matrix

These facts are the basis for the migration design above.
