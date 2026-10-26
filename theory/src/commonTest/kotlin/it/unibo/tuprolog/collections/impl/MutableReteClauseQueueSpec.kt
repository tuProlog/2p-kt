package it.unibo.tuprolog.collections.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.collections.prototypes.clauseQueueTests

class MutableReteClauseQueueSpec :
    FunSpec({
        include(clauseQueueTests(Factories::emptyClauseQueue, Factories::clauseQueueOf))
    })
