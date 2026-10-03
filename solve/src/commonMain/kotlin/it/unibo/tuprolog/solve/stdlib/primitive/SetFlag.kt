package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.PermissionError.Operation.MODIFY
import it.unibo.tuprolog.solve.exception.error.PermissionError.Permission.FLAG
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object SetFlag : BinaryRelation.NonBacktrackable<ExecutionContext>("set_flag") {
    override val help: String =
        """
        `set_flag(+Name, +Value)`
        
        Sets solver flag `Name` to the ground `Value`. For notable flags, editability and the declared admissible-value domain are enforced.

        **Examples**

        ```prolog
        ?- set_flag(unknown, fail).
        yes.

        ?- current_flag(unknown, X).
        X = fail.

        ?- set_flag(max_arity, 10).
        throws error(permission_error(modify, flag, max_arity), _).

        ?- set_flag(unknown, maybe).
        throws error(domain_error(_, maybe), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        ensuringArgumentIsInstantiated(0)
        ensuringArgumentIsAtom(0)
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsGround(1)
        val name = (first as Atom).value
        NotableFlag.fromName(name)?.let {
            if (!it.isEditable) {
                throw PermissionError.of(context, signature, MODIFY, FLAG, first)
            }
            if (second !in it.admissibleValues) {
                throw DomainError.forFlagValues(context, signature, it.admissibleValues.asIterable(), second, 1)
            }
        }
        return replySuccess {
            setFlag(name, second)
        }
    }
}
