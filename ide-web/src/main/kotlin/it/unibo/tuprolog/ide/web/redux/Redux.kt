import it.unibo.tuprolog.ide.web.redux.appActionsReducer
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
    val query: String,
    var solutions: Collection<TuPrologSolution>,
    var executionContext: ExecutionContextAware?,
    var pageStatus: Page.Status?,
    var pageException: Throwable?,
    var stdOutMessage: String,
    var stdErrMessage: String,
    var warningMessage: Warning?,
    var stdInMessage: String,
) {
    fun nameByID(): String {
        return id.name
    }

    companion object {
        fun fromPage(page: Page): PageWrapper {
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
                page.stdin)
        }
    }
}

data class AppState(
    var pages: Map<Page, PageWrapper>,
    var currentPage: PageWrapper?,
)

fun rootReducer(
    state: AppState,
    action: Any
) = appActionsReducer(state, action.unsafeCast<RAction>())

val myStore = createStore(
    ::rootReducer,
    AppState(
        emptyMap(),
        null
    ),
    rEnhancer()
)
