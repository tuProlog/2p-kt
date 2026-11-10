package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.QuinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.List as LogicList

object SubAtom : QuinaryRelation.WithoutSideEffects<ExecutionContext>("sub_atom") {
    override val help: String =
        """
        `sub_atom(+Atom, ?Before, ?Length, ?After, ?SubAtom)`
        
        Relates `Atom` with a contiguous `SubAtom` and the number of characters before, within, and after it. On backtracking, it enumerates every slice consistent with the bound arguments, by increasing `Before` and then `Length`.

        **Examples**

        ```prolog
        ?- sub_atom(hello, 1, 3, A, S).
        A = 1, S = ell.

        ?- sub_atom(abracadabra, B, 2, A, ab).
        B = 0, A = 9 ; B = 7, A = 2.

        ?- sub_atom(ab, B, L, A, S).
        B = 0, L = 0, A = 2, S = '' ; B = 0, L = 1, A = 1, S = a.

        ?- sub_atom(X, 0, 1, _, S).
        throws error(instantiation_error, _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term,
        fourth: Term,
        fifth: Term,
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(0)
        ensuringArgumentIsAtom(0)
        if (second !is Var) ensuringArgumentIsInteger(1)
        if (third !is Var) ensuringArgumentIsInteger(2)
        if (fourth !is Var) ensuringArgumentIsInteger(3)
        if (fifth !is Var) ensuringArgumentIsAtom(4)
        val whole = (first as Atom).value
        val n = whole.length
        val sub = (fifth as? Atom)?.value
        val befores = (second as? Integer)?.intValue?.toInt()?.let { sequenceOf(it) } ?: (0..n).asSequence()
        return befores
            .filter { it in 0..n }
            .flatMap { before ->
                val lengths =
                    when {
                        sub != null -> sequenceOf(sub.length)
                        third is Integer -> sequenceOf(third.intValue.toInt())
                        fourth is Integer -> sequenceOf(n - before - fourth.intValue.toInt())
                        else -> (0..n - before).asSequence()
                    }
                lengths.filter { it in 0..n - before }.map { length -> before to length }
            }.filter { (before, length) -> sub == null || whole.startsWith(sub, before) }
            .map { (before, length) ->
                mgu(
                    LogicList.of(second, third, fourth, fifth),
                    LogicList.of(
                        Integer.of(before),
                        Integer.of(length),
                        Integer.of(n - before - length),
                        Atom.of(whole.substring(before, before + length)),
                    ),
                )
            }.filter { it.isSuccess }
    }
}
