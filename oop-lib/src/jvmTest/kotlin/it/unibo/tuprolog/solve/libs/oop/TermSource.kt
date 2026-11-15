package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import kotlin.reflect.KClass

/** A host object whose method returns a logic term. */
class TermSource {
    fun term(): Term = Struct.of("f", Atom.of("a"))

    fun typeName(type: KClass<*>): String = type.simpleName!!
}
