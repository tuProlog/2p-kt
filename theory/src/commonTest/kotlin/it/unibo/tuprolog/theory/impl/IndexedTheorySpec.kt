package it.unibo.tuprolog.theory.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.testutils.Factories
import it.unibo.tuprolog.theory.theoryTests

class IndexedTheorySpec :
    FunSpec({
        include(
            theoryTests(
                Factories::emptyIndexedTheory,
                Factories::indexedTheoryOf,
            ),
        )
    })
