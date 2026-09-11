package it.unibo.tuprolog.ui.swing

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.Style
import org.fife.ui.rsyntaxtextarea.TokenTypes
import java.awt.Color
import java.awt.Font

internal fun RSyntaxTextArea.configurePrologSyntaxScheme() {
    val plain = Font(Font.MONOSPACED, Font.PLAIN, 14)
    val bold = plain.deriveFont(Font.BOLD)
    syntaxScheme.setStyle(TokenTypes.COMMENT_EOL, Style(Color(0x6A, 0x73, 0x7D), null, plain.deriveFont(Font.ITALIC)))
    syntaxScheme.setStyle(
        TokenTypes.COMMENT_MULTILINE,
        Style(Color(0x6A, 0x73, 0x7D), null, plain.deriveFont(Font.ITALIC)),
    )
    syntaxScheme.setStyle(TokenTypes.OPERATOR, Style(Color(0xA6, 0x26, 0xA4), null, bold))
    syntaxScheme.setStyle(TokenTypes.SEPARATOR, Style(Color(0x55, 0x55, 0x55), null, bold))
    syntaxScheme.setStyle(TokenTypes.FUNCTION, Style(Color(0x00, 0x5C, 0x99), null, bold))
    syntaxScheme.setStyle(TokenTypes.IDENTIFIER, Style(Color(0x00, 0x66, 0x00), null, plain))
    syntaxScheme.setStyle(TokenTypes.VARIABLE, Style(Color(0x9A, 0x67, 0x00), null, plain))
    syntaxScheme.setStyle(TokenTypes.LITERAL_NUMBER_DECIMAL_INT, Style(Color(0x00, 0x55, 0xAA), null, plain))
    syntaxScheme.setStyle(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, Style(Color(0xA3, 0x15, 0x15), null, plain))
}
