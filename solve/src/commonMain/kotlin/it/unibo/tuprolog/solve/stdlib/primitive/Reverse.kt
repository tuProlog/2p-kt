package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object Reverse : BinaryRelation.Functional<ExecutionContext>("reverse") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution {
        return when {
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
    }

    private fun Solve.Request<ExecutionContext>.reverse(list: List, other: Term): Substitution {
        val reversed = List.of(list.toList().asReversed())
        return mgu(reversed, other)
    }
}
