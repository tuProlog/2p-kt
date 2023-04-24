package it.unibo.tuprolog.ide.web

import mui.material.*
import mui.system.responsive
import react.FC
import react.Props
import react.ReactNode


external interface QueryEditorProps : Props {
    var onSolve: () -> Unit
    var onSolveAll: () -> Unit
    var onStop: () -> Unit
    var onReset: () -> Unit
}

val QueryEditor = FC<QueryEditorProps> {
    Stack {
        direction = responsive(StackDirection.row)
        TextField {
            id = "query"
            label = ReactNode("Query")
            variant = FormControlVariant.outlined
            fullWidth = true
        }
        Button {
            variant = ButtonVariant.contained
            +"Solve"
            onClick = {}
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