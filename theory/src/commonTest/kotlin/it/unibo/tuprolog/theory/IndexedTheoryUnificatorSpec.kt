package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec

class IndexedTheoryUnificatorSpec :
    FunSpec({
        include(
            theoryUnificatorTests(
                Theory.Companion::indexedOf,
            ),
        )
    })
