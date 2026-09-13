package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId

/** Everything a `SolverSessionFactory` needs to build the `SolverSession` backing one page. */
data class SolverSessionCreationRequest(
    val pageId: PageId,
    val documentId: DocumentId?,
    val sourceText: String,
    val documentRevision: Long,
    val profileId: SolverProfileId,
    val options: Map<String, String>,
    val stdin: String,
)
