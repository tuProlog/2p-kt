package it.unibo.tuprolog.collections.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.collections.prototypes.clauseMultiSetTests

class MutableReteClauseMultiSetSpec :
    FunSpec({
        include(clauseMultiSetTests(Factories::emptyClauseMultiset, Factories::mutableClauseMultisetOf))
    })
