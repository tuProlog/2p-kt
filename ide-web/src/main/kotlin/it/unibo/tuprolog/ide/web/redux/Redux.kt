import it.unibo.tuprolog.ide.web.redux.reducers.tuPrologActions
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologPage
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologSolution
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.ui.gui.Page
import mui.material.AlertColor
import redux.RAction
import redux.createStore
import redux.rEnhancer

// Stato
class Message(var text: String, var color: AlertColor) {} //, var id:UUID
data class TuProlog(
    var pages: Collection<TuPrologPage>, // Map PageId, pageName
    var currentPage: TuPrologPage?, // PageId
    var solutions: Collection<TuPrologSolution>, // Collection<TuPrologSolution>
    var executionContext: ExecutionContextAware?, // ExecutionContextAware?
    var pageStatus: Page.Status?,
    var pageException: Throwable?
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
            null
        )
    ),
    rEnhancer()
)
