# ui/ide-swing — agent notes

See `../AGENTS.md` for the general rule behind this (setters over Kotlin property shorthand for Swing
renderer wiring) and the macOS Swing-testing note.

## `SolutionCellRenderer` icon fix (concrete example)

`SolutionCellRenderer` (`solutions/SolutionCellRenderer.kt`) is a `DefaultTreeCellRenderer`. Overriding only
the getters (`getLeafIcon()`/`getOpenIcon()`/`getClosedIcon()` returning a `currentIcon` field) looks correct
but does **not** make custom icons show up in real, interactive use (regardless of look-and-feel) — something
in the real rendering path reads these via the setters' side effects, not the getter override alone. (An
offscreen `component.paint()` screenshot can look right either way, which makes this easy to mis-diagnose as
a screenshot-capture artifact rather than a real bug — verify in an actual interactive window.)

The fix, applied in `getTreeCellRendererComponent` immediately after `super.getTreeCellRendererComponent(...)`:

```kotlin
leafIcon = currentIcon
openIcon = currentIcon
closedIcon = currentIcon
icon = currentIcon
```

i.e. call the actual `setLeafIcon`/`setOpenIcon`/`setClosedIcon` setters, in addition to the getter
overrides and setting `icon` directly.

And in `SolutionTree.kt`'s `init` block, wire the renderer with the explicit setter:

```kotlin
setCellRenderer(SolutionCellRenderer())
```

not `cellRenderer = SolutionCellRenderer()`. Apply the same pattern (setters + getter overrides + explicit
`setCellRenderer`-equivalent wiring) to any other custom cell renderer added here or in `ide-plp-swing`.
