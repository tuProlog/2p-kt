package it.unibo.tuprolog.ui.gui.presentation

import kotlin.test.Test
import kotlin.test.assertFailsWith

class PresentationInvariantsTest {
    @Test
    fun textPositionRejectsNegativeCoordinates() {
        assertFailsWith<IllegalArgumentException> { TextPosition(-1, 0, 0) }
        assertFailsWith<IllegalArgumentException> { TextPosition(0, -1, 0) }
        assertFailsWith<IllegalArgumentException> { TextPosition(0, 0, -1) }
    }

    @Test
    fun textRangeCannotEndBeforeItStarts() {
        assertFailsWith<IllegalArgumentException> {
            TextRange(TextPosition(5, 0, 5), TextPosition(2, 0, 2))
        }
    }
}
