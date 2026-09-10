package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.primitives.NewObject3
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * `new_object(+Type, ?ObjectRef)`: sugar for [NewObject3] with an empty argument list, i.e.
 * invoking `Type`'s no-argument public constructor.
 *
 * ```prolog
 * new_object(Type, Instance) :- new_object(Type, [], Instance).
 * ```
 *
 * @see NewObject3
 */
object NewObject2 : RuleWrapper<ExecutionContext>(NewObject3.functor, 2) {
    private val Type by variables
    private val Instance by variables

    override val Scope.head: List<Term>
        get() = listOf(Type, Instance)

    override val Scope.body: Term
        get() = structOf(NewObject3.functor, Type, emptyLogicList, Instance)
}
