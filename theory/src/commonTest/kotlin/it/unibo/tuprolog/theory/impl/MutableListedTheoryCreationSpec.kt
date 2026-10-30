package it.unibo.tuprolog.theory.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.collections.impl.Factories
import it.unibo.tuprolog.theory.theoryCreationTests

class MutableListedTheoryCreationSpec :
    FunSpec({
        include(
            theoryCreationTests(
                Factories::emptyMutableListedTheory,
                Factories::mutableListedTheoryOf,
                Factories::mutableListedTheoryOf,
                Factories::mutableListedTheoryOf,
                Factories::mutableListedTheoryOf,
            ),
        )
    })
