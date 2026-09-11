package it.unibo.tuprolog.ui.swing

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.Style
import org.fife.ui.rsyntaxtextarea.TokenTypes
import java.awt.Color
import java.awt.Font

internal fun RSyntaxTextArea.configurePrologSyntaxScheme() {
    val plain = Font(Font.MONOSPACED, Font.PLAIN, PrologSyntaxColors.FONT_SIZE)
    val bold = plain.deriveFont(Font.BOLD)
    syntaxScheme.setStyle(
        TokenTypes.COMMENT_EOL,
        Style(PrologSyntaxColors.COMMENT, null, plain.deriveFont(Font.ITALIC)),
    )
    syntaxScheme.setStyle(
        TokenTypes.COMMENT_MULTILINE,
        Style(PrologSyntaxColors.COMMENT, null, plain.deriveFont(Font.ITALIC)),
    )
    syntaxScheme.setStyle(TokenTypes.OPERATOR, Style(PrologSyntaxColors.OPERATOR, null, bold))
    syntaxScheme.setStyle(TokenTypes.SEPARATOR, Style(PrologSyntaxColors.SEPARATOR, null, bold))
    syntaxScheme.setStyle(TokenTypes.FUNCTION, Style(PrologSyntaxColors.FUNCTION, null, bold))
    syntaxScheme.setStyle(TokenTypes.IDENTIFIER, Style(PrologSyntaxColors.IDENTIFIER, null, plain))
    syntaxScheme.setStyle(TokenTypes.VARIABLE, Style(PrologSyntaxColors.VARIABLE, null, plain))
    syntaxScheme.setStyle(TokenTypes.LITERAL_NUMBER_DECIMAL_INT, Style(PrologSyntaxColors.NUMBER, null, plain))
    syntaxScheme.setStyle(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, Style(PrologSyntaxColors.STRING, null, plain))
}

/** Holds the syntax palette in a companion object, the one place a property's RGB literals aren't magic numbers. */
private class PrologSyntaxColors private constructor() {
    companion object {
        const val FONT_SIZE = 14
        val COMMENT = Color(0x6A, 0x73, 0x7D)
        val OPERATOR = Color(0xA6, 0x26, 0xA4)
        val SEPARATOR = Color(0x55, 0x55, 0x55)
        val FUNCTION = Color(0x00, 0x5C, 0x99)
        val IDENTIFIER = Color(0x00, 0x66, 0x00)
        val VARIABLE = Color(0x9A, 0x67, 0x00)
        val NUMBER = Color(0x00, 0x55, 0xAA)
        val STRING = Color(0xA3, 0x15, 0x15)
    }
}
