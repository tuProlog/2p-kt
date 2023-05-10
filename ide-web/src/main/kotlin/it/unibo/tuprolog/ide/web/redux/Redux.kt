import it.unibo.tuprolog.ide.web.redux.reducers.messagesActions
import it.unibo.tuprolog.ide.web.redux.reducers.tuPrologActions
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologPage
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologSolution
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.TimeUnit
import it.unibo.tuprolog.solve.times
import it.unibo.tuprolog.ui.gui.Page
import redux.RAction
import redux.createStore
import redux.rEnhancer
import mui.material.AlertColor

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
    var messages: List<Message>
)


fun rootReducer(
    state: AppState,
    action: Any
) = AppState(
    tuPrologActions(state, action.unsafeCast<RAction>()),
    messagesActions(state, action.unsafeCast<RAction>())
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
            null),
        emptyList()
    ),
    rEnhancer()
)
