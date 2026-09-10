package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import it.unibo.tuprolog.ui.gui.presentation.SemanticCategory
import it.unibo.tuprolog.ui.gui.presentation.SemanticToken
import it.unibo.tuprolog.ui.gui.presentation.TextPosition
import it.unibo.tuprolog.ui.gui.presentation.TextRange
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AceEditorViewTest {
    private fun withEditor(block: (AceEditorView) -> Unit) {
        val container = document.createElement("div") as HTMLElement
        // Ace only renders rows into elements with a real size; a bare appended div collapses to 0 height.
        container.style.width = "600px"
        container.style.height = "300px"
        document.body?.appendChild(container)
        val editor = AceEditorView(container)
        try {
            block(editor)
        } finally {
            container.parentNode?.removeChild(container)
        }
    }

    @Test
    fun `a real Ace instance mounts and echoes back the text it is given`() {
        withEditor { editor ->
            editor.value = "p(1).\np(2)."
            assertEquals("p(1).\np(2).", editor.value)
        }
    }

    @Test
    fun `a freshly mounted editor is not focused and accepts diagnostics without throwing`() {
        withEditor { editor ->
            assertFalse(editor.isFocused)
            editor.setDiagnostics(
                listOf(
                    Diagnostic(
                        DiagnosticSeverity.ERROR,
                        "syntax error",
                        TextRange(TextPosition(0, 0, 0), TextPosition(1, 0, 1)),
                    ),
                ),
            )
            editor.setDiagnostics(emptyList())
            editor.resize()
        }
    }

    @Test
    fun `semantic tokens are rendered as classified spans in the real DOM`() {
        withEditor { editor ->
            editor.value = "% comment"
            editor.setSemanticTokens(
                listOf(
                    SemanticToken(
                        TextRange(TextPosition(0, 0, 0), TextPosition(9, 0, 9)),
                        SemanticCategory.COMMENT,
                    ),
                ),
            )
            editor.resize()
            val container = document.getElementsByClassName("ace_comment")
            assertTrue(container.length > 0, "expected an 'ace_comment' span to be rendered")
            assertEquals("% comment", container.item(0)?.textContent)
        }
    }

    @Test
    fun `typing through Ace's own insert pipeline does not throw`() {
        withEditor { editor ->
            editor.testType("p(X) :- q(X).")
            assertEquals("p(X) :- q(X).", editor.value)
        }
    }
}
