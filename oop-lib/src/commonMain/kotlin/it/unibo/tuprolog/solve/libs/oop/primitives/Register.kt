package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * `register(+Ref, +Alias)`: registers `Ref` -- an [it.unibo.tuprolog.solve.libs.oop.ObjectRef] or
 * [it.unibo.tuprolog.solve.libs.oop.TypeRef] -- under the ground alias term `Alias`, by adding a
 * matching [it.unibo.tuprolog.solve.libs.oop.rules.Alias] fact to the current solver's static
 * knowledge base. Once registered, `Ref` can be referred to as `$Alias` anywhere a reference is
 * expected. See [Unregister] for removing an alias again.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if `Ref` or `Alias` is
 * unbound, or `Alias` is not ground.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if `Ref` is not a
 * [it.unibo.tuprolog.solve.libs.oop.Ref], or `Alias` is not a [it.unibo.tuprolog.core.Struct].
 */
object Register : BinaryRelation.NonBacktrackable<ExecutionContext>("register") {
    override val help: String =
        """
        `register(+Ref, +Alias)`
        
        Registers the object or type reference `Ref` under the ground term `Alias`, by adding an `alias(Alias, Ref)` fact to the static knowledge base. Afterwards `${'$'}Alias` can be used wherever a reference is expected, e.g. as the receiver of a method call. Registering an already-used alias adds another fact, so the earliest registration keeps taking precedence until removed with `unregister/1`. Raises an instantiation error if an argument is unbound or `Alias` is not ground, and a type error if `Ref` is not a reference or `Alias` is not callable. Example: `new_object('java.util.ArrayList', [], L), register(L, my_list), ${'$'}my_list.add(1)`.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        ensuringAllArgumentsAreInstantiated()
        ensuringArgumentIsRef(0)
        ensuringArgumentIsStruct(1)
        if (!second.isGround) {
            throw InstantiationError.forArgument(context, signature, second.variables.first(), 0)
        }
        return replySuccess {
            addStaticClauses(Alias.of(second as Struct, first as Ref).implementation)
        }
    }
}
