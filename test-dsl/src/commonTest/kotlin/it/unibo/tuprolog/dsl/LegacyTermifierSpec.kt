package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec

class LegacyTermifierSpec :
    FunSpec({
        include(legacyTermifierTests(Termificator::legacy))
    })
