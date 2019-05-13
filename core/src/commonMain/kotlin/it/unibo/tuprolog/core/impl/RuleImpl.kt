package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

internal open class RuleImpl(override val head: Struct, override val body: Term)
    : ClauseImpl(head, body), Rule {

}

