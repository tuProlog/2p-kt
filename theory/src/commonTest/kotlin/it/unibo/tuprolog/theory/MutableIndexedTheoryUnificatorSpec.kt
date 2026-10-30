package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec

class MutableIndexedTheoryUnificatorSpec :
    FunSpec({
        include(
            theoryUnificatorTests(
                MutableTheory.Companion::indexedOf,
            ),
        )
    })
