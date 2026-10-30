package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec

class ListedTheoryUnificatorSpec :
    FunSpec({
        include(
            theoryUnificatorTests(
                Theory.Companion::listedOf,
            ),
        )
    })
