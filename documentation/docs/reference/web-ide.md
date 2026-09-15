# Web IDE (`:ide-web`)

`:ide-web` is a browser-based Prolog IDE (Kotlin/JS, Ace editor), reachable at
[tuprolog.github.io/2p-kt/web-ide/](https://tuprolog.github.io/2p-kt/web-ide/) with no install required. See
[Using the Web IDE](../tutorials/using-the-web-ide.md) for a hands-on walkthrough,
[Deploy the Web IDE](../how-to/deploy-the-web-ide.md) for self-hosting it, and
[Web IDE architecture](../explanation/web-ide-architecture.md) for the rationale behind the choices summarized
here. It is the browser counterpart of the desktop [`:ide-swing`](module-map.md) IDE — both are built on the
same toolkit-neutral [`:gui`](module-map.md) model, so almost everything below applies to the desktop IDE too.

## Layout

| Area | Contains |
|---|---|
| Menu bar (top) | Page management (New/Open/Save/Upload/Download), settings, and the status/caret labels. |
| Query bar | The query field, Solve/Solve 10/Solve 100/Solve all/Stop/Reset buttons, and the timeout field. |
| Editor (left/center) | The Ace-based Prolog text editor for the selected page. |
| Side panel (right) | Eleven tabs surfacing the solver's state — see [Side panels](#side-panels). |

The layout is responsive: above 820px wide, the editor and side panel sit side by side, with a draggable divider
(`#split-handle`) between them that works with a mouse, a touchscreen, or a pen alike. Below that width — a
phone, or a browser window narrowed that far — the side panel drops below the editor instead, at a fixed 40%
of the viewport height with its own scrolling, and the divider disappears (dragging a *width* stops making
sense once the two areas are stacked instead of side by side). The menu bar and query bar's buttons wrap onto
further rows rather than overflowing at any width.

## Menu bar

| Button | Effect |
|---|---|
| **New** | Opens a new, empty, untitled page. |
| **New from template…** (dropdown, hidden if no templates are configured) | Opens a new page pre-filled with a built-in example theory — see [Built-in templates](#built-in-templates). |
| **Open...** | Lists pages previously saved to this browser's local storage and opens the chosen one. |
| **Save** / **Save as...** | Persists the current page to this browser's local storage (see [Persistence](#persistence)). |
| **Close page** | Closes the selected page (prompting first if it has unsaved changes). |
| **Upload...** | Opens a real `.pl`/`.pro`/`.prolog`/text file from disk as a new page. |
| **Download** | Saves the selected page's current text as a real file on disk. |
| **Restore default settings** | Resets the editor's zoom level back to the default font size. |
| **Delete persisted state…** | After confirmation, wipes every locally-stored page/workspace setting and suppresses the next autosave, so the app comes back up empty on reload. |

The status label (center-right) shows the current page's resolution status (`Idle`, `Resolution: RUNNING`, ...);
the caret label (far right) shows the caret's current line/column, **one-based** like every other location the
IDE displays.

## Solving controls

| Control | Behavior |
|---|---|
| Query field | The goal to solve; pressing <kbd>Enter</kbd> here is equivalent to clicking Solve. |
| **Solve** (→ **Next** once a resolution is in progress) | Requests one further solution. |
| **Solve 10** / **Solve 100** (→ **Next 10** / **Next 100**) | Requests up to 10/100 further solutions in one batch. |
| **Solve all** (→ **All next**) | Requests every remaining solution, with no limit. |
| **Stop** | Cancels the in-progress resolution. |
| **Reset** | Clears the current resolution and any solver-session side effects (assertions, retractions, loaded libraries/operators/flags), back to a fresh session. |
| Timeout field | Accepts a duration like `500ms`, `5s`, `1h 30m`, or `none`/`0` for no limit; applies to the *next* query. |

```kotlin
--8<-- "ui/gui/src/commonMain/kotlin/it/unibo/tuprolog/ui/gui/controller/ConsumptionMode.kt:5:12"
```

## Side panels

```kotlin
--8<-- "ui/gui/src/commonMain/kotlin/it/unibo/tuprolog/ui/gui/model/PanelId.kt:4:16"
```

| Panel | Shows |
|---|---|
| Solutions | The solver's answers to the current query, one entry per solution (or a `halt:`/`timeout:` entry if resolution stopped abnormally). |
| Stdin | A text area feeding the Prolog interpreter's standard input stream. |
| Stdout | The Prolog interpreter's standard output stream. |
| Stderr | The Prolog interpreter's standard error stream. |
| Warnings | Non-fatal issues raised while solving (e.g. an undefined predicate). |
| Diagnostics | Syntax errors/warnings in the currently edited theory, each with a one-based line/column location. |
| Operators | The solver's current operator table; editable — add a row to define a new operator. |
| Flags | The solver's current flag values; editable where the flag allows it. |
| Libraries | The currently loaded libraries and the predicates/operators/functions each one contributes. |
| Static KB | Read-only: the clauses that make up the page's static knowledge base. |
| Dynamic KB | Read-only: clauses added/removed at runtime via assertions/retractions. |

Solutions, Stdout, Stderr, Warnings, and Diagnostics mark themselves with an unread-changes indicator whenever
they update while a different tab is selected; the rest never do (their content isn't something a query run
"pushes" to you).

## Editor

Syntax highlighting, squiggly-underlined diagnostics with hover tooltips, and the one-based line/column
reporting are all driven by the same lexer/parser 2P-Kt's other tooling uses — see
[Web IDE architecture](../explanation/web-ide-architecture.md#one-highlighter-for-both-frontends).

### Keyboard shortcuts

| Shortcut | Effect |
|---|---|
| <kbd>Ctrl</kbd>/<kbd>Cmd</kbd> + <kbd>+</kbd> (or <kbd>=</kbd>) | Zoom in (increase editor font size). |
| <kbd>Ctrl</kbd>/<kbd>Cmd</kbd> + <kbd>-</kbd> | Zoom out. |
| <kbd>Ctrl</kbd>/<kbd>Cmd</kbd> + <kbd>0</kbd> | Reset zoom to the default font size. |
| <kbd>Ctrl</kbd>/<kbd>Cmd</kbd> + mouse wheel | Zoom in/out, mirroring native browser zoom. |

Font size is clamped between 8px and 48px and persists across reloads (see [Persistence](#persistence)).

### Built-in templates

The **New from template…** dropdown offers four ready-made theories, shared with the desktop IDE: Peano
arithmetic, N-Queens, a family tree, and list processing.

## Persistence

Two entirely separate storage mechanisms are in play, and they're not interchangeable:

| Action | Storage | Survives a reload? | Leaves the browser? |
|---|---|---|---|
| New / Open... / Save / Save as... | Browser local storage | Yes | No |
| Upload... / Download | A real file on disk, picked by the browser's file dialog | N/A (it's a file) | Yes |

Local storage also holds the workspace's own settings (open pages, selected page, editor zoom level, window
layout), restored automatically the next time the app loads — until **Delete persisted state…** is used, or
until the browser's own storage for the site is cleared.

## Errors

A page whose theory fails to parse (e.g. a stray unclosed bracket) still allows solving to be attempted; the
resolution fails immediately, and the status label reports `Resolution failed: <message>`, where `<message>`
includes the same one-based line/column location shown in the Diagnostics tab.
