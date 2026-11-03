package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.core.List as LogicList

/**
 * `add_arguments(+Goal, +Arguments, ?Extended)`: appends the [LogicList] `Arguments` to the arguments of the callable
 * `Goal`, as `call/2..8` do before calling it (see [it.unibo.tuprolog.solve.stdlib.rule.CallN]).
 */
object AddArguments : TernaryRelation.Functional<ExecutionContext>("add_arguments") {
    override val help: String =
        """
        `add_arguments(+Goal, +Arguments, ?Extended)`
        
        Unifies `Extended` with the goal obtained by appending the list `Arguments` to the arguments of `Goal`, which must be an atom or a compound term, as `call/2..8` do before calling it. Raises an instantiation error if `Goal` is unbound, and a type error (`callable`) if it is neither an atom nor a compound term.

        **Examples**

        ```prolog
        ?- add_arguments(foo, [1, 2], G).
        G = foo(1, 2).

        ?- add_arguments(foo(a), [b], G).
        G = foo(a, b).

        ?- add_arguments(1, [a], G).
        throws error(type_error(callable, 1), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term,
    ): Substitution {
        ensuringArgumentIsInstantiated(0)
        ensuringArgumentIsWellFormedList(1)
        val goal =
            first as? Struct
                ?: throw TypeError.forArgument(context, signature, TypeError.Expected.CALLABLE, first, 0)
        return mgu(third, Struct.of(goal.functor, goal.args + (second as LogicList).toList()))
    }
}
