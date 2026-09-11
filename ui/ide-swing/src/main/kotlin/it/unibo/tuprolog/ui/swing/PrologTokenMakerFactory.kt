package it.unibo.tuprolog.ui.swing

import org.fife.ui.rsyntaxtextarea.TokenMaker
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory

internal class PrologTokenMakerFactory(
    private val tokenMaker: TokenMaker,
) : TokenMakerFactory() {
    override fun getTokenMakerImpl(key: String): TokenMaker? = if (key == PROLOG_SYNTAX_STYLE) tokenMaker else null

    override fun keySet(): Set<String> = setOf(PROLOG_SYNTAX_STYLE)
}
