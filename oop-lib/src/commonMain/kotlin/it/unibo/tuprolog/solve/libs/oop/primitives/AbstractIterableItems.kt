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

abstract class AbstractIterableItems<T : Any>(iterable: String, private val target: KClass<T>) :
    BinaryRelation.Functional<ExecutionContext>("${iterable}_items") {

    protected abstract fun Sequence<Any?>.toIterable(): T

    protected abstract val Any?.isIterable: Boolean

    protected abstract val T.items: Sequence<Any?>

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(first: Term, second: Term): Substitution =
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
                    val items = (obj as T).items.map {
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
