package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.name
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * `type(?TypeName, ?TypeRef)`: converts between a type-name [it.unibo.tuprolog.core.Atom]
 * `TypeName` (the type's simple name, see [it.unibo.tuprolog.solve.libs.oop.name]) and a
 * [it.unibo.tuprolog.solve.libs.oop.TypeRef] `TypeRef`, in either direction, using
 * [it.unibo.tuprolog.solve.libs.oop.TypeFactory.default] to resolve names to types.
 *
 * Fails (rather than throwing) if `TypeName` names a type that cannot be resolved.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if both arguments are unbound.
 */
object Type : BinaryRelation.Functional<ExecutionContext>("type") {
    override val help: String =
        """
        `type(?TypeName, ?TypeRef)`
        
        Converts between an atom `TypeName` and a type reference `TypeRef` to the class it names. Given an atom holding a fully qualified type name (e.g. `'java.lang.String'`), unifies `TypeRef` with a reference to that class, failing if no such class exists; given a type reference, unifies `TypeName` with the name of its class. Deterministic. Raises an instantiation error if both arguments are unbound and a type error if the bound argument has the wrong shape.

        **Examples**

        ```prolog
        ?- type('java.util.ArrayList', T), new_object(T, [], L), N := L.size.
        N = 0.

        ?- type('java.util.ArrayList', T), type(Name, T).
        Name = 'ArrayList'.

        ?- type('no.such.Type', T).
        no.
        ```
        """.trimIndent()

    private val typeFactory = TypeFactory.default

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution =
        catchingOopExceptions {
            when {
                first is Var && second is Var -> {
                    ensuringArgumentIsInstantiated(0)
                    Substitution.failed()
                }
                first is Var -> {
                    ensuringArgumentIsAtom(1)
                    ensuringArgumentIsTypeRef(1)
                    mgu(first, Atom.of((second as TypeRef).type.name))
                }
                second is Var -> {
                    ensuringArgumentIsAtom(0)
                    typeFactory.typeRefFromName((first as Atom).value)?.let { mgu(second, it) } ?: Substitution.failed()
                }
                else -> {
                    ensuringArgumentIsAtom(0)
                    Substitution.failed()
                }
            }
        }
}
