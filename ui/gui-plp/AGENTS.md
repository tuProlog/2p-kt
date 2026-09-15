# ui/gui-plp — agent notes

## Probabilistic solving is opt-in, enabled here via a template-method override

`SolverFactorySession` (in `ui/gui`) builds `SolveOptions` via `SolveOptions.allLazily()`/
`allLazilyWithTimeout(...)`, which leaves `isProbabilistic = false` by default — probabilistic solving is
opt-in by design (see `solve-plp/.../ProbExtensions.kt`). `SolverFactorySession.configureSolveOptions(...)`
is an open template-method hook for exactly this: this module's `PlpSolverFactorySession` overrides it as
`options.setProbabilistic(true)`.

This is why `ide-plp-swing`'s Probability and BDD tabs previously stayed empty for *every* query, regardless
of what was asked — nothing in the PLP GUI wiring (`PlpGuiExtension`, `PlpSwingMain.kt`) ever turned the flag
on before this override existed. If those tabs ever look "always empty" again, check
`PlpSolverFactorySession.configureSolveOptions` first — it's the single place that enables probabilistic
resolution for a session. A plain (non-PLP) Prolog profile never touches this class, so this can't regress
the classic `ide-swing` path.
