package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.solve.sideffects.SideEffect
import org.gciatto.kt.math.BigInteger
import it.unibo.tuprolog.core.List as LogicList

private const val MAX_PRIORITY = 1200

object Op : TernaryRelation.NonBacktrackable<ExecutionContext>("op") {
    override val help: String =
        """
        `op(+Priority, +Specifier, +Names)`
        
        Adds or updates operator declarations in the current solver state. `Priority` must be an integer between 0 and 1200, `Specifier` a valid operator specifier such as `yfx`, and `Names` an atom or a list of atoms, each of which becomes an operator; a priority above 1200 raises a domain error (`operator_priority`).

        **Examples**

        ```prolog
        ?- op(700, xfx, likes).
        yes.

        ?- X = (alice likes bob), X = likes(A, B).
        A = alice, B = bob.

        ?- op(200, xfx, [foo, bar]), current_op(P, T, bar).
        P = 200, T = xfx.

        ?- op(700, abc, foo).
        throws error(domain_error(operator_specifier, abc), _).

        ?- op(1201, xfx, foo).
        throws error(domain_error(operator_priority, 1201), _).

        ?- op(200, xfx, [foo|bar]).
        throws error(type_error(list, [foo|bar]), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
    ): Solve.Response {
        ensuringArgumentIsInteger(0)
        ensuringArgumentIsNonNegativeInteger(0)
        if ((first as Integer).intValue > BigInteger.of(MAX_PRIORITY)) {
            throw DomainError.forArgument(context, signature, DomainError.Expected.OPERATOR_PRIORITY, first, 0)
        }
        ensuringArgumentIsAtom(1)
        ensuringArgumentIsSpecifier(1)
        val operators = operatorNames(third).map { Operator.fromTerms(first, second as Atom, it)!! }
        return replySuccess(
            Substitution.empty(),
            null,
            SideEffect.SetOperators(operators),
        )
    }

    /** The operator names in [names], an atom or a list of atoms (ISO). */
    private fun Solve.Request<ExecutionContext>.operatorNames(names: Term): List<Atom> {
        if (names !is LogicList) {
            ensuringArgumentIsAtom(2)
            return listOf(names as Atom)
        }
        val items = names.unfoldedList
        val tail = items.last()
        if (tail is Var) throw InstantiationError.forArgument(context, signature, tail, 2)
        if (!tail.isEmptyList) throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, names, 2)
        return items.dropLast(1).map {
            when (it) {
                is Var -> throw InstantiationError.forArgument(context, signature, it, 2)
                is Atom -> it
                else -> throw TypeError.forArgument(context, signature, TypeError.Expected.ATOM, it, 2)
            }
        }
    }
}
