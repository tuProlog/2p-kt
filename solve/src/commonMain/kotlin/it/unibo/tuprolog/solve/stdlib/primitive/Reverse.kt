package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object Reverse : BinaryRelation.Functional<ExecutionContext>("reverse") {
    override val help: String =
        """
        `reverse(?List, ?Reversed)`
        
        Relates a proper list with the list containing the same elements in reverse order. Either argument may be used to determine the other when sufficiently instantiated.

        **Examples**

        ```prolog
        ?- reverse([1, 2, 3], R).
        R = [3, 2, 1].

        ?- reverse(L, [a, b]).
        L = [b, a].

        ?- reverse([1|T], R).
        throws error(domain_error(well_formed_list, _), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution =
        when {
            first is Var -> {
                ensuringArgumentIsWellFormedList(1)
                reverse(second as List, first)
            }
            second is Var -> {
                ensuringArgumentIsWellFormedList(0)
                reverse(first as List, second)
            }
            else -> {
                ensuringAllArgumentsAreInstantiated()
                ensuringArgumentIsList(0)
                ensuringArgumentIsList(1)
                val list1 = first as List
                val list2 = second as List
                when {
                    list1.isWellFormed -> reverse(first, second)
                    list2.isWellFormed -> reverse(second, first)
                    else -> {
                        ensuringArgumentIsWellFormedList(0)
                        ensuringArgumentIsWellFormedList(1)
                        Substitution.failed()
                    }
                }
            }
        }

    private fun Solve.Request<ExecutionContext>.reverse(
        list: List,
        other: Term,
    ): Substitution {
        val reversed = List.of(list.toList().asReversed())
        return mgu(reversed, other)
    }
}
