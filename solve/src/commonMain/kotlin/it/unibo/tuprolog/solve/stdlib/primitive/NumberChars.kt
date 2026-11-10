package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.List as LogicList

object NumberChars : BinaryRelation.Functional<ExecutionContext>("number_chars") {
    override val help: String =
        """
        `number_chars(?Number, ?Chars)`
        
        Relates a number with the list of one-character atoms forming its textual representation. Either side may drive the conversion when sufficiently instantiated.

        **Examples**

        ```prolog
        ?- number_chars(N, ['4', '2']).
        N = 42.

        ?- number_chars(3.5, L).
        L = ['3', '.', '5'].

        ?- number_chars(N, L).
        throws error(instantiation_error, _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution =
        when {
            first is Var && second is Var -> {
                ensuringAllArgumentsAreInstantiated()
                Substitution.failed()
            }

            first is Var -> Substitution.of(first, parseNumber(charListText(1)))
            second is Var -> {
                ensuringArgumentIsNumeric(0)
                Substitution.of(second, LogicList.of(first.toString().map { Atom.of("" + it) }))
            }
            else -> {
                ensuringArgumentIsNumeric(0)
                if (second.isList && second.castToList().isWellFormed && second.variables.none()) {
                    mgu(first, parseNumber(charListText(1)))
                } else {
                    ensuringArgumentIsList(1)
                    mgu(LogicList.of(first.toString().map { Atom.of("" + it) }), second)
                }
            }
        }
}
