package it.unibo.tuprolog.ui.gui.presentation

import it.unibo.tuprolog.ui.gui.identity.FeatureId

data class TextPosition(
    val offset: Int,
    val line: Int,
    val column: Int,
) {
    init {
        require(offset >= 0) { "offset must be non-negative" }
        require(line >= 0) { "line must be non-negative" }
        require(column >= 0) { "column must be non-negative" }
    }
}

data class TextRange(
    val start: TextPosition,
    val endExclusive: TextPosition,
) {
    init {
        require(start.offset <= endExclusive.offset) { "range start must not follow its end" }
    }
}

enum class DiagnosticSeverity {
    INFO,
    WARNING,
    ERROR,
}

data class DiagnosticSource(
    val value: String,
)

data class Diagnostic(
    val severity: DiagnosticSeverity,
    val message: String,
    val range: TextRange? = null,
    val source: DiagnosticSource = DiagnosticSource("gui"),
    val code: String? = null,
)

enum class SemanticCategory {
    COMMENT,
    OPERATOR,
    PARENTHESIS,
    BRACE,
    BRACKET,
    FUNCTOR,
    ATOM,
    VARIABLE,
    NUMBER,
    STRING,
    FULL_STOP,
    DIRECTIVE,
    ERROR,
}

data class SemanticToken(
    val range: TextRange,
    val category: SemanticCategory,
    val modifiers: Set<String> = emptySet(),
)

fun interface SyntaxHighlightingService {
    suspend fun classify(
        source: String,
        operators: List<OperatorPresentation>,
    ): List<SemanticToken>
}

data class BindingPresentation(
    val variable: String,
    val value: String,
)

sealed interface SolutionPresentation {
    val query: String

    data class Yes(
        override val query: String,
        val bindings: List<BindingPresentation> = emptyList(),
        val solvedQuery: String? = null,
        val metadata: Map<String, String> = emptyMap(),
    ) : SolutionPresentation

    data class No(
        override val query: String,
    ) : SolutionPresentation

    data class Halt(
        override val query: String,
        val message: String,
        val logicStackTrace: List<String> = emptyList(),
        val isTimeout: Boolean = false,
    ) : SolutionPresentation
}

data class WarningPresentation(
    val message: String,
    val logicStackTrace: List<String> = emptyList(),
)

data class OperatorPresentation(
    val name: String,
    val priority: Int,
    val specifier: String,
)

data class FlagPresentation(
    val name: String,
    val value: String,
)

data class LibraryPresentation(
    val alias: String,
    val predicates: List<String> = emptyList(),
    val operators: List<OperatorPresentation> = emptyList(),
    val functions: List<String> = emptyList(),
)

data class SolverInspectionSnapshot(
    val operators: List<OperatorPresentation> = emptyList(),
    val flags: List<FlagPresentation> = emptyList(),
    val libraries: List<LibraryPresentation> = emptyList(),
    val staticKnowledgeBase: String = "",
    val dynamicKnowledgeBase: String = "",
)

enum class SemanticRegion {
    EDITOR,
    QUERY,
    RESULTS,
    INSPECTOR,
    CONSOLE,
    SIDEBAR,
    STATUS,
}

data class FeaturePlacement(
    val region: SemanticRegion,
    val priority: Int = 0,
)

data class FeatureDescriptor(
    val id: FeatureId,
    val displayName: String,
    val placement: FeaturePlacement,
    val requiredCapabilities: Set<String> = emptySet(),
)
