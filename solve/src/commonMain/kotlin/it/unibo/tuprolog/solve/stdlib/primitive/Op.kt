package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.solve.sideffects.SideEffect
import org.gciatto.kt.math.BigInteger

private const val MAX_PRIORITY = 1200

object Op : TernaryRelation.NonBacktrackable<ExecutionContext>("op") {
    override val help: String =
        """
        `op(+Priority, +Specifier, +Name)`
        
        Adds or updates an operator declaration in the current solver state. `Priority` must be an integer between 0 and 1200, `Specifier` a valid operator specifier such as `yfx`, and `Name` an atom; a priority above 1200 raises a domain error (`operator_priority`).

        **Examples**

        ```prolog
        ?- op(700, xfx, likes).
        yes.

        ?- X = (alice likes bob), X = likes(A, B).
        A = alice, B = bob.

        ?- op(700, abc, foo).
        throws error(domain_error(operator_specifier, abc), _).

        ?- op(1201, xfx, foo).
        throws error(domain_error(operator_priority, 1201), _).
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
        ensuringArgumentIsAtom(2)
        val operator = Operator.fromTerms(first, second as Atom, third as Atom)!!
        return replySuccess(
            Substitution.empty(),
            null,
            SideEffect.SetOperators(operator),
        )
    }
}
