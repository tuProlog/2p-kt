package it.unibo.tuprolog.ide.web.redux.reducers

import Message
import State
import it.unibo.tuprolog.ide.web.redux.actions.AddMessage
import it.unibo.tuprolog.ide.web.redux.actions.RemoveMessage
import redux.RAction

fun messagesActions(state: State, action: RAction): List<Message> = when (action) {
    is AddMessage -> {
        state.messages + Message(action.text, action.color)
        state.messages
    }
    is RemoveMessage -> {
//        state.filter { m -> m.id == action.id }
        state.messages
    }
    else -> state.messages
}