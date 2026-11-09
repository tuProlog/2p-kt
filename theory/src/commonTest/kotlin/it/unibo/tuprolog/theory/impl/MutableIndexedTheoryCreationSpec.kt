package it.unibo.tuprolog.theory.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.testutils.Factories
import it.unibo.tuprolog.theory.theoryCreationTests

class MutableIndexedTheoryCreationSpec :
    FunSpec({
        include(
            theoryCreationTests(
                Factories::emptyMutableIndexedTheory,
                Factories::mutableIndexedTheoryOf,
                Factories::mutableIndexedTheoryOf,
                Factories::mutableIndexedTheoryOf,
                Factories::mutableIndexedTheoryOf,
            ),
        )
    })
