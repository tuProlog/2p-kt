package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.List as LogicList

object NumberCodes : BinaryRelation.Functional<ExecutionContext>("number_codes") {
    override val help: String =
        """
        `number_codes(?Number, ?Codes)`
        
        Relates a number with the list of integer character codes forming its textual representation. Either side may drive the conversion when sufficiently instantiated.

        **Examples**

        ```prolog
        ?- number_codes(N, [52, 50]).
        N = 42.

        ?- number_codes(12, L).
        L = [49, 50].

        ?- number_codes(N, [52, a]).
        throws error(type_error(integer, a), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution =
        when {
            first is Var -> {
                ensuringArgumentIsInstantiated(1)
                Substitution.of(first, parseNumber(text(second)))
            }
            else -> {
                ensuringArgumentIsNumeric(0)
                if (second.isList && second.castToList().isWellFormed && second.variables.none()) {
                    mgu(first, parseNumber(text(second)))
                } else {
                    if (second !is Var) ensuringArgumentIsList(1)
                    mgu(second, LogicList.of(first.toString().map { Integer.of(it.code) }))
                }
            }
        }

    private fun Solve.Request<ExecutionContext>.text(codes: Term): String {
        ensuringArgumentIsList(1)
        val list = codes.castToList()
        val tail = list.last
        when {
            tail is Var -> throw InstantiationError.forArgument(context, signature, tail, 1)
            !list.isWellFormed -> throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, list, 1)
        }
        return list.toSequence().joinToString("") {
            when (it) {
                is Integer -> {
                    ensuringTermIsCharCode(it)
                    it.intValue.toChar().toString()
                }
                is Var -> throw InstantiationError.forArgument(context, signature, it, 1)
                else -> throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, it, 1)
            }
        }
    }
}
