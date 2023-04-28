import js.uri.encodeURIComponent
import react.Reducer
import redux.RAction
import redux.createStore
import redux.rEnhancer
import web.html.HTML
import kotlin.js.Date
import kotlin.reflect.KProperty1
import web.dom.document

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
class ChangeSelectedTab(val newValue: String = "") : RAction
class RemoveEditorTab : RAction
class RenameEditor(val newName: String = "") : RAction
class DownloadTheory : RAction
class UpdateEditorTheory(val newTheory: String = "") : RAction
class OnFileLoad(val fileName: String = "", val editorValue: String = "") : RAction

const val MYCOUNTER = 10
const val MYCOUNTER2 = 10

// Gestione di azioni
fun counterActions(state: Counter = Counter(MYCOUNTER), action: RAction): Counter = when (action) {
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

fun tuPrologActions(state: TuProlog, action: RAction): TuProlog = when (action) {
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

    is RemoveEditorTab -> {
        if (state.editorTabs.size > 1) {
            // find the deletable tab panel index
            val index = state.editorTabs.indexOfFirst { it.fileName == state.editorSelectedTab }
            state.editorTabs.removeAt(index)
            // select new ide
            if (index == 0)
                state.editorSelectedTab = state.editorTabs[index].fileName
            else
                state.editorSelectedTab = state.editorTabs[index - 1].fileName
        }
        state
    }

    is DownloadTheory -> {
        val editorText = state.editorTabs.find { it2 -> it2.fileName == state.editorSelectedTab }?.editorValue ?: ""
        if (editorText != "") {
            val elem = document.createElement(HTML.a)
            elem.setAttribute("href", "data:text/plain;charset=utf-8," + encodeURIComponent(editorText))
            elem.setAttribute("download", state.editorSelectedTab)
            elem.click()
//            isErrorAlertOpen = false
        }
//        else {
//            errorAlertMessage = "No theory specified"
//            isErrorAlertOpen = true
//        }
        state
    }

    is RenameEditor -> {
        val isOk: EditorTab? = state.editorTabs.find { it3 -> it3.fileName == action.newName }
        if (isOk == null) {
            val indexForRename = state.editorTabs.indexOfFirst { it3 -> it3.fileName == state.editorSelectedTab }
            state.editorTabs[indexForRename].fileName = action.newName
            state.editorSelectedTab = state.editorTabs[indexForRename].fileName
//            isErrorAlertOpen = false
        }
//        else {
//            errorAlertMessage = if (it != editorSelectedTab)
//                "Cannot rename file. A file with this name already exists"
//            else
//                "Cannot rename file with the same value"
//            isErrorAlertOpen = true
//        }
        state
    }

    is ChangeSelectedTab -> {
        state.editorSelectedTab = action.newValue
        state
    }

    is UpdateEditorTheory -> {
        state.editorTabs.find { it2 -> it2.fileName == state.editorSelectedTab }?.editorValue = action.newTheory
        state
    }


    is OnFileLoad -> {
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

val DEMO_EDITORS = mutableListOf(
    EditorTab(
        "Test1.pl", """
            % member2(List, Elem, ListWithoutElem)
            member2([X|Xs],X,Xs).
            member2([X|Xs],E,[X|Ys]):-member2(Xs,E,Ys).
            % permutation(Ilist, Olist)
            permutation([],[]).
            permutation(Xs, [X | Ys]) :-
            member2(Xs,X,Zs),
            permutation(Zs, Ys).
    
            % permutation([10,20,30],L).
        """
    ),
    EditorTab(
        "Test2.pl", """
            nat(z).
            nat(s(X)) :- nat(X).

            % nat(N).
        """
    ),
    EditorTab(
        "Test3.pl", """
            increment(A, B, C) :- C is A + B.
            
            % increment(1,2,X).
        """
    )
)

val myStore = createStore(
    ::rootReducer,
    State(
        Counter(MYCOUNTER2),
        TuProlog("Test1.pl", "", DEMO_EDITORS)
    ),
    rEnhancer()
)
