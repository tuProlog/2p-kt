package it.unibo.tuprolog.solve

/**
 * Conformance tests for a resolution exception's `logicStackTrace`: the sequence of goal [Signature]s active when
 * the exception was raised, from the innermost failing goal to the top-level query. All test cases share the same
 * three-rule theory `foo(X) :- bar(X). bar(X) :- baz(X). baz(X) :- <errorExpression>.`, varying only the innermost
 * `<errorExpression>` and the query wrapping `foo(X)`. Shared by every `Solver` implementation via the
 * `TestStackTrace.prototype(solverFactory)` factory (see `TestClassicStackTrace` in `:solve-classic` for a concrete
 * usage).
 */
interface TestStackTrace : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestStackTrace = TestStackTraceImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- foo(X).
     * ```
     * where `baz/1`'s body is `X is Y + 1` with `Y` unbound, so an `instantiation_error` is raised; the resulting
     * `logicStackTrace` is `[is/2, baz/1, bar/1, foo/1, ?-/1]`.
     */
    fun testSimpleStackTrace()

    /**
     * Same theory as [testSimpleStackTrace], but with the query wrapped in `findall(X, foo(X), L)`; the resulting
     * `logicStackTrace` gains one extra `findall/3` frame between `foo/1` and the top-level `?-/1`.
     */
    fun testDoubleStackTrace()

    /**
     * Same theory as [testSimpleStackTrace], but with the query wrapped in `findall(X, bagof(Z, foo(Z), X), L)`;
     * the resulting `logicStackTrace` gains both a `bagof/3` and a `findall/3` frame, in that order, between
     * `foo/1` and the top-level `?-/1`.
     */
    fun testTripleStackTrace()

    /**
     * Same query as [testTripleStackTrace], but with `baz/1`'s body replaced by `throw(x)`: the resulting halt is a
     * `SystemError` wrapping the uncaught `x`, and its `logicStackTrace` does *not* include a frame for `throw/1`
     * itself — only `[baz/1, bar/1, foo/1, bagof/3, findall/3, ?-/1]` — since a `throw/1` call does not push a
     * stack frame the way a user-defined goal invocation does.
     */
    fun testThrowIsNotInStacktrace()
}
