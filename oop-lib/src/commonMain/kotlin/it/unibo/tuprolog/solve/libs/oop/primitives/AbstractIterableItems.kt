package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.ObjectToTermConverter
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import kotlin.reflect.KClass

/**
 * The shared implementation of `${iterable}_items/2`, converting between a Prolog [List] of items
 * and a JVM/Kotlin collection type `T` wrapped in an [ObjectRef] -- letting Prolog code build a
 * `java.util.ArrayList`/array/`java.util.LinkedHashSet` (see [ArrayItems], [ListItems],
 * [SetItems]) from a logic list, or read one back out of an [ObjectRef] as a logic list, without
 * going through `new_object/3` and repeated `invoke_method/3` calls.
 *
 * Bidirectional: given a logic [List] as the second argument, unifies the first with a fresh
 * [ObjectRef] wrapping a `T` built from its (converted) items; given an [ObjectRef] wrapping a `T`
 * as the first argument, unifies the second with the logic list of its items (each converted back
 * to a [Term] via [ObjectToTermConverter]).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if the first argument is neither an
 * unbound variable nor an [ObjectRef] wrapping a `T`.
 */
abstract class AbstractIterableItems<T : Any>(
    iterable: String,
    private val target: KClass<T>,
) : BinaryRelation.Functional<ExecutionContext>("${iterable}_items") {
    /** Builds a `T` out of this sequence of already-converted items. */
    protected abstract fun Sequence<Any?>.toIterable(): T

    /** Whether this value is (dynamically) a `T` this primitive knows how to read items from. */
    protected abstract val Any?.isIterable: Boolean

    /** This `T`'s items, in iteration order. */
    protected abstract val T.items: Sequence<Any?>

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution =
        when {
            first is Var && second is Var ->
                ensuringAllArgumentsAreInstantiated().let { Substitution.failed() }
            second is List -> {
                val converter = termToObjectConverter
                val items = second.toSequence().map { converter.convert(it) }.toIterable()
                val objectRef = ObjectRef.of(items)
                mgu(first, objectRef)
            }
            first is ObjectRef -> {
                val obj = first.`object`
                if (obj.isIterable) {
                    @Suppress("UNCHECKED_CAST")
                    val items =
                        (obj as T).items.map {
                            ObjectToTermConverter.default.convert(it)
                        }
                    mgu(second, List.of(items))
                } else {
                    Substitution.failed()
                }
            }
            else -> {
                throw TypeError.forArgument(context, signature, TypeError.Expected.OBJECT_REFERENCE, first, 0)
            }
        }
}
