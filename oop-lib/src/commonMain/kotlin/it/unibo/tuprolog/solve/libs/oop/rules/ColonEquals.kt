package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.OOP.CALL_OPERATOR
import it.unibo.tuprolog.solve.libs.oop.OOP.CAST_OPERATOR
import it.unibo.tuprolog.solve.libs.oop.primitives.Assign
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.Var
import it.unibo.tuprolog.solve.libs.oop.primitives.Cast as CastPrimitive

/**
 * The three clauses of the `:=`/2 operator, the entry point of `:oop-lib`'s fluent syntax: it
 * dispatches, based on whether its first argument is unbound and its second a cast expression, to
 * either invoking a (possibly chained) method/constructor call ([Invocation], [Cast]) or assigning
 * a property ([Assignment]).
 *
 * ```prolog
 * ':='(R, X as T) :- var(R), !, fluent_reduce(X, Y), cast(Y, T, R).
 * ':='(R, M) :- var(R), !, fluent_reduce(M, R).
 * ':='(C, V) :- property_reduce(C, R, P), assign(R, P, V).
 * ```
 *
 * For instance, `R := X.foo(1) as 'java.lang.Long'` first fluently reduces `X.foo(1)` and then
 * casts the outcome to `Long` before binding `R` ([Cast]); `R := X.foo(1).bar` fluently chains two
 * invocations into `R` ([Invocation]); and `X.name := joe` reduces `X.name` to a `(Ref, Property)`
 * pair and assigns `joe` to it ([Assignment]).
 *
 * @see it.unibo.tuprolog.solve.libs.oop.primitives.Cast
 * @see it.unibo.tuprolog.solve.libs.oop.primitives.Assign
 * @see FluentReduce
 * @see PropertyReduce
 */
sealed class ColonEquals : RuleWrapper<ExecutionContext>(CALL_OPERATOR, 2) {
    override val help: String =
        """
        `?Target := +Expression`
        
        Fluent evaluation and assignment. If `Target` is unbound, `Target := Receiver.m1(...).m2` evaluates the method/property chain on the right (see `./2`) and binds `Target` to the converted result of the last access; a right-hand side that is not a chain is just unified with `Target` (e.g. `X := 5`). `Target := Term as Type` additionally converts the (evaluated) `Term` via `cast/3`, binding `Target` to an object reference; here `Type` must be a type reference or a `${'$'}Alias` such as `${'$'}long`, and since `as` binds tighter than `.`, a chain must be parenthesised: `R := (Obj.size) as ${'$'}long`. If `Target` is bound, it must be a chain `Receiver.Property` (possibly longer), and `:=` evaluates everything but the last access and then assigns `Expression` to `Property` via `assign/3`.

        **Examples**

        ```prolog
        ?- new_object('java.util.ArrayList', [], L), L.add(a), N := L.size.
        N = 1.

        ?- X := 5.
        X = 5.

        ?- new_object('java.util.ArrayList', [], L), R := (L.size) as ${'$'}long, object_ref(R).
        yes.

        ?- new_object('java.awt.Point', [1, 2], P), P.x := 7, X := P.x.
        X = 7.
        ```
        """.trimIndent()

    object Cast : ColonEquals() {
        private val R by variables
        private val X by variables
        private val Y by variables
        private val T by variables

        override val Scope.head: List<Term>
            get() = listOf(R, structOf(CAST_OPERATOR, X, T))

        override val Scope.body: Term
            get() =
                tupleOf(
                    structOf(Var.functor, R),
                    atomOf("!"),
                    structOf(FluentReduce.FUNCTOR, X, Y),
                    structOf(CastPrimitive.functor, Y, T, R),
                )
    }

    object Invocation : ColonEquals() {
        private val R by variables
        private val M by variables

        override val Scope.head: List<Term>
            get() = listOf(R, M)

        override val Scope.body: Term
            get() =
                tupleOf(
                    structOf(Var.functor, R),
                    atomOf("!"),
                    structOf(FluentReduce.FUNCTOR, M, R),
                )
    }

    object Assignment : ColonEquals() {
        private val C by variables
        private val P by variables
        private val R by variables
        private val V by variables

        override val Scope.head: List<Term>
            get() = listOf(C, V)

        override val Scope.body: Term
            get() =
                tupleOf(
                    structOf(PropertyReduce.FUNCTOR, C, R, P),
                    structOf(Assign.functor, R, P, V),
                )
    }
}
