package it.unibo.tuprolog.theory.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.collections.impl.Factories
import it.unibo.tuprolog.theory.theoryTests

class MutableListedTheorySpec :
    FunSpec({
        include(
            theoryTests(
                Factories::emptyMutableListedTheory,
                Factories::mutableListedTheoryOf,
            ),
        )
    })
