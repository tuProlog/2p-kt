import react.Reducer
import redux.RAction
import redux.createStore
import redux.rEnhancer
import kotlin.js.Date
import kotlin.reflect.KProperty1

// Utils
fun <S, A, R> combineReducers(reducers: Map<KProperty1<S, R>, Reducer<*, A>>): Reducer<S, A> {
    return redux.combineReducers(reducers.mapKeys { it.key.name })
}

class EditorTab(var fileName: String, var editorValue: String)


// Stato
data class Counter(var count: Int)
data class TuProlog(var editorSelectedTab: String, var editorQuery: String, var editorTabs: MutableList<EditorTab>)

data class State(
    val counter: Counter,
    var tuProlog: TuProlog,
)
// Azioni disponibili

class Increase : RAction
class Decrease : RAction
class AddEditorTab(val content: String = "") : RAction
class OnFileLoad(val fileName: String = "", val editorValue: String = "") : RAction


// Gestione di azioni
fun counterActions(state: Counter = Counter(10), action: RAction): Counter = when (action) {
    is Increase -> {
        state.count++
        state
    }

    is Decrease -> {
        state.count--
        state
    }

    else -> state
}

fun tuPrologActions(state: TuProlog = TuProlog("", "", mutableListOf()), action: RAction): TuProlog = when (action) {
    is AddEditorTab -> {
        val fileName: String = "undefined_" + Date().getTime() + ".pl"
        state.editorTabs.add(
            EditorTab(
                fileName, action.content.trimIndent()
            )
        )
        state.editorSelectedTab = fileName
        state
    }

    is OnFileLoad -> {
        console.log(action.fileName, action.editorValue)
        if (state.editorTabs.find { it.fileName == action.fileName } == null) {
            state.editorTabs.add(EditorTab(action.fileName, action.editorValue))
        } else {
//            errorAlertMessage = "File already exists"
//            isErrorAlertOpen = true
        }
        state.editorSelectedTab = action.fileName
        state
    }

    else -> state
}

// Redux Store

fun rootReducer(
    state: State,
    action: Any
) = State(
    counterActions(state.counter, action.unsafeCast<RAction>()),
    tuPrologActions(state.tuProlog, action.unsafeCast<RAction>()),
)

val myStore = createStore(
    ::rootReducer,
    State(
        Counter(10),
        TuProlog("", "", mutableListOf())
    ),
    rEnhancer()
)