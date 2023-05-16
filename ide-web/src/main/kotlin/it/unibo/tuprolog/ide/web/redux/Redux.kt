import it.unibo.tuprolog.ide.web.redux.reducers.tuPrologActions
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologPage
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologSolution
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.ui.gui.Page
import it.unibo.tuprolog.ui.gui.PageID
import redux.RAction
import redux.createStore
import redux.rEnhancer


data class PageWrapper(
    var id: PageID,
    val theory: String,
//    val query: String,
//    val solutions AddSolution(pageId, solution), CleanSolutions(pageId)
//    val execution context UpdateExecutionContext(pageId, context), CleanExecutionContext(pageId)
//    val page status // updateStatus(newStatus: string)
//    val exceptions
) {
    fun nameByID(): String {
        return id.name
    }

    companion object {
        fun fromPage(page: Page): PageWrapper {
            return PageWrapper(page.id, page.theory)
        }
    }
}

// TODO simulate multitabs by wrapping page specific variables into TuProlog:pages collection

data class TuProlog(
    var pages: Collection<PageWrapper>,
    var currentPage: TuPrologPage?,
    var solutions: Collection<TuPrologSolution>,
    var executionContext: ExecutionContextAware?,
    var pageStatus: Page.Status?,
    var pageException: Throwable?,
    var stdOutMessage: String,
    var stdErrMessage: String,
    var warningMessage: Warning?,
    var stdInMessage: String
)

data class AppState(
    var tuProlog: TuProlog,
)


fun rootReducer(
    state: AppState,
    action: Any
) = AppState(
    tuPrologActions(state, action.unsafeCast<RAction>()),
)

val myStore = createStore(
    ::rootReducer,
    AppState(
        TuProlog(
            emptyList(),
            null,
            emptyList(),
            null,
            null,
            null,
            "",
            "",
            null,
            ""
            ),
    ),
    rEnhancer()
)
