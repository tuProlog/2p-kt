package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec

class MutableListedTheoryUnificatorSpec :
    FunSpec({
        include(
            theoryUnificatorTests(
                MutableTheory.Companion::listedOf,
            ),
        )
    })
