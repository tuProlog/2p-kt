package it.unibo.tuprolog.ide.web.redux

import it.unibo.tuprolog.ide.web.tuprolog.TPPage
import it.unibo.tuprolog.ide.web.tuprolog.TPPageStatus
import it.unibo.tuprolog.ide.web.tuprolog.TPSolution
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.ui.gui.PageID
import redux.RAction
import redux.createStore
import redux.rEnhancer

data class PageWrapper(
    var id: PageID,
    var theory: String,
    var query: String,
    var solutions: Collection<TPSolution>,
    var executionContext: ExecutionContextAware?,
    var pageStatus: TPPageStatus?,
    var pageException: Throwable?,
    var stdOutMessage: String,
    var stdErrMessage: String,
    var warningMessage: Warning?,
    var stdInMessage: String,
) {
    companion object {
        fun fromPage(page: TPPage): PageWrapper {
            return PageWrapper(
                page.id,
                page.theory,
                page.query,
                emptyList(),
                null,
                page.state,
                null,
                "",
                "",
                null,
                page.stdin
            )
        }
    }
}

data class TuProlog(
    var pages: MutableMap<TPPage, PageWrapper>,
    var currentPage: PageWrapper?,
)

data class AppState(
    var tuProlog: TuProlog
)

fun rootReducer(
    state: AppState,
    action: Any
) = AppState(
    tuPrologReducer(state.tuProlog, action.unsafeCast<RAction>())
)

val myStore = createStore(
    ::rootReducer,
    AppState(
        TuProlog(
            mutableMapOf(),
            null
        )
    ),
    rEnhancer()
)
