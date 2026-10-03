package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * `fluent_reduce(+Expression, ?Result)`: reduces a fluent chain of accesses -- as built by the
 * `.`/2 operator ([it.unibo.tuprolog.solve.libs.oop.rules.Dot], e.g. `Obj.foo(1).bar` parses into
 * the list `[Obj, foo(1), bar]`) -- down to a single `Result`, by repeatedly invoking each element
 * of the list as a method on the result of invoking the previous one:
 *
 * ```prolog
 * fluent_reduce([P, M | X], R) :- !, invoke_method(P, M, P1), fluent_reduce([P1 | X], R). % recursive
 * fluent_reduce([P | M], R) :- !, invoke_method(P, M, R).                                 % couple
 * fluent_reduce(R, R) :- !.                                                               % trivial
 * ```
 *
 * The trivial clause also covers non-list expressions (e.g. a bare [it.unibo.tuprolog.solve.libs.oop.Ref]
 * or a `$Alias` expression, with nothing left to invoke), which simply reduce to themselves.
 *
 * @see it.unibo.tuprolog.solve.libs.oop.rules.Dot
 * @see it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
 */
@Suppress("PropertyName")
sealed class FluentReduce : RuleWrapper<ExecutionContext>(FUNCTOR, ARITY) {
    override val help: String =
        """
        `fluent_reduce(+Expression, ?Result)`
        
        Helper behind `./2` and `:=/2`: evaluates a chain of member accesses `Receiver.M1.M2...` (which, as `.` is the list constructor, is the same term as the improper list `[Receiver, M1 | M2]`) by invoking `M1` on `Receiver` via `invoke_method/3`, then `M2` on that result, and so on, unifying `Result` with the last result. A term that is not a chain is unified with `Result` unchanged. Deterministic (each clause cuts). Errors are those of `invoke_method/3`.

        **Examples**

        ```prolog
        ?- fluent_reduce(${'$'}math.max(1, 2), M).
        M = 2.

        ?- new_object('java.lang.StringBuilder', [abc], B), fluent_reduce(B.reverse.toString, S).
        S = cba.

        ?- fluent_reduce(foo, R).
        R = foo.
        ```
        """.trimIndent()

    companion object {
        const val FUNCTOR = "fluent_reduce"
        const val ARITY = 2
    }

    protected val R by variables

    override val Scope.body: Term
        get() = atomOf("!")

    object Recursive : FluentReduce() {
        private val P by variables
        private val M by variables
        private val P1 by variables
        private val X by variables

        override val Scope.head: List<Term>
            get() =
                kotlin.collections.listOf(
                    logicListFrom(P, M, last = X),
                    R,
                )

        override val Scope.body: Term
            get() =
                tupleOf(
                    atomOf("!"),
                    structOf(InvokeMethod.functor, P, M, P1),
                    structOf(FUNCTOR, consOf(P1, X), R),
                )
    }

    object Couple : FluentReduce() {
        private val P by variables
        private val M by variables

        override val Scope.head: List<Term>
            get() = kotlin.collections.listOf(consOf(P, M), R)

        override val Scope.body: Term
            get() =
                tupleOf(
                    atomOf("!"),
                    structOf(InvokeMethod.functor, P, M, R),
                )
    }

    // object Base : FluentReduce() {
    //     override val Scope.head: List<Term>
    //         get() = kotlin.collections.listOf(
    //             listOf(R),
    //             R
    //         )
    // }

    object Trivial : FluentReduce() {
        override val Scope.head: List<Term>
            get() = kotlin.collections.listOf(R, R)
    }
}
