package it.unibo.tuprolog.ide.web.redux

import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import redux.RAction

fun tuPrologReducer(state: TuProlog, action: RAction): TuProlog =
    when (action) {
        is AddPage -> {
            state.pages += (action.page to PageWrapper.fromPage(action.page))
            state
        }
        is RemovePage -> {
            state.pages.remove(action.page)
            state
        }
        is UpdatePageName -> {
            val currPage = TuPrologController.application.pageByID(action.new)
            state.pages[currPage]?.id = action.new
            state
        }

        is UpdateSelectedPage -> {
            state.currentPage =  state.pages[action.page]
            state
        }

        is UpdateTheory -> {
            state.currentPage!!.theory = action.newTheory
            state
        }

        is UpdateQuery -> {
            state.currentPage!!.query = action.newQuery
            state
        }

        is NewSolution -> {
            state.currentPage!!.solutions =
                listOf(action.solution) + state.currentPage!!.solutions
            state
        }

        is UpdateExecutionContext -> {
            state.currentPage!!.executionContext = action.context
            state
        }

        is ResetPage -> {
            state.currentPage!!.solutions = emptyList()
            state
        }

        is UpdateStatus -> {
            state.currentPage!!.pageStatus =
                TuPrologController.application.currentPage?.state
            state
        }

        is PageError -> {
            state.currentPage!!.pageException = action.exception
            state
        }

        is CleanPageError -> {
            state.currentPage!!.pageException = null
            state
        }

        is CleanSolutions -> {
            state.currentPage!!.solutions = emptyList()
            state
        }

        is StdOut -> {
            state.currentPage!!.stdOutMessage = action.output
            state
        }

        is StdErr -> {
            state.currentPage!!.stdErrMessage = action.error
            state
        }

        is Warnings -> {
            state.currentPage!!.warningMessage = action.warning
            state
        }

        else -> state

    }