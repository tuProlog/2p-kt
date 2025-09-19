package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.ObjectToTermConverter
import it.unibo.tuprolog.utils.NumberTypeTester

internal class ObjectToTermConverterImpl : ObjectToTermConverter {
    private val numberTypeTester = NumberTypeTester()

    override fun convert(source: Any?): Term =
        with(numberTypeTester) {
            when (source) {
                null -> ObjectRef.NULL
                is String -> Atom.of(source)
                is Number ->
                    when {
                        source.isInteger -> Integer.of(source.toInteger())
                        else -> Real.of(source.toDecimal())
                    }
                is Char -> Atom.of(charArrayOf(source).concatToString())
                is Boolean -> Truth.of(source)
                else -> ObjectRef.of(source)
            }
        }
}
