package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

/**
 * `new_object(+Type, +Arguments, ?ObjectRef)`: constructs a new instance of `Type` (a
 * [it.unibo.tuprolog.solve.libs.oop.TypeRef], type-name atom, or `$Alias` expression) by invoking
 * whichever public constructor best matches the Prolog list `Arguments`, unifying `ObjectRef` with
 * an [it.unibo.tuprolog.solve.libs.oop.ObjectRef] wrapping the freshly created instance. See
 * [it.unibo.tuprolog.solve.libs.oop.rules.NewObject2] for the no-arguments `new_object/2` sugar.
 *
 * Example: `new_object('java.util.ArrayList', [], L)` creates an empty `ArrayList`.
 *
 * Fails (rather than throwing) if `Type` is an atom naming a type that cannot be resolved.
 *
 * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.ConstructorInvocationException if `Type`
 * resolves to a type with no public constructor accepting `Arguments`.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if `Type` is neither a
 * [it.unibo.tuprolog.solve.libs.oop.TypeRef], a type-name atom, nor a `$Alias` expression, or
 * `Arguments` is not a list.
 */
object NewObject3 : TernaryRelation.Functional<ExecutionContext>("new_object") {
    override val help: String =
        """
        `new_object(+Type, +Arguments, -ObjectRef)`
        
        Instantiates `Type` by calling its public constructor that best matches the list `Arguments`, and unifies `ObjectRef` with a reference to the new object. `Type` may be an atom holding a fully qualified type name (e.g. `'java.util.ArrayList'`), a type reference, or a `${'$'}Alias` reference to a type (e.g. `${'$'}arraylist`). Arguments are converted to objects as in `invoke_method/3`; use `as` casts to disambiguate overloaded constructors. Fails if the type name cannot be resolved. Raises a type error if `Type` is not callable or `Arguments` is not a list, an existence error if no constructor accepts the arguments, and a system error if the constructor throws.

        **Examples**

        ```prolog
        ?- new_object('java.util.ArrayList', [], L), L.add(a), N := L.size.
        N = 1.

        ?- new_object(${'$'}arraylist, [], L), list_items(L, Items).
        Items = [].

        ?- new_object('no.such.Type', [], L).
        no.

        ?- new_object('java.util.ArrayList', [a], L).
        throws error(representation_error(_), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
        third: Term,
    ): Substitution {
        ensuringArgumentIsStruct(0)
        ensuringArgumentIsList(1)
        return catchingOopExceptions {
            val type = getArgumentAsTypeRef(0)
            val arguments = (second as List).toArray()
            val objectReference = type?.create(termToObjectConverter, *arguments)?.asObjectRef()
            objectReference?.let { mgu(third, it) } ?: Substitution.failed()
        }
    }
}
