package it.unibo.tuprolog.ide.web.tuprolog

import it.unibo.tuprolog.ide.web.appConfig
import it.unibo.tuprolog.ide.web.redux.AddPage
import it.unibo.tuprolog.ide.web.redux.AppState
import it.unibo.tuprolog.ide.web.redux.NewSolution
import it.unibo.tuprolog.ide.web.redux.PageError
import it.unibo.tuprolog.ide.web.redux.RemovePage
import it.unibo.tuprolog.ide.web.redux.ResetPage
import it.unibo.tuprolog.ide.web.redux.StdErr
import it.unibo.tuprolog.ide.web.redux.StdOut
import it.unibo.tuprolog.ide.web.redux.UpdateExecutionContext
import it.unibo.tuprolog.ide.web.redux.UpdatePageName
import it.unibo.tuprolog.ide.web.redux.UpdateQuery
import it.unibo.tuprolog.ide.web.redux.UpdateSelectedPage
import it.unibo.tuprolog.ide.web.redux.UpdateStatus
import it.unibo.tuprolog.ide.web.redux.UpdateTheory
import it.unibo.tuprolog.ide.web.redux.Warnings
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.ui.gui.DefaultJsRunner
import it.unibo.tuprolog.ui.gui.Event
import it.unibo.tuprolog.utils.observe.Binding
import redux.RAction
import redux.Store
import redux.WrapperAction

object TuPrologController {

    private val customSolverFactory: SolverFactory = ClassicSolverFactory
        .newBuilder()
        .withLibrary(IOLib) // open issue
        .toFactory()

    var application: TPApplication =
        TPApplication.of(
            DefaultJsRunner(),
            customSolverFactory,
            TPPage.DEFAULT_TIMEOUT
        )

    private var activePageBindings: List<Binding> = emptyList()

    private lateinit var store: Store<AppState, RAction, WrapperAction>

    private val catchAnyEvent: (Event<Any>) -> Unit =
        { if (appConfig.logEvents) console.log("[Controller] Missing event handler: ", it) }
    private val logEvent: (Event<Any>) -> Unit =
        { if (appConfig.logEvents) console.log("[Controller] Received event: ", it) }

    fun initialize(store: Store<AppState, RAction, WrapperAction>) {
        this.store = store
        bindApplication(application)
        application.newPage()
    }

    private fun bindApplication(application: TPApplication) {
        this.application = application
        application.onStart.bind(catchAnyEvent)
        application.onError.bind {
            logEvent(it)
            store.dispatch(PageError(it.event.first, it.event.second))
        }
        application.onPageCreated.bind {
            logEvent(it)
            it.event.solverBuilder.withLibrary(IOLib)
            store.dispatch(AddPage(it.event))
        }
        application.onPageLoaded.bind {
            logEvent(it)
            store.dispatch(AddPage(it.event))
        }
        application.onPageClosed.bind {
            logEvent(it)
            store.dispatch(RemovePage(it.event))
        }
        application.onPageSelected.bind {
            logEvent(it)
            bindPage(it.event)
            store.dispatch(UpdateSelectedPage(application.currentPage))
        }
        application.onPageUnselected.bind {
            logEvent(it)
            unbindPage()
            store.dispatch(UpdateSelectedPage(null))
        }
        application.onQuit.bind(catchAnyEvent)
    }

    private fun bindPage(page: TPPage) {
        activePageBindings += page.onResolutionStarted.bind(catchAnyEvent)
        activePageBindings += page.onResolutionOver.bind {
            logEvent(it)
            store.dispatch(UpdateExecutionContext(it))
        }
        activePageBindings += page.onQueryChanged.bind {
            logEvent(it)
            store.dispatch(UpdateQuery(it.event))
        }
        activePageBindings += page.onTheoryChanged.bind {
            logEvent(it)
            store.dispatch(UpdateTheory(it.event))
        }
        activePageBindings += page.onNewQuery.bind {
            logEvent(it)
            store.dispatch(UpdateExecutionContext(it))
        }
        activePageBindings += page.onQueryOver.bind {
            logEvent(it)
            store.dispatch(UpdateExecutionContext(it))
        }
        activePageBindings += page.onNewSolution.bind {
            store.dispatch(NewSolution(it.event))
            store.dispatch(UpdateExecutionContext(it))
        }
        activePageBindings += page.onStateChanged.bind {
            logEvent(it)
            store.dispatch(UpdateStatus(it.event))
        }
        activePageBindings += page.onStdoutPrinted.bind {
            logEvent(it)
            store.dispatch(StdOut(it.event))
        }
        activePageBindings += page.onStderrPrinted.bind {
            logEvent(it)
            store.dispatch(StdErr(it.event))
        }
        activePageBindings += page.onWarning.bind {
            logEvent(it)
            store.dispatch(Warnings(it.event))
        }
        activePageBindings += page.onNewSolver.bind {
            logEvent(it)
            store.dispatch(UpdateExecutionContext(it))
        }
        activePageBindings += page.onNewStaticKb.bind {
            logEvent(it)
            store.dispatch(UpdateExecutionContext(it))
        }
        activePageBindings += page.onSolveOptionsChanged.bind(catchAnyEvent)
        activePageBindings += page.onSave.bind(catchAnyEvent)
        activePageBindings += page.onRename.bind {
            logEvent(it)
            store.dispatch(UpdatePageName(it.event.second))
        }
        activePageBindings += page.onReset.bind {
            logEvent(it)
            store.dispatch(ResetPage())
            store.dispatch(UpdateExecutionContext(it))
        }
    }

    private fun unbindPage() {
        activePageBindings.forEach { it.unbind() }
        activePageBindings = emptyList()
    }
}
