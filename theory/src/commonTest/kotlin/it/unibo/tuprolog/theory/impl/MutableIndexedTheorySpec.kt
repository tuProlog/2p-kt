package it.unibo.tuprolog.theory.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.testutils.Factories
import it.unibo.tuprolog.theory.theoryTests

class MutableIndexedTheorySpec :
    FunSpec({
        include(
            theoryTests(
                Factories::emptyMutableIndexedTheory,
                Factories::mutableIndexedTheoryOf,
            ),
        )
    })
