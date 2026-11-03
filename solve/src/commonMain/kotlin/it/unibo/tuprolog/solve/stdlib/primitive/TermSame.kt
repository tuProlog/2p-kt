package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/** Implementation of '=@='/2 predicate */
object TermSame : BinaryRelation.Predicative<ExecutionContext>("=@=") {
    override val help: String =
        """
        `Left =@= Right`
        
        Succeeds when `Left` and `Right` are variants of each other: equal up to a consistent renaming of their variables, so that each variable of one corresponds to exactly one variable of the other. It does not instantiate either term.

        **Examples**

        ```prolog
        ?- f(a, X) =@= f(a, X).
        yes.

        ?- f(a) =@= f(b).
        no.

        ?- f(X, Y) =@= f(Y, X).
        yes.

        % a shared variable is not a variant of two distinct ones
        ?- f(X, X) =@= f(Y, Z).
        no.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
    ): Boolean = first isVariantOf second
}

/**
 * Whether this term and [other] are variants: equal up to a bijective renaming of their variables. Iterative, so that
 * long lists don't overflow the stack.
 */
internal infix fun Term.isVariantOf(other: Term): Boolean {
    val forward = mutableMapOf<Var, Var>()
    val backward = mutableMapOf<Var, Var>()
    val pending = ArrayDeque(listOf(this to other))
    while (pending.isNotEmpty()) {
        val (left, right) = pending.removeLast()
        val same =
            when {
                left is Var && right is Var ->
                    forward.getOrPut(left) { right } == right && backward.getOrPut(right) { left } == left
                left is Var || right is Var -> false
                left is Struct && right is Struct ->
                    (left.functor == right.functor && left.arity == right.arity).also {
                        if (it) pending.addAll(left.args.zip(right.args))
                    }
                else -> left == right
            }
        if (!same) return false
    }
    return true
}
