package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder

object SetPersistent : AbstractSetData("persistent") {
    override fun SideEffectsBuilder.setData(key: String, value: Term) {
        setPersistentData(key, value)
    }
}
