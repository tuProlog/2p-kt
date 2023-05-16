package it.unibo.tuprolog.ide.web.redux


import AppState
import PageWrapper
import it.unibo.tuprolog.ide.web.redux.actions.*
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ui.gui.Page
import redux.RAction

fun appActionsReducer(state: AppState, action: RAction): AppState =
    when (action) {
        is UpdatePagesList -> {
            state.pages = action.list.map {
                it to PageWrapper.fromPage(it)
            }.toMap()
            state
        }

        is UpdateSelectedPage -> {
            state.currentPage = action.page
            state
        }

        is NewSolution -> {
            state.solutions =
                listOf(action.solution) + state.solutions
            state
        }

        is UpdateExecutionContext -> {
            state.executionContext = action.context
            state
        }

        is ResetPage -> {
            state.solutions = emptyList()
            state
        }

        is UpdateStatus -> {
            state.pageStatus =
                TuPrologController.application.currentPage?.state
            state
        }

        is PageError -> {
            state.pageException = action.exception
            state
        }

        is CleanPageError -> {
            state.pageException = null
            state
        }

        is CleanSolutions -> {
            state.solutions = emptyList()
            state
        }

        is StdOut -> {
            state.stdOutMessage = action.output
            state
        }

        is StdErr -> {
            state.stdErrMessage = action.error
            state
        }

        is Warnings -> {
            state.warningMessage = action.warning
            state
        }

        else -> state

    }