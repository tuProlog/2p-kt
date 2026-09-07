package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.OOP.ACCESS_OPERATOR
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * The `.`/2 (`xfy`, priority 800) operator that lets Prolog code chain member accesses fluently,
 * e.g. `Obj.foo(1).bar` for "invoke `foo(1)` on `Obj`, then `bar` on the result".
 *
 * ```prolog
 * '.'(Ref, Method) :- fluent_reduce([Ref | Method], _).
 * ```
 *
 * As a clause in its own right, this lets a `.` expression also be used directly as a goal (e.g.
 * `X.println("hi")` as a whole query), invoking the chain purely for its side effects and
 * discarding the reduced result; capture the result instead with `:=` (see [ColonEquals]).
 *
 * This relies on `.`/2 being, by construction, the very same functor/arity Prolog uses for list
 * cons cells: `Obj.foo(1).bar` parses (right-associatively) into `.(Obj, .(foo(1), bar))`, which
 * is indistinguishable from the (improper) list `[Obj, foo(1) | bar]` -- so [FluentReduce] can
 * walk it exactly like a list of "things to invoke in sequence" without any extra parsing.
 *
 * @see FluentReduce
 */
object Dot : RuleWrapper<ExecutionContext>(ACCESS_OPERATOR, 2) {
    private val Method by variables
    private val Ref by variables

    override val Scope.head: List<Term>
        get() =
            kotlin.collections.listOf(
                Ref,
                Method,
            )

    override val Scope.body: Term
        get() = structOf(FluentReduce.FUNCTOR, consOf(Ref, Method), `_`)
}
