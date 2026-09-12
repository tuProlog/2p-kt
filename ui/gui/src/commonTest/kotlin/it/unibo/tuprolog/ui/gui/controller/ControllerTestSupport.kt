package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.PageState
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout

internal val testProfileId = SolverProfileId("test")

internal suspend fun GuiController.awaitPage(
    pageId: PageId,
    predicate: (PageState) -> Boolean,
): PageState =
    withTimeout(5_000) {
        state
            .first { snapshot ->
                snapshot.workspace.page(pageId)?.let(predicate) == true
            }.workspace
            .page(pageId)!!
    }

internal suspend fun GuiController.awaitResolution(
    pageId: PageId,
    status: ResolutionStatus,
): PageState = awaitPage(pageId) { it.resolution.status == status }
