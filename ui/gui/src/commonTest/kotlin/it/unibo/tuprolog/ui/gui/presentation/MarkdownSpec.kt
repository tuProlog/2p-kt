package it.unibo.tuprolog.ui.gui.presentation

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class MarkdownSpec :
    FunSpec({
        test("preview-skips-the-usage-line-and-strips-code-spans") {
            documentationPreview(
                "`member(?Element, ?List)`\n\nSucceeds when `Element` is an element of `List`.\n\nMore.",
            ) shouldBe "Succeeds when Element is an element of List."
        }

        test("preview-falls-back-to-the-only-paragraph-and-truncates") {
            documentationPreview("`foo/0`") shouldBe "`foo/0`".replace("`", "")
            documentationPreview("abcdefgh", maxLength = 5) shouldBe "abcd…"
            documentationPreview("") shouldBe ""
        }

        test("renders-common-mark") {
            val html = markdownToHtml("`x`\n\n- item")
            withClue(html) { ("<code>x</code>" in html).shouldBeTrue() }
            withClue(html) { ("<li>" in html).shouldBeTrue() }
        }
    })
