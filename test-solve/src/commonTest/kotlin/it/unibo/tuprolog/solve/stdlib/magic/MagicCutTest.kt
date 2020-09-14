package it.unibo.tuprolog.solve.stdlib.magic

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MagicCutTest {
    @Test
    fun testMagicCutRepresentation() {
        assertTrue {
            MagicCut.toString() == MagicCut.FUNCTOR
        }
    }

    @Test
    fun testMagicCutCannotBeCreated() {
        assertTrue {
            Atom.of("!") !is MagicCut
        }
        assertTrue {
            Atom.of("¡") !is MagicCut
        }
    }

    @Test
    fun testMagicCutEqualityWithOrdinaryCut() {
        assertTrue {
            Atom.of("!") == MagicCut
        }
        assertTrue {
            MagicCut == Atom.of("!")
        }
        assertEquals(Atom.of("!").hashCode(), MagicCut.hashCode())
    }

    @Test
    fun testCopingMagicCutPreservesIs() {
        assertEquals(MagicCut, MagicCut.freshCopy())
        assertEquals(MagicCut, MagicCut.freshCopy(Scope.empty()))
        assertEquals(MagicCut, MagicCut.freshCopy(Scope.of(Var.anonymous())))
        assertEquals(MagicCut, MagicCut.freshCopy(Scope.of(Var.anonymous(), Var.anonymous())))
    }
}
