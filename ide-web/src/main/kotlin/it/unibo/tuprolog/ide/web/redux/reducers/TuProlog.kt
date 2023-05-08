package it.unibo.tuprolog.ide.web.redux.reducers

import Message
import AppState
import TuProlog
import it.unibo.tuprolog.ide.web.redux.actions.NewSolution
import it.unibo.tuprolog.ide.web.redux.actions.ResetPage
import it.unibo.tuprolog.ide.web.redux.actions.UpdatePagesList
import it.unibo.tuprolog.ide.web.redux.actions.UpdateSelectedPage
import it.unibo.tuprolog.ide.web.redux.actions.UpdateStatus
import js.uri.encodeURIComponent
import mui.material.AlertColor
import redux.RAction
import web.dom.document
import web.html.HTML
import kotlin.js.Date


// TODO risolvere complessità ciclica della funzione
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

    is ResetPage -> {
        state.tuProlog.solutions = emptyList()
        state.tuProlog
    }

    is UpdateStatus -> {
        state.tuProlog.pageStatus = action.newStatus
        state.tuProlog
    }


    else -> state.tuProlog
}