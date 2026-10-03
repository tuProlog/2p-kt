package it.unibo.tuprolog.ui.gui.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarkdownTest {
    @Test
    fun previewSkipsTheUsageLineAndStripsCodeSpans() {
        assertEquals(
            "Succeeds when Element is an element of List.",
            documentationPreview(
                "`member(?Element, ?List)`\n\nSucceeds when `Element` is an element of `List`.\n\nMore.",
            ),
        )
    }

    @Test
    fun previewFallsBackToTheOnlyParagraphAndTruncates() {
        assertEquals("`foo/0`".replace("`", ""), documentationPreview("`foo/0`"))
        assertEquals("abcd…", documentationPreview("abcdefgh", maxLength = 5))
        assertEquals("", documentationPreview(""))
    }

    @Test
    fun rendersCommonMark() {
        val html = markdownToHtml("`x`\n\n- item")
        assertTrue("<code>x</code>" in html, html)
        assertTrue("<li>" in html, html)
    }
}
