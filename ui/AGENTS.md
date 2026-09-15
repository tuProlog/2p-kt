# ui/ — agent notes

Modules: `gui` (toolkit-agnostic solver-session core), `gui-plp` (probabilistic extension of `gui`),
`ide-swing` (Swing IDE), `ide-plp-swing` (Swing IDE with probabilistic/BDD features, extends `ide-swing`),
`ide-web` (Kotlin/JS), `repl`. See root `AGENTS.md` for repo-wide detekt and graph-rendering notes.

## Testing Swing UI from a non-interactive process (macOS)

On macOS, `java.awt.Robot`-driven synthetic input (AssertJ Swing, or anything using
`Robot.mousePress`/`keyPress`) silently does nothing when the driving process lacks the Accessibility
("control your computer") permission — which any automated/non-interactive process (CI agent, sandboxed
shell, etc.) typically does, even when `GraphicsEnvironment.isHeadless()` reports `false`. A spawned window
renders and direct EDT mutations (`setText(...)`) work, but
`KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner` stays `null` after a synthetic click, and
clicking a `JTabbedPane` tab never changes `selectedIndex` — no exception is thrown.

- Detect it by checking `focusOwner`/`selectedIndex` after a simulated action, not by trusting the absence of
  an exception.
- Focus/toFront/always-on-top tricks don't fix it — they only affect the JVM's own AWT bookkeeping, not the
  OS-level permission gate. Granting Accessibility permission to the driving process (System Settings →
  Privacy & Security → Accessibility) does, where that's possible.
- Where it isn't possible (e.g. a sandboxed agent), verify Swing/AssertJ-Swing E2E suites either
  interactively, or on a Linux CI runner under `xvfb-run` (X11 has no equivalent permission gate — no window
  manager needed).

## Custom Swing cell renderers: use setters, not Kotlin property shorthand

For `DefaultTreeCellRenderer`/`DefaultListCellRenderer`-style custom renderers (`ide-swing`,
`ide-plp-swing`), and for wiring one onto its `JTree`/`JList`, call the actual `setXxx(...)` methods
(`setLeafIcon`/`setOpenIcon`/`setClosedIcon`, `setCellRenderer`) rather than Kotlin's property-assignment
shorthand (`cellRenderer = ...`). Both forms compile and *should* be equivalent, but in real interactive use
only the explicit-setter form renders correctly — see `ide-swing/AGENTS.md` for the verified case and exact
pattern.
