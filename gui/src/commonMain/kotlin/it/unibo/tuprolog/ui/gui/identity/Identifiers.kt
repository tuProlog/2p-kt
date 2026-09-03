package it.unibo.tuprolog.ui.gui.identity

/** Stable application-level identity. It is intentionally unrelated to a filesystem path. */
data class DocumentId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "DocumentId cannot be blank" }
    }

    override fun toString(): String = value
}

/** Stable identity of an interactive page/session. A page and a document are distinct concepts. */
data class PageId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "PageId cannot be blank" }
    }

    override fun toString(): String = value
}

data class SolverProfileId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "SolverProfileId cannot be blank" }
    }

    override fun toString(): String = value
}

data class SolverSessionId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "SolverSessionId cannot be blank" }
    }

    override fun toString(): String = value
}

data class ResolutionSessionId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "ResolutionSessionId cannot be blank" }
    }

    override fun toString(): String = value
}

data class EffectId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "EffectId cannot be blank" }
    }

    override fun toString(): String = value
}

data class ExtensionId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "ExtensionId cannot be blank" }
    }

    override fun toString(): String = value
}

data class FeatureId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "FeatureId cannot be blank" }
    }

    override fun toString(): String = value
}

data class CommandId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "CommandId cannot be blank" }
    }

    override fun toString(): String = value
}
