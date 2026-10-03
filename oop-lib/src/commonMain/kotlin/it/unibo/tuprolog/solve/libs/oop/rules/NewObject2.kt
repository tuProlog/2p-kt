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
    override val help: String =
        """
        `new_object(+Type, -ObjectRef)`
        
        Instantiates `Type` through its public no-argument constructor and unifies `ObjectRef` with a reference to the new object; shorthand for `new_object(Type, [], ObjectRef)`. `Type` may be an atom holding a fully qualified type name, a type reference, or a `${'$'}Alias` reference to a type. Errors are those of `new_object/3`. Example: `new_object('java.util.ArrayList', L)`.
        """.trimIndent()

    private val Type by variables
    private val Instance by variables

    override val Scope.head: List<Term>
        get() = listOf(Type, Instance)

    override val Scope.body: Term
        get() = structOf(NewObject3.functor, Type, emptyLogicList, Instance)
}
