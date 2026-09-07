package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.impl.ObjectToTermConverterImpl
import kotlin.jvm.JvmStatic

/**
 * Converts a JVM/Kotlin value into its Prolog [Term] representation -- the direction opposite to
 * [TermToObjectConverter] -- e.g. as needed to turn the return value of a reflectively invoked
 * method ([Ref.invoke]) back into something a Prolog computation can unify against.
 *
 * The [default] implementation maps `null` to [ObjectRef.NULL], [String] to [it.unibo.tuprolog.core.Atom],
 * any [Number] to [it.unibo.tuprolog.core.Integer] or [it.unibo.tuprolog.core.Real], single [Char]s to
 * one-letter atoms, [Boolean] to [it.unibo.tuprolog.core.Truth], and everything else to an
 * [ObjectRef] wrapping the value as-is.
 *
 * @see TermToObjectConverter
 */
interface ObjectToTermConverter {
    /** Converts [source] into its [Term] representation. */
    fun convert(source: Any?): Term

    companion object {
        /** The default [ObjectToTermConverter], also used internally by [Result.Value]. */
        @JvmStatic
        val default: ObjectToTermConverter = ObjectToTermConverterImpl()
    }
}
