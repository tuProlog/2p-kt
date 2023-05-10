package it.unibo.tuprolog.ide.web.tuprolog

import AppState
import it.unibo.tuprolog.ide.web.redux.actions.NewSolution
import it.unibo.tuprolog.ide.web.redux.actions.PageError
import it.unibo.tuprolog.ide.web.redux.actions.ResetPage
import it.unibo.tuprolog.ide.web.redux.actions.UpdatePagesList
import it.unibo.tuprolog.ide.web.redux.actions.UpdateSelectedPage
import it.unibo.tuprolog.ide.web.redux.actions.UpdateExecutionContext
import it.unibo.tuprolog.ide.web.redux.actions.UpdateStatus
import it.unibo.tuprolog.solve.TimeUnit
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.times
import it.unibo.tuprolog.ui.gui.Application
import it.unibo.tuprolog.ui.gui.DefaultJsRunner
import it.unibo.tuprolog.ui.gui.Event
import it.unibo.tuprolog.ui.gui.Page
import it.unibo.tuprolog.ui.gui.SolverEvent
import redux.RAction
import redux.Store
import redux.WrapperAction

object TuPrologController {

    // TODO move application into getter method and initialize in registerReduxStore

    public lateinit var application: Application
    private lateinit var store: Store<AppState, RAction, WrapperAction>

    private val catchAnyEvent: (Event<Any>) -> Unit = { console.log("[Controller] Missing event handler: ", it) }
    val logEvent: (Event<Any>) -> Unit = { console.log("[Controller] Received event: ", it) }

    fun registerReduxStore(store: Store<AppState, RAction, WrapperAction>) {
        this.store = store
    }

    fun bindApplication(application: Application) {
        this.application = application
        application.onStart.bind(catchAnyEvent)
        application.onError.bind{
            logEvent(it)
            store.dispatch(PageError(it.event.first, it.event.second))
        }
        application.onPageCreated.bind{
            logEvent(it)
            store.dispatch(UpdatePagesList(application.pages))
        }
        application.onPageLoaded.bind {
            logEvent(it)
            store.dispatch(UpdatePagesList(application.pages))
        }
        application.onPageClosed.bind {
            logEvent(it)
            store.dispatch(UpdatePagesList(application.pages))
        }
        application.onPageSelected.bind {
            logEvent(it)
            bindPage(it.event)
            store.dispatch(UpdateSelectedPage(application.currentPage))
        }
        application.onPageUnselected.bind {
            logEvent(it)
            unbindPage(it.event)
            store.dispatch(UpdateSelectedPage(null))
        }
        application.onQuit.bind(catchAnyEvent)
    }

    // TODO applicare l'unbind
    private fun bindPage(page: Page) {
        page.onResolutionStarted.bind {
            logEvent(it)
            console.log(page.state)
//            store.dispatch(UpdateStatus(page.state))
            store.dispatch(UpdateExecutionContext(it))
        }
        page.onResolutionOver.bind {
            logEvent(it)
            console.log(page.state)
//            store.dispatch(UpdateStatus(page.state))
            store.dispatch(UpdateExecutionContext(it))
        }
        page.onNewQuery.bind {
            logEvent(it)
            console.log(page.state)
//            store.dispatch(UpdateStatus(page.state))
            store.dispatch(UpdateExecutionContext(it))
        }
        page.onQueryOver.bind {
            logEvent(it)
            console.log(page.state)
//            store.dispatch(UpdateStatus(page.state))
            store.dispatch(UpdateExecutionContext(it))
        }
        page.onNewSolution.bind {
            logEvent(it)
            console.log(page.state)
            store.dispatch(NewSolution(it.event))
            store.dispatch(UpdateExecutionContext(it))
        }
        page.onStdoutPrinted.bind(catchAnyEvent)
        page.onStderrPrinted.bind(catchAnyEvent)
        page.onWarning.bind(catchAnyEvent)
        page.onNewSolver.bind(catchAnyEvent)
        page.onNewStaticKb.bind(catchAnyEvent)
        page.onSolveOptionsChanged.bind(catchAnyEvent)
        page.onSave.bind(catchAnyEvent)
        page.onReset.bind {
            logEvent(it)
            store.dispatch(ResetPage())
            store.dispatch(UpdateExecutionContext(it))
        }
    }

    private fun unbindPage(page: Page) {
        page.onResolutionStarted.unbind(catchAnyEvent)
        page.onResolutionOver.unbind(catchAnyEvent)
        page.onNewQuery.unbind(catchAnyEvent)
        page.onQueryOver.unbind(catchAnyEvent)
        page.onNewSolution.unbind(catchAnyEvent)
        page.onStdoutPrinted.unbind(catchAnyEvent)
        page.onStderrPrinted.unbind(catchAnyEvent)
        page.onWarning.unbind(catchAnyEvent)
        page.onNewSolver.unbind(catchAnyEvent)
        page.onNewStaticKb.unbind(catchAnyEvent)
        page.onSolveOptionsChanged.unbind(catchAnyEvent)
        page.onReset.unbind(catchAnyEvent)
    }
}