package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.presentation.SemanticToken

data class PageState(
    val id: PageId,
    val title: String,
    val content: PageContent,
    val query: QueryState = QueryState(),
    val configuration: PageConfiguration = PageConfiguration(),
    val solverSession: SolverSessionState = SolverSessionState(),
    val resolution: ResolutionState = ResolutionState(),
    val history: PageHistoryState = PageHistoryState(),
    val console: ConsoleState = ConsoleState(),
    val diagnostics: DiagnosticState = DiagnosticState(),
    val semanticTokens: List<SemanticToken> = emptyList(),
    val features: Map<FeatureId, PageFeatureState> = emptyMap(),
)
