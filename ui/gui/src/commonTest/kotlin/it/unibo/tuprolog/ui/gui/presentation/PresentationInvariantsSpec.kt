package it.unibo.tuprolog.ui.gui.presentation

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class PresentationInvariantsSpec :
    FunSpec({
        test("text-position-rejects-negative-coordinates") {
            shouldThrow<IllegalArgumentException> { TextPosition(-1, 0, 0) }
            shouldThrow<IllegalArgumentException> { TextPosition(0, -1, 0) }
            shouldThrow<IllegalArgumentException> { TextPosition(0, 0, -1) }
        }

        test("text-range-cannot-end-before-it-starts") {
            shouldThrow<IllegalArgumentException> {
                TextRange(TextPosition(5, 0, 5), TextPosition(2, 0, 2))
            }
        }
    })
