package it.unibo.tuprolog.ui.gui.presentation

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertFailsWith

class PresentationInvariantsSpec :
    FunSpec({
        test("text-position-rejects-negative-coordinates") {
            assertFailsWith<IllegalArgumentException> { TextPosition(-1, 0, 0) }
            assertFailsWith<IllegalArgumentException> { TextPosition(0, -1, 0) }
            assertFailsWith<IllegalArgumentException> { TextPosition(0, 0, -1) }
        }

        test("text-range-cannot-end-before-it-starts") {
            assertFailsWith<IllegalArgumentException> {
                TextRange(TextPosition(5, 0, 5), TextPosition(2, 0, 2))
            }
        }
    })
