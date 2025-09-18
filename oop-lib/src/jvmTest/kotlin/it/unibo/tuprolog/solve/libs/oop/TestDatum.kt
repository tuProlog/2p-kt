package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopException
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import kotlin.reflect.KClass

internal data class TestDatum(
    val term: Term,
    val type: KClass<*>,
    val converted: Any?,
) {
    val string: String get() = type.name

    constructor(obj: Any?, type: KClass<*>) :
        this(obj?.let { ObjectRef.of(obj) } ?: ObjectRef.NULL, type, obj)

    val exception by lazy { converted as? OopException }

    val isFailed: Boolean
        get() = converted == null || exception != null

    companion object {
        fun failed(
            term: Term,
            type: KClass<*>,
        ): TestDatum = TestDatum(term, type, TermToObjectConversionException(term, type))
    }
}
