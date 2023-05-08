import it.unibo.tuprolog.ide.web.redux.reducers.messagesActions
import it.unibo.tuprolog.ide.web.redux.reducers.tuPrologActions
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologPage
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologSolution
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

//val DEMO_EDITORS = mutableListOf(
//    EditorTab(
//        "Test1.pl", """
//            % member2(List, Elem, ListWithoutElem)
//            member2([X|Xs],X,Xs).
//            member2([X|Xs],E,[X|Ys]):-member2(Xs,E,Ys).
//            % permutation(Ilist, Olist)
//            permutation([],[]).
//            permutation(Xs, [X | Ys]) :-
//            member2(Xs,X,Zs),
//            permutation(Zs, Ys).
//
//            % permutation([10,20,30],L).
//        """
//    ),
//    EditorTab(
//        "Test2.pl", """
//            nat(z).
//            nat(s(X)) :- nat(X).
//
//            % nat(N).
//        """
//    ),
//    EditorTab(
//        "Test3.pl", """
//            increment(A, B, C) :- C is A + B.
//
//            % increment(1,2,X).
//        """
//    )
//)

val myStore = createStore(
    ::rootReducer,
    AppState(
        TuProlog(emptyList(), null, emptyList(), Page.Status.IDLE, null),
        emptyList()
    ),
    rEnhancer()
)
