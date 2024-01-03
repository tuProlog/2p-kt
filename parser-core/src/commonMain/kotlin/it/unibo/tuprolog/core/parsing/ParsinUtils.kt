package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

internal val Term.isOpDirective: Boolean
    get() {
        return this is Struct &&
            arity == 3 &&
            functor == "op" &&
            this.isGround &&
            this[0] is Numeric &&
            this[1] is Atom
    }
