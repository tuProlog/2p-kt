import it.unibo.tuprolog.ide.web.redux.reducers.messagesActions
import it.unibo.tuprolog.ide.web.redux.reducers.tuPrologActions
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologPage
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologSolution
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.ui.gui.Page
import redux.RAction
import redux.createStore
import redux.rEnhancer
import mui.material.AlertColor

// Stato
class Message(var text: String, var color: AlertColor) {} //, var id:UUID
data class TuProlog(
    var pages: Collection<TuPrologPage>,
    var currentPage: TuPrologPage?,
    var solutions: Collection<TuPrologSolution>,
    var executionContext: ExecutionContextAware?,
    var pageStatus: Page.Status,
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
        TuProlog(emptyList(), null, emptyList(), null, Page.Status.IDLE, null),
        emptyList()
    ),
    rEnhancer()
)
