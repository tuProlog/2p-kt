package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.AddArguments
import kotlin.collections.List as KtList

/**
 * `call/N`, for `N` from 2 to 8 (ISO/IEC 13211-1:1995/Cor.2:2012): `call(G, A1, ..., An) :- add_arguments(G, [A1,
 * ..., An], C), call(C).`, i.e. calls `G` with the extra arguments `A1, ..., An` appended to its own (see
 * [AddArguments]). Like `call/1`, it is opaque to cut.
 */
class CallN private constructor(
    arity: Int,
) : RuleWrapper<ExecutionContext>("call", arity) {
    override val help: String =
        """
        `call(+Goal, ?Arg1, ..., ?ArgN)`
        
        Calls `Goal` with the extra arguments `Arg1, ..., ArgN` appended to its own, for `N` from 1 to 7: `call(succ_or_zero, 1, X)` calls `succ_or_zero(1, X)`, and `call(foo(a), b)` calls `foo(a, b)`. It is the portable way to write higher-order predicates such as `maplist/3`. Like `call/1`, it is opaque to cut. Raises an instantiation error if `Goal` is unbound and a type error (`callable`) if it is neither an atom nor a compound term.

        **Examples**

        ```prolog
        ?- call(atom_length, abc, L).
        L = 3.

        ?- call(atom_length(abc), L).
        L = 3.

        ?- call(1, a).
        throws error(type_error(callable, 1), _).
        ```
        """.trimIndent()

    override val Scope.head: KtList<Term>
        get() = (0 until signature.arity).map { if (it == 0) varOf("G") else varOf("A$it") }

    override val Scope.body: Term
        get() =
            tupleOf(
                structOf(AddArguments.functor, varOf("G"), logicListOf(head.drop(1)), varOf("C")),
                structOf("call", varOf("C")),
            )

    companion object {
        /** `call/2` to `call/8`. */
        val wrappers: Sequence<CallN> = (2..MAX_ARITY).asSequence().map { CallN(it) }

        private const val MAX_ARITY = 8
    }
}
