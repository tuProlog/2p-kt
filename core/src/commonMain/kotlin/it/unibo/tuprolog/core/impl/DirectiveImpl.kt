package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

internal class DirectiveImpl(override val body: Term) : ClauseImpl(null, body), Directive {

    override val head: Struct? = super<Directive>.head
}
