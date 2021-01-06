package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.libs.io.IOMode
import it.unibo.tuprolog.solve.libs.io.Url
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsAtom
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsInstantiated
import it.unibo.tuprolog.solve.primitive.Solve

object IOPrimitiveUtils {
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsUrl(index: Int): Url {
        ensuringArgumentIsInstantiated(index)
        val term = arguments[index]
        val error = DomainError.forArgument(context, signature, DomainError.Expected.SOURCE_SINK, term, index)
        if (term !is Atom) {
            throw error
        }
        try {
            return Url.of(term.value)
        } catch (_: InvalidUrlException) {
            throw error
        }
    }

    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsIOMode(index: Int): IOMode {
        ensuringArgumentIsInstantiated(index)
        ensuringArgumentIsAtom(index)
        val term = arguments[index] as Atom
        if (term !in IOMode.atomValues) {
            throw DomainError.forArgument(context, signature, DomainError.Expected.IO_MODE, term, index)
        }
        return IOMode.valueOf(term.value.toUpperCase())
    }
}
