package it.unibo.tuprolog.ide.web.redux.reducers

import Message
import AppState
import TuProlog
import it.unibo.tuprolog.ide.web.redux.actions.NewSolution
import it.unibo.tuprolog.ide.web.redux.actions.UpdatePagesList
import it.unibo.tuprolog.ide.web.redux.actions.UpdateSelectedPage
import js.uri.encodeURIComponent
import mui.material.AlertColor
import redux.RAction
import web.dom.document
import web.html.HTML
import kotlin.js.Date


// TODO risolvere complessità ciclica della funzione
// TODO verificare se la dispatch è sincrona o asincrona
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
        state.tuProlog.solutions += action.solution
        state.tuProlog
    }

    else -> state.tuProlog
}