package it.unibo.tuprolog.libraries.stdlib.magic

import it.unibo.tuprolog.core.Atom

object MagicCut : Atom by Atom.of("!") {
    override val isConstant: Boolean
        get() = super<Atom>.isConstant


}