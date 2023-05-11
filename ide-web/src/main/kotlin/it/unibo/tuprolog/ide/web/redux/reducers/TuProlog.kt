package it.unibo.tuprolog.ide.web.redux.reducers

import AppState
import TuProlog
import it.unibo.tuprolog.ide.web.redux.actions.*
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ui.gui.InQuerySyntaxError
import it.unibo.tuprolog.ui.gui.InTheorySyntaxError
import mui.system.StackDirection
import redux.RAction

fun tuPrologActions(state: AppState, action: RAction): TuProlog = when (action) {
    is UpdatePagesList -> {
        state.tuProlog.pages = action.list.toMutableList()
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

    is StdOut -> {
        state.tuProlog.stdOutMessage = action.out
        state.tuProlog
    }

    is StdErr -> {
        state.tuProlog.stdErrMessage = action.err
        state.tuProlog
    }

    is Warnings -> {
        state.tuProlog.warningMessage = action.warn
        state.tuProlog
    }

    else -> state.tuProlog
}