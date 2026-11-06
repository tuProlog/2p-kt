package it.unibo.tuprolog.theory.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.testutils.Factories
import it.unibo.tuprolog.theory.properIndexingTests

class IndexedClauseIndexingSpec :
    FunSpec({
        include(
            properIndexingTests(
                Factories::indexedTheoryOf,
            ),
        )
    })
