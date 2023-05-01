import it.unibo.tuprolog.ide.web.redux.reducers.messagesActions
import it.unibo.tuprolog.ide.web.redux.reducers.tuPrologActions
import react.Reducer
import redux.RAction
import redux.createStore
import redux.rEnhancer
import mui.material.AlertColor

// Stato
class Message(var text: String, var color: AlertColor) //, var id:UUID
class EditorTab(var fileName: String, var editorValue: String)
data class TuProlog(var editorSelectedTab: String, var editorQuery: String, var editorTabs: MutableList<EditorTab>)

data class State(
    var tuProlog: TuProlog,
    var messages: List<Message>
)


fun rootReducer(
    state: State,
    action: Any
) = State(
    tuPrologActions(state.tuProlog, action.unsafeCast<RAction>()),
    messagesActions(state.messages, action.unsafeCast<RAction>())
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
        TuProlog("Test1.pl", "", DEMO_EDITORS),
        emptyList()
    ),
    rEnhancer()
)
