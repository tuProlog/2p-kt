package it.unibo.tuprolog.theory.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.collections.impl.Factories
import it.unibo.tuprolog.theory.theoryCreationTests

class ListedTheoryCreationSpec :
    FunSpec({
        include(
            theoryCreationTests(
                Factories::emptyListedTheory,
                Factories::listedTheoryOf,
                Factories::listedTheoryOf,
                Factories::listedTheoryOf,
                Factories::listedTheoryOf,
            ),
        )
    })
