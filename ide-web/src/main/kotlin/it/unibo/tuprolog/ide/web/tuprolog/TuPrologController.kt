package it.unibo.tuprolog.ide.web.tuprolog

import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.ui.gui.Application
import it.unibo.tuprolog.ui.gui.DefaultJsRunner
import it.unibo.tuprolog.ui.gui.Event
import it.unibo.tuprolog.ui.gui.Page

class TuPrologController {

    val application = Application.of(DefaultJsRunner(), ClassicSolverFactory, defaultTimeout = 1000L)

    val catchAnyEvent: (Event<Any>) -> Unit = { console.log("Missing event: ", it) }
    fun bindApplication() {
        application.onStart.bind(catchAnyEvent)
        application.onError.bind(catchAnyEvent)
        application.onPageCreated.bind(catchAnyEvent)
        application.onPageLoaded.bind(catchAnyEvent)
        application.onPageClosed.bind(catchAnyEvent)
        application.onPageSelected.bind {
            bindPage(it.event)
        }
        application.onPageUnselected.bind(catchAnyEvent)
        application.onQuit.bind(catchAnyEvent)
    }

    private fun bindPage(page: Page) {
        page.onResolutionStarted.bind(catchAnyEvent)
        page.onResolutionOver.bind(catchAnyEvent)
        page.onNewQuery.bind(catchAnyEvent)
        page.onQueryOver.bind(catchAnyEvent)
        page.onNewSolution.bind(catchAnyEvent)
        page.onStdoutPrinted.bind(catchAnyEvent)
        page.onStderrPrinted.bind(catchAnyEvent)
        page.onWarning.bind(catchAnyEvent)
        page.onNewSolver.bind(catchAnyEvent)
        page.onNewStaticKb.bind(catchAnyEvent)
        page.onSolveOptionsChanged.bind(catchAnyEvent)
        page.onReset.bind(catchAnyEvent)
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