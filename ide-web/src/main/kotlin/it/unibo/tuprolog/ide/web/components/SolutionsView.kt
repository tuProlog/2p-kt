package it.unibo.tuprolog.ide.web.components

import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TimeOutException
import mui.icons.material.CancelOutlined
import mui.icons.material.CheckCircleOutline
import mui.icons.material.ErrorOutlineOutlined
import mui.icons.material.TimerOffOutlined
import mui.material.ListItem
import mui.material.ListItemIcon
import mui.material.ListItemText
import mui.material.SvgIconColor
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span

val formatter = TermFormatter.prettyExpressions()

external interface ViewProps : Props {
    var solution: Solution
}

fun getYesSubstitutions(solution: Solution.Yes): List<String> {
    return solution.substitution.map {
        it.key.format(formatter) + ": " + it.value.format(formatter)
    }
}

fun getYesText(solution: Solution.Yes): String {
    return solution.solvedQuery.format(formatter)
}

val YesView = FC<ViewProps> { props ->
    ListItem {
        ListItemIcon {
            CheckCircleOutline {
                color = SvgIconColor.success
            }
        }
        ListItemText {
            primary =
                ReactNode("Yes: " + getYesText(props.solution.castToYes()))
            secondary = ReactNode(
                getYesSubstitutions(props.solution.castToYes())
                    .joinToString(", ")
            )
        }
    }
}

val NoView = FC<ViewProps> {
    ListItem {
        ListItemIcon {
            CancelOutlined {
                color = SvgIconColor.disabled
            }
        }
        ListItemText {
            primary = ReactNode("No.")
        }
    }
}

val HaltView = FC<ViewProps> { props ->
    ListItem {
        if (props.solution.castToHalt().exception is TimeOutException) {
            ListItemIcon {
                TimerOffOutlined {
                    color = SvgIconColor.warning
                }
            }
            ListItemText {
                primary = ReactNode("Timeout.")
            }
        } else {
            ListItemIcon {
                ErrorOutlineOutlined {
                    color = SvgIconColor.error
                }
            }
            ListItemText {
                primary = ReactNode("Halt:")
                secondary = HaltViewMessage.create { solution = props.solution }
            }
        }
    }
}

val HaltViewMessage = FC<ViewProps> { props ->
    p {
        span {
            +props.solution.exception?.message
        }
        br {}
        span {
            +props.solution.exception?.logicStackTrace?.joinToString(", ")
        }
    }
}
