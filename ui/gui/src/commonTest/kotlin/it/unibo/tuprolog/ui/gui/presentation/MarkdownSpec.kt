package it.unibo.tuprolog.ui.gui.presentation

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarkdownSpec :
    FunSpec({
        test("preview-skips-the-usage-line-and-strips-code-spans") {
            assertEquals(
                "Succeeds when Element is an element of List.",
                documentationPreview(
                    "`member(?Element, ?List)`\n\nSucceeds when `Element` is an element of `List`.\n\nMore.",
                ),
            )
        }

        test("preview-falls-back-to-the-only-paragraph-and-truncates") {
            assertEquals("`foo/0`".replace("`", ""), documentationPreview("`foo/0`"))
            assertEquals("abcd…", documentationPreview("abcdefgh", maxLength = 5))
            assertEquals("", documentationPreview(""))
        }

        test("renders-common-mark") {
            val html = markdownToHtml("`x`\n\n- item")
            assertTrue("<code>x</code>" in html, html)
            assertTrue("<li>" in html, html)
        }
    })
