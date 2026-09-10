package it.unibo.tuprolog.solve

/**
 * Conformance tests for the non-standard `get_ephemeral/2`, `get_durable/2`, `get_persistent/2`, `set_ephemeral/2`,
 * `set_durable/2` and `set_persistent/2` built-ins, which read/write the three tiers of
 * [it.unibo.tuprolog.solve.data.CustomDataStore] attached to a [Solver]'s execution context. Shared by every
 * `Solver` implementation via the `TestCustomData.prototype(solverFactory)` factory (see `TestClassicCustomData` in
 * `:solve-classic` for a concrete usage).
 *
 * The three tiers only differ in how long an entry survives (see [it.unibo.tuprolog.solve.data.CustomDataStore]'s
 * own documentation); [testEphemeralData], [testDurableData] and [testPersistentData] each probe a different one
 * of those lifetimes.
 */
interface TestCustomData : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCustomData = TestCustomDataImpl(solverFactory)
    }

    /** Tests that a fresh [Solver]'s API exposes all six custom-data get/set primitives. */
    fun testApi()

    /**
     * Tests that, within a single query `set_ephemeral(key, 1), (get_ephemeral(key, X) ; get_ephemeral(key, X))`,
     * the first `get_ephemeral/2` succeeds binding `X` to `1`, but backtracking into the disjunction to retry
     * `get_ephemeral/2` a second time fails: ephemeral data does not survive backtracking within the same query.
     */
    fun testEphemeralData()

    /**
     * Same query shape as [testEphemeralData] but using `set_durable/2`/`get_durable/2`: unlike ephemeral data,
     * durable data survives backtracking, so *both* attempts at `get_durable/2` succeed, binding `X` to `1`.
     */
    fun testDurableData()

    /**
     * Tests that persistent data survives across separate `solve` calls on the same [Solver]: after solving
     * `set_persistent(key, 1)` in one call, a *separate* `solve` call for `get_persistent(key, X)` still succeeds
     * (binding `X` to `1`), and a query combining both within a single call behaves like [testDurableData].
     */
    fun testPersistentData()
}
