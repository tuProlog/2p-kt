package it.unibo.tuprolog.ide.web.redux.reducers

import Message
import AppState
import it.unibo.tuprolog.ide.web.redux.actions.AddMessage
import it.unibo.tuprolog.ide.web.redux.actions.MessageType
import it.unibo.tuprolog.ide.web.redux.actions.RemoveMessage
import it.unibo.tuprolog.ide.web.redux.actions.TypedMessage
import mui.material.AlertColor
import redux.RAction

fun messagesActions(state: AppState, action: RAction): List<Message> = when (action) {
    is AddMessage -> {
        state.messages + Message(action.text, action.color)
    }
    is RemoveMessage -> {
//        state.filter { m -> m.id == action.id }
        state.messages
    }

    is TypedMessage -> {
        val color: AlertColor = when (action.type) {
            MessageType.WARNING -> AlertColor.warning
            MessageType.ERROR -> AlertColor.error
            MessageType.SUCCESS -> AlertColor.success
            else -> AlertColor.info
        }
        state.messages + Message(action.text, color)
    }

    else -> state.messages
}