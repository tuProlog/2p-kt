import react.Reducer
import react.redux.Provider
import react.redux.useDispatch
import react.redux.useSelector
import redux.RAction
import redux.compose
import redux.createStore
import redux.rEnhancer
import kotlin.reflect.KProperty1

// Utils
fun <S, A, R> combineReducers(reducers: Map<KProperty1<S, R>, Reducer<*, A>>): Reducer<S, A> {
    return redux.combineReducers(reducers.mapKeys { it.key.name })
}

// Stato
data class Counter(var count: Int)

data class State(
    val counter: Counter = Counter(10)



)

// Azioni disponibili

class Increase : RAction

class Decrease : RAction

// Gestione di azioni
fun counter(counter: Counter = Counter(10), action: RAction): Counter = when (action) {
    is Increase -> {
        counter.count++
        counter
    }

    is Decrease -> {
        counter.count--
        counter
    }

    else -> counter
}
// Redux Store

fun rootReducer(
    state: State,
    action: Any
) = State(
    counter(state.counter, action.unsafeCast<RAction>()),
)

val myStore = createStore(
    ::rootReducer,
    State(),
    rEnhancer()
)