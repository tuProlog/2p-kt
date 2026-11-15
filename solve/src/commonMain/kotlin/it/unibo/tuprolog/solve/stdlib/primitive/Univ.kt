package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.List as LogicList

/**
 * Implementation of '=..'/2 predicate
 */
object Univ : BinaryRelation.Functional<ExecutionContext>("=..") {
    override val help: String =
        """
        `?Term =.. ?List`
        
        Relates a term to its univ representation. A compound term is represented by a list whose first element is the functor and remaining elements are the arguments; atomic terms (atoms and numbers) are represented by a singleton list. The relation can also construct a term from a suitable list: a non-empty list whose head is an atom, or a singleton list holding an atomic term.

        **Examples**

        ```prolog
        ?- foo(a, B) =.. L.
        L = [foo, a, _].

        ?- T =.. [point, 1, 2].
        T = point(1, 2).

        ?- hello =.. L.
        L = [hello].

        ?- 42 =.. L.
        L = [42].

        ?- T =.. [f(a), b].
        throws error(type_error(atomic, f(a)), _).

        ?- T =.. [f, a|b].
        throws error(type_error(list, [f, a|b]), _).
        ```
        """.trimIndent()

    private fun Solve.Request<ExecutionContext>.decompose(
        first: Compound,
        second: Term,
    ): Substitution {
        val decomposed = LogicList.of(first.functorTerm, *first.args.toTypedArray())
        return mgu(second, decomposed)
    }

    private fun Solve.Request<ExecutionContext>.recompose(
        first: Term,
        second: LogicList,
    ): Substitution {
        val items = second.unfoldedList
        val tail = items.last()
        if (tail is Var) throw InstantiationError.forArgument(context, signature, tail, 1)
        if (!tail.isEmptyList) throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, second, 1)
        val list = items.dropLast(1)
        val head =
            list.firstOrNull()
                ?: throw DomainError.forArgument(context, signature, DomainError.Expected.NON_EMPTY_LIST, second, 1)
        val composed =
            when {
                head is Var -> throw InstantiationError.forArgument(context, signature, head, 1)
                list.size == 1 && head is Constant -> head
                head is Struct && head.arity > 0 ->
                    throw TypeError.forArgument(context, signature, TypeError.Expected.ATOMIC, head, 1)
                head !is Atom -> throw TypeError.forArgument(context, signature, TypeError.Expected.ATOM, head, 1)
                else -> Struct.of(head.value, list.drop(1))
            }
        return mgu(first, composed)
    }

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution =
        when (first) {
            is Compound -> {
                when (second) {
                    is LogicList -> decompose(first, second)
                    is Var -> decompose(first, second)
                    else -> {
                        throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, second, 1)
                    }
                }
            }
            is Var -> {
                when (second) {
                    is LogicList -> recompose(first, second)
                    is Var -> {
                        throw InstantiationError.forArgument(context, signature, first, 0)
                    }
                    else -> {
                        throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, second, 1)
                    }
                }
            }
            else -> {
                when (second) {
                    is LogicList, is Var -> mgu(second, LogicList.of(first))
                    else -> throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, second, 1)
                }
            }
        }
}
