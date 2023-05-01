package it.unibo.tuprolog.ide.web.components

import Message
import State
import csstype.Position
import csstype.Position.Companion.absolute
import csstype.em
import emotion.react.css
import mui.material.Alert
import mui.material.Snackbar
import mui.material.Stack
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.redux.useSelector

val Messages = FC<Props> {

    val messages = useSelector<State, List<Message>> { s -> s.messages }


    ReactHTML.div {
        css {
            position = absolute
            top = 0.em
            right = 0.em
        }


        Stack {
            messages.forEach { m ->
                Snackbar {
                    autoHideDuration = 5000
//                    onClose = { _, _ -> isErrorAlertOpen = false }

                    Alert {
                        severity = m.color
                        +m.text
                    }
                    // TODO change snack-bar anchor
                }
            }
        }
    }
}
