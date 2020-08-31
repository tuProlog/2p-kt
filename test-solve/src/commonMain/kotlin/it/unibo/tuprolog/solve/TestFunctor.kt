package it.unibo.tuprolog.solve

interface TestFunctor : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestFunctor =
            TestFunctorImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- functor(foo(a,b,c),foo,3).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testFunArity()

    /**
     * Tests the queries
     * ```prolog
     * ?- functor(foo(a,b,c),X,Y).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solutions which binds the variable `X` to `foo` and the variable `Y` to `3` .
     */
    fun testFunArityWithSub()

    /**
     * Tests the queries
     * ```prolog
     * ?- functor(X,foo,0).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds the variable `X` to `foo`.
     */
    fun testFunArityZero()

    /**
     * Tests the queries
     * ```prolog
     * ?- functor(mats(A,B),A,B).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solutions which binds the variable `A` to `mats` and the variable `B` to `2` .
     */
    fun testFunMats()

    /**
     * Tests the queries
     * ```prolog
     * ?- functor(foo(a),foo,2).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testFunWrongArity()

    /**
     * Tests the queries
     * ```prolog
     * ?- functor(foo(a),fo,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testFunWrongName()

    /**
     * Tests the queries
     * ```prolog
     * ?- functor(1,X,Y).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solutions which binds the variable `X` to `1` and the variable `Y` to `0` .
     */
    fun testFunXNameYArity()

    /**
     * Tests the queries
     * ```prolog
     * ?- functor(X,1.1,0).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds the variable `X` to `1.1`.
     */
    fun testFunDecNum()

    /**
     * Tests the queries
     * ```prolog
     * ?- functor([_|_],'.',2).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testFunConsOf()

    /**
     * Tests the queries
     * ```prolog
     * ?- functor([],[],0).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testFunEmptyList()

    /**
     * Tests the query
     * ```prolog
     * ?- functor(X, Y, 3).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `instantiation_error`.
     */
    fun testFunXYWrongArity()

    /**
     * Tests the query
     * ```prolog
     * ?- functor(X, foo, N).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `instantiation_error`.
     */
    fun testFunXNArity()

    /**
     * Tests the query
     * ```prolog
     * ?- functor(X, foo, a).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(integer,a)`.
     */
    fun testFunXAArity()

    /**
     * Tests the query
     * ```prolog
     * ?- functor(X, 1.5, 1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(atom,1.5)`.
     */
    fun testFunNumName()

    /**
     * Tests the query
     * ```prolog
     * ?- functor(X, foo, 1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(atomic,foo(a))`.
     */
    fun testFunFooName()

    /**
     * Tests the query
     * ```prolog
     * ?- (current_prolog_flag(max_arity,A), X is A + 1, functor(T, foo, X)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `representation_error(max_arity)`.
     */
    fun testFunFlag()

    /**
     * Tests the query
     * ```prolog
     * ?- functor(T, foo, -1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `domain_error(not_less_than_zero,-1)`.
     */
    fun testFunNegativeArity()
}