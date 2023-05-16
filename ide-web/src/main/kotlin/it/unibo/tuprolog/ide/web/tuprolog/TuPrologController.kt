package it.unibo.tuprolog.ide.web.tuprolog

import AppState
import it.unibo.tuprolog.ide.web.redux.actions.NewSolution
import it.unibo.tuprolog.ide.web.redux.actions.PageError
import it.unibo.tuprolog.ide.web.redux.actions.ResetPage
import it.unibo.tuprolog.ide.web.redux.actions.UpdateExecutionContext
import it.unibo.tuprolog.ide.web.redux.actions.UpdatePagesList
import it.unibo.tuprolog.ide.web.redux.actions.UpdateSelectedPage
import it.unibo.tuprolog.ide.web.redux.actions.UpdateStatus
import it.unibo.tuprolog.ide.web.redux.actions.*
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverBuilder
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TimeUnit
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.ui.gui.Application
import it.unibo.tuprolog.ui.gui.DefaultJsRunner
import it.unibo.tuprolog.ui.gui.Event
import it.unibo.tuprolog.ui.gui.Page
import redux.RAction
import redux.Store
import redux.WrapperAction
import it.unibo.tuprolog.solve.libs.io.IOLib

object TuPrologController {

    private val customSolverFactory: SolverFactory = ClassicSolverFactory
        .newBuilder()
        .withLibrary(IOLib) // open issue
        .toFactory()

    var application: Application =
        TuPrologApplication.of(
            DefaultJsRunner(),
            customSolverFactory,
            Page.DEFAULT_TIMEOUT
        )
    private lateinit var store: Store<AppState, RAction, WrapperAction>

    private val catchAnyEvent: (Event<Any>) -> Unit =
        { console.log("[Controller] Missing event handler: ", it) }
    private val logEvent: (Event<Any>) -> Unit =
        { console.log("[Controller] Received event: ", it) }

    fun initialize(store: Store<AppState, RAction, WrapperAction>) {
        this.store = store
        bindApplication(application)
        application.newPage()
    }

    fun bindApplication(application: Application) {
        this.application = application
        application.onStart.bind(catchAnyEvent)
        application.onError.bind {
            logEvent(it)
            store.dispatch(PageError(it.event.first, it.event.second))
        }
        application.onPageCreated.bind { it: Event<Page> ->
            logEvent(it)
            // TODO try to define additional libraries inside of the Application SolverFactory

            console.log(it.event.solverBuilder.runtime.toString())
            it.event.solverBuilder.withLibrary(IOLib)
            console.log(it.event.solverBuilder.runtime.toString())
            // open issue
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
            store.dispatch(UpdateExecutionContext(it))
        }
        page.onResolutionOver.bind {
            logEvent(it)
            store.dispatch(UpdateExecutionContext(it))
        }
        page.onNewQuery.bind {
            logEvent(it)
            store.dispatch(UpdateExecutionContext(it))
        }
        page.onQueryOver.bind {
            logEvent(it)
            store.dispatch(UpdateExecutionContext(it))
        }
        page.onNewSolution.bind {
            logEvent(it)
            store.dispatch(NewSolution(it.event))
            store.dispatch(UpdateExecutionContext(it))
        }
        page.onStateChanged.bind {
            logEvent(it)
            store.dispatch(UpdateStatus(it.event))
        }
        page.onStdoutPrinted.bind {
            logEvent(it)
            store.dispatch(StdOut(it.event))
        }
        page.onStderrPrinted.bind {
            logEvent(it)
            store.dispatch(StdErr(it.event))
        }
        page.onWarning.bind {
            logEvent(it)
            store.dispatch(Warnings(it.event))
        }
        page.onNewSolver.bind(catchAnyEvent)
        page.onNewStaticKb.bind(catchAnyEvent)
        page.onSolveOptionsChanged.bind(catchAnyEvent)
        page.onSave.bind({
            logEvent(it)
            // feedback
        })
        page.onRename.bind {
            logEvent(it)
            store.dispatch(UpdatePagesList(application.pages))
            store.dispatch(UpdateSelectedPage(application.currentPage))
        }
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