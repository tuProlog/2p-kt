package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopException
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import kotlin.reflect.KClass

internal data class TestDatum(
    val termId: String,
    val term: Term,
    val type: KClass<*>,
    val converted: Any?,
) {
    val id: String get() = "$termId/${type.simpleName!!.lowercase()}"

    val string: String get() = type.name

    constructor(termId: String, obj: Any?, type: KClass<*>) :
        this(termId, obj?.let { ObjectRef.of(obj) } ?: ObjectRef.NULL, type, obj)

    val exception by lazy { converted as? OopException }

    val isFailed: Boolean
        get() = converted == null || exception != null

    companion object {
        fun failed(
            termId: String,
            term: Term,
            type: KClass<*>,
        ): TestDatum = TestDatum(termId, term, type, TermToObjectConversionException(term, type))
    }
}
