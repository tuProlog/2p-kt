package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation

/** Caches whole-source analysis while RSTA asks for syntax tokens a line at a time. */
internal class PrologAnalysisCache(
    private val source: () -> String,
    private val operators: () -> List<OperatorPresentation>,
) {
    private var cachedKey: Pair<String, List<OperatorPresentation>>? = null
    private var cachedAnalysis: PrologAnalysis? = null

    fun analysis(): PrologAnalysis {
        val key = source() to operators()
        if (key != cachedKey) {
            cachedKey = key
            cachedAnalysis = PrologAnalyzer.analyze(key.first, key.second)
        }
        return checkNotNull(cachedAnalysis)
    }

    fun invalidate() {
        cachedKey = null
        cachedAnalysis = null
    }
}
