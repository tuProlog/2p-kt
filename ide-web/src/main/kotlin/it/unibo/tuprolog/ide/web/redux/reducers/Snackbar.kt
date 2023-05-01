package it.unibo.tuprolog.ide.web.redux.reducers

import Message
import it.unibo.tuprolog.ide.web.redux.actions.AddMessage
import it.unibo.tuprolog.ide.web.redux.actions.RemoveMessage
import redux.RAction

fun messagesActions(state: List<Message>, action: RAction): List<Message> = when (action) {
    is AddMessage -> {
        state + Message(action.text, action.color)
        state
    }
    is RemoveMessage -> {
//        state.filter { m -> m.id == action.id }
        state
    }
    else -> state
}