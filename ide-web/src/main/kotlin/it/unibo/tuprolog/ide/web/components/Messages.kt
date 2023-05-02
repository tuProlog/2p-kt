package it.unibo.tuprolog.ide.web.components

import Message
import AppState
import csstype.integer
import csstype.Position.Companion.absolute
import csstype.em
import emotion.react.css
import mui.material.Alert
import mui.material.Button
import mui.material.Stack
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.redux.useSelector

val Messages = FC<Props> {

    val messages = useSelector<AppState, List<Message>> { s -> s.messages }


    ReactHTML.div {
        css {
            position = absolute
            bottom = 1.em
            right = 1.em
            zIndex = integer(10)
        }

        Stack {
            messages.forEach { m ->
                Alert {
                    severity = m.color
                    +m.text
                }
            }

            Button {
                + "Hide all"
            }
        }
    }
}
