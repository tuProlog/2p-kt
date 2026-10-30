package it.unibo.tuprolog.theory.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.collections.impl.Factories
import it.unibo.tuprolog.theory.theoryCreationTests

class IndexedTheoryCreationSpec :
    FunSpec({
        include(
            theoryCreationTests(
                Factories::emptyIndexedTheory,
                Factories::indexedTheoryOf,
                Factories::indexedTheoryOf,
                Factories::indexedTheoryOf,
                Factories::indexedTheoryOf,
            ),
        )
    })
