package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.libs.oop.NullRef

internal object NullRefImpl : NullRef, Atom by Atom.of(NullRef.NULL_FUNCTOR)