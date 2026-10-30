package it.unibo.tuprolog.theory.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.collections.impl.Factories
import it.unibo.tuprolog.theory.theoryTests

class ListedTheorySpec :
    FunSpec({
        include(
            theoryTests(
                Factories::emptyListedTheory,
                Factories::listedTheoryOf,
            ),
        )
    })
