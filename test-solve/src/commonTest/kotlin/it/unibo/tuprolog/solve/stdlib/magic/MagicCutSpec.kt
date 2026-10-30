package it.unibo.tuprolog.solve.stdlib.magic

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MagicCutSpec :
    FunSpec({
        test("magic-cut-representation") {
            assertTrue {
                MagicCut.toString() == MagicCut.FUNCTOR
            }
        }

        test("magic-cut-cannot-be-created") {
            assertTrue {
                Atom.of("!") !is MagicCut
            }
            assertTrue {
                Atom.of("¡") !is MagicCut
            }
        }

        test("magic-cut-equality-with-ordinary-cut") {
            assertTrue {
                Atom.of("!") == MagicCut
            }
            assertTrue {
                MagicCut == Atom.of("!")
            }
            assertEquals(Atom.of("!").hashCode(), MagicCut.hashCode())
        }

        test("coping-magic-cut-preserves-is") {
            assertEquals(MagicCut, MagicCut.freshCopy())
            assertEquals(MagicCut, MagicCut.freshCopy(Scope.empty()))
            assertEquals(MagicCut, MagicCut.freshCopy(Scope.of(Var.anonymous())))
            assertEquals(MagicCut, MagicCut.freshCopy(Scope.of(Var.anonymous(), Var.anonymous())))
        }
    })
