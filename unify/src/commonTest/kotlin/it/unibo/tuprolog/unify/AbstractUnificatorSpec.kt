package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.unify.testutils.ReferenceUnificators
import it.unibo.tuprolog.unify.testutils.UnificatorUtils
import it.unibo.tuprolog.unify.testutils.unificatorContract

/** The shared [Unificator] contract, against the equation-based [AbstractUnificator] strategies. */
class AbstractUnificatorSpec :
    FunSpec({
        include(unificatorContract("strict", ReferenceUnificators::strict))
        include(unificatorContract("naive", ReferenceUnificators::naive, disabled = UnificatorUtils.naiveDivergences))
    })
