package it.unibo.tuprolog.ide.web.components

import mui.material.Button
import mui.material.ButtonVariant
import mui.material.FormControlVariant
import mui.material.Stack
import mui.material.StackDirection
import mui.material.TextField
import mui.system.responsive
import react.FC
import react.Props
import react.ReactNode
import react.createRef
import react.dom.onChange
import react.useState
import web.html.HTMLInputElement

external interface QueryEditor : Props {
    var onSolve: (query: String) -> Unit
    var onSolveAll: () -> Unit
    var onStop: () -> Unit
    var onReset: () -> Unit
}

val QueryEditorComponent = FC<QueryEditor> { props ->
    var editorQuery by useState("")
    val inputRef2 = createRef<HTMLInputElement>()

    Stack {
        direction = responsive(StackDirection.row)
        TextField {
            id = "query"
            label = ReactNode("Query")
            variant = FormControlVariant.outlined
            fullWidth = true
            inputRef = inputRef2
            onChange = {
                inputRef2.current?.let { it1 ->
                    editorQuery = it1.value
                }
            }
        }
        Button {
            variant = ButtonVariant.contained
            +"Solve"
            onClick = {
                props.onSolve(editorQuery)
            }
        }
        Button {
            variant = ButtonVariant.contained
            +"Solve All"
        }
        Button {
            variant = ButtonVariant.contained
            disabled = true
            +"Stop"
        }
        Button {
            variant = ButtonVariant.contained
            +"Reset"
        }
    }
}
