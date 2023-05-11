package it.unibo.tuprolog.ide.web.redux.reducers

import AppState
import TuProlog
import it.unibo.tuprolog.ide.web.redux.actions.CleanPageError
import it.unibo.tuprolog.ide.web.redux.actions.NewSolution
import it.unibo.tuprolog.ide.web.redux.actions.PageError
import it.unibo.tuprolog.ide.web.redux.actions.ResetPage
import it.unibo.tuprolog.ide.web.redux.actions.UpdateExecutionContext
import it.unibo.tuprolog.ide.web.redux.actions.UpdatePagesList
import it.unibo.tuprolog.ide.web.redux.actions.UpdateSelectedPage
import it.unibo.tuprolog.ide.web.redux.actions.UpdateStatus
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ui.gui.InQuerySyntaxError
import it.unibo.tuprolog.ui.gui.InTheorySyntaxError
import redux.RAction

fun tuPrologActions(state: AppState, action: RAction): TuProlog = when (action) {
    is UpdatePagesList -> {
        state.tuProlog.pages = action.list
        state.tuProlog
    }

    is UpdateSelectedPage -> {
        state.tuProlog.currentPage = action.page
        state.tuProlog
    }

    is NewSolution -> {
        state.tuProlog.solutions = listOf(action.solution) + state.tuProlog.solutions
        state.tuProlog
    }

    is UpdateExecutionContext -> {
        state.tuProlog.executionContext = action.context
        state.tuProlog
    }

    is ResetPage -> {
        state.tuProlog.solutions = emptyList()
        state.tuProlog
    }

    is UpdateStatus -> {
        state.tuProlog.pageStatus = TuPrologController.application.currentPage?.state
        state.tuProlog
    }

    is PageError -> {
        state.tuProlog.pageException = action.exception
        state.tuProlog
    }

    is CleanPageError -> {
        state.tuProlog.pageException = null
        state.tuProlog
    }

    else -> state.tuProlog
}